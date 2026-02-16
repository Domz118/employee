package com.hris.employee.rateLimiter;

import com.hris.employee.configuration.jwt.RateLimiterFilter;
import com.hris.employee.entity.User;
import com.hris.employee.repository.UserRepository;
import com.hris.employee.security.UserRateLimiterService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RateLimiterFilterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRateLimiterService rateLimiterService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RateLimiterFilter rateLimiterFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    //  ALLOWED REQUEST

    @Test
    @DisplayName("Should continue filter chain when user is within rate limit")
    void shouldContinueFilterWhenUserAllowed() throws ServletException, IOException {

        String username = "user";

        // Mock authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null)
        );

        User user = new User();
        user.setId(2L);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(rateLimiterService.tryConsume(user))
                .thenReturn(true);

        // Call correct method (NOT doFilterInternal)
        rateLimiterFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    }


    //  RATE LIMIT EXCEEDED

    @Test
    @DisplayName("Should return 429 when rate limit exceeded")
    void shouldReturn429WhenRateLimitExceeded() throws ServletException, IOException {

        String username = "user";

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null)
        );

        User user = new User();
        user.setId(2L);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(rateLimiterService.tryConsume(user))
                .thenReturn(false);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        rateLimiterFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        verify(filterChain, never()).doFilter(request, response);

        writer.flush();
        assertTrue(stringWriter.toString().contains("Rate limit exceeded"));
    }


    //NO AUTHENTICATION
    @Test
    @DisplayName("Should continue filter chain when no authentication present")
    void shouldContinueWhenNoAuthentication() throws ServletException, IOException {

        SecurityContextHolder.clearContext();

        rateLimiterFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(rateLimiterService);
    }
}

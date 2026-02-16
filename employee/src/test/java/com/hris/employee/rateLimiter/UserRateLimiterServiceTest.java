package com.hris.employee.rateLimiter;

import com.hris.employee.entity.User;
import com.hris.employee.security.UserRateLimiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRateLimiterServiceTest {

    private UserRateLimiterService rateLimiterService;
    private User user;

    @BeforeEach
    void setUp() {
        rateLimiterService = new UserRateLimiterService();
        user = new User();
        user.setId(1L);
    }

    @Test
    void shouldAllowFirstFiveRequests() {
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiterService.tryConsume(user));
        }
    }

    @Test
    void shouldBlockSixthRequest() {
        for (int i = 0; i < 6; i++) {
            rateLimiterService.tryConsume(user);
        }

        assertFalse(rateLimiterService.tryConsume(user));
    }

    @Test
    void shouldThrowExceptionWhenUserIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> rateLimiterService.tryConsume(null));
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        User userWithoutId = new User();
        assertThrows(IllegalArgumentException.class,
                () -> rateLimiterService.tryConsume(userWithoutId));
    }
}


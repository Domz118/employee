package com.hris.employee.services;

import com.hris.employee.entity.User;
import com.hris.employee.exception.handler.InputFieldException;
import com.hris.employee.repository.UserRepository;
import com.hris.employee.service.AuthenticationServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("admin@gmail.com");
    }

    @Test
    void passwordReset_Success() {

        String email = "admin@gmail.com";
        String password = "123456";
        String password2 = "123456";

        when(userRepository.getUserByEmail(email, User.class))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(password))
                .thenReturn("encodedPassword");

        when(passwordEncoder.encode(password2))
                .thenReturn("encodedResetCode");

        when(userRepository.save(user)).thenReturn(user);

        String result = authenticationService.passwordReset(email, password, password2);

        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
        assertEquals("encodedResetCode", user.getPassword());
        assertEquals("encodedResetCode", user.getPasswordResetCode());
    }

    @Test
    void passwordReset_EmailNotFound_ShouldThrowException() {

        when(userRepository.getUserByEmail(anyString(), eq(User.class)))
                .thenReturn(Optional.empty());

        InputFieldException exception = assertThrows(
                InputFieldException.class,
                () -> authenticationService.passwordReset(
                        "wrong@mail.com", "123", "123"
                )
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(userRepository, never()).save(any());
    }


}

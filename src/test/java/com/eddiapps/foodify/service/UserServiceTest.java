package com.eddiapps.foodify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.eddiapps.foodify.dto.LoginRequest;
import com.eddiapps.foodify.dto.LoginResponse;
import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.exception.InvalidCredentialsException;
import com.eddiapps.foodify.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private UserService userService;

    @Test
    void loginSuccessful() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("eddi@web.de");
        loginRequest.setPassword("Secret123");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("eddi@web.de");
        user.setPasswordHash("hashed-password");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("test-token");

        LoginResponse loginResponse = userService.login(loginRequest);

        assertEquals("test-token", loginResponse.getToken());
        assertEquals("Bearer", loginResponse.getTokenType());
    }

    @Test
    void wrongEmail() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("eddo@web.de");
        loginRequest.setPassword("Secret123");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(loginRequest)
        );
    }

    @Test
    void wrongPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("eddi@web.de");
        loginRequest.setPassword("Secret1234");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("eddi@web.de");
        user.setPasswordHash("hashed-password");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(loginRequest)
        );
    }
}

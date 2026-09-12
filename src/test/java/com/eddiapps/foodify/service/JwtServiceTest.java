package com.eddiapps.foodify.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.eddiapps.foodify.entity.UserEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.JwtException;

public class JwtServiceTest {

    private static final String TEST_SECRET =
            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMinutes", 15L);
    }

    @Test
    void generateTokenAndExtractEmail() {
        UserEntity user = new UserEntity();
        user.setEmail("eddi@test.de");

        String jwt = jwtService.generateToken(user);
        String extractedEmail = jwtService.extractEmail(jwt);
        assertEquals("eddi@test.de", extractedEmail);
    }

    @Test
    void manipulateJwt() {
        UserEntity user = new UserEntity();
        user.setEmail("eddi@test.de");

        String jwt = jwtService.generateToken(user);
        String manipulatedJwt = jwt.substring(0, jwt.length() - 1) + "x";
        assertThrows(JwtException.class, () -> jwtService.extractEmail(manipulatedJwt));
    }

    @Test
    void expiredJwt() {
        UserEntity user = new UserEntity();
        user.setEmail("eddi@test.de");

        ReflectionTestUtils.setField(jwtService, "expirationMinutes", -1L);
        String jwt = jwtService.generateToken(user);
        assertThrows(JwtException.class, () -> jwtService.extractEmail(jwt));
    }
}

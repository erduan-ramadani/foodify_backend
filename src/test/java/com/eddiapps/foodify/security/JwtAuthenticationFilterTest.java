package com.eddiapps.foodify.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.repository.UserRepository;
import com.eddiapps.foodify.service.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService, userRepository);
        SecurityContextHolder.clearContext();
    }

    @Test
    void validJwtSetsAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");

        UserEntity user = new UserEntity();
        user.setEmail("eddi@web.de");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtService.extractEmail("test-token")).thenReturn("eddi@web.de");
        when(userRepository.findByEmail("eddi@web.de")).thenReturn(Optional.of(user));

        filter.doFilter(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(user, authentication.getPrincipal());
    }

    @Test
    void userDoesNotExist() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtService.extractEmail("test-token")).thenReturn("eddi@web.de");
        when(userRepository.findByEmail("eddi@web.de")).thenReturn(Optional.empty());

        filter.doFilter(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertEquals(401, response.getStatus());
        assertNull(authentication);
    }

    @Test
    void invalidOrManipulatedJwt() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        when(jwtService.extractEmail("test-token")).thenThrow(new JwtException("Invalid JWT"));

        filter.doFilter(request, response, filterChain);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertEquals(401, response.getStatus());
        assertNull(authentication);
    }
}

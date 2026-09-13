package com.eddiapps.foodify.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eddiapps.foodify.entity.UserEntity;
import com.eddiapps.foodify.repository.UserRepository;
import com.eddiapps.foodify.service.JwtService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @Test
    void foodEntriesWithoutJwtReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/food-entries")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithoutCredentialsReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void foodEntriesWithValidJwtReturnsOk() throws Exception {
        UserEntity user = new UserEntity();
        user.setEmail("eddi@test.de");
        user.setName("Eddi");
        user.setPasswordHash("test-hash");

        user = userRepository.save(user);
        String jwt = jwtService.generateToken(user);

        mockMvc.perform(get("/api/food-entries").header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void foodEntriesWithInvalidJwtReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/food-entries")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void foodEntriesWithExpiredJwtReturnsUnauthorized() throws Exception {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="));

        String expiredJwt = Jwts.builder()
                .subject("eddi@test.de")
                .expiration(Date.from(Instant.now().minus(1, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();

        mockMvc.perform(get("/api/food-entries")
                        .header("Authorization", "Bearer " + expiredJwt))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidLoginRequestReturnsStructuredValidationError() throws Exception {
        String json = """
                {
                  "email": "keine-email",
                  "password": ""
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}

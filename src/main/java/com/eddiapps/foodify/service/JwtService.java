package com.eddiapps.foodify.service;

import com.eddiapps.foodify.entity.UserEntity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration-minutes}")
    private long expirationMinutes;

    public String generateToken(UserEntity user) {
        Date date = Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES));
        return Jwts.builder()
                .subject(user.getEmail())
                .expiration(date)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String jwt) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

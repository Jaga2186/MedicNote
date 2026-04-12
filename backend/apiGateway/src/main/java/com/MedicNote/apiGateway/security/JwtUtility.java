package com.MedicNote.apiGateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtUtility {

    @Value("${jwt.secret}")
    private String secret;

    private Key secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        log.info("Gateway JwtUtility initialized successfully");
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            boolean notExpired = !claims.getExpiration().before(new Date());
            log.debug("Token valid: {}", notExpired);
            return notExpired;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
}
package com.MedicNote.prescriptionService.security;

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
        log.info("PrescriptionService JWT Utility initialized successfully");
    }

    // ✅ NO generateToken — Prescription Service never issues tokens

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.warn("Failed to parse JWT token: {}", e.getMessage());
            throw e;
        }
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token, String email) {
        try {
            String extractedEmail = extractEmail(token);
            boolean notExpired = !extractAllClaims(token).getExpiration().before(new Date());
            return extractedEmail.equals(email) && notExpired;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            return !extractAllClaims(token).getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
}
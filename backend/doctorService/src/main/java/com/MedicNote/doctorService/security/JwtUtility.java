package com.MedicNote.doctorService.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtility {

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration}")
    private long EXPIRATION;

    private Key secretKey;

    @PostConstruct
    public void init() {
        log.info("Initializing JWT Utility...");

        // Secret must be minimum 32 characters
        secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());

        log.info("JWT Utility initialized successfully");
    }

    private Key getSecretKey() {
        return secretKey;
    }

    // ✅ Generate token with role
    public String generateToken(String email, String role) {
        log.info("Generating JWT token for email: {} role: {}", email, role);

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Generate token without role
    public String generateToken(String email) {
        return generateToken(email, null);
    }

    // ================= TOKEN EXTRACTION =================

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // ================= VALIDATION =================

    public boolean isTokenValid(String token) {
        return !extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, String email) {
        try {
            final String extractedEmail = extractEmail(token);
            return extractedEmail.equals(email) && isTokenValid(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            return isTokenValid(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
}
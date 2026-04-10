package com.MedicNote.authService.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

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
    private String SECRET;

    @Value("${jwt.expiration}")
    private long EXPIRATION;

    private Key secretKey;

    private Key getSecretKey() {
        if (secretKey == null) {
            log.debug("Initializing JWT secret Key");
            secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));
        }
        return secretKey;
    }

    public String generateToken(String email, String role) {
        log.info("Generating JWT token for email: {} role: {}", email, role);
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();

        log.debug("JWT token generated successfully for email: {}", email);
        return token;
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
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

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            boolean isValid = !claims.getExpiration().before(new Date());
            if (isValid) {
                log.debug("JWT token is valid");
            } else {
                log.warn("JWT token is expired");
            }
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
}

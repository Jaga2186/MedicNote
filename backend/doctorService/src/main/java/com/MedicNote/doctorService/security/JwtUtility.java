package com.MedicNote.doctorService.security;

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

    public String generateToken(String email) {
        return generateToken(email, null);
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
        String email = extractAllClaims(token).getSubject();
        log.debug("Extracted email from token: {}", email);
        return email;
    }

    public String extractRole(String token) {
        String role = extractAllClaims(token).get("role", String.class);
        log.debug("Extracted role from token: {}", role);
        return role;
    }

    public Date extractExpiration(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        log.debug("Token expiration time: {}", expiration);
        return expiration;
    }

    public boolean isTokenValid(String token) {
        boolean isValid = !extractExpiration(token).before(new Date());
        log.debug("Token validity checked: {}", isValid);
        return isValid;
    }

    public boolean validateToken(String token, String email) {
        try {
            final String extractedEmail = extractEmail(token);
            boolean isValid = extractedEmail.equals(email) && isTokenValid(token);

            if (isValid) {
                log.info("JWT token is valid for email: {}", email);
            } else {
                log.warn("JWT token validation failed for email: {}", email);
            }
            return isValid;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired for email: {}", email);
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            boolean isValid = isTokenValid(token);

            if (isValid) {
                log.debug("JWT token is valid");
            } else {
                log.warn("JWT token is invalid or expired");
            }
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }
}

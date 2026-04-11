package com.MedicNote.doctorService.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtility jwtUtility;

    public JwtAuthenticationFilter(JwtUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // 🔓 1️⃣ Skip Swagger endpoints
        if (path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs") ||
                path.contains("/swagger-resources") ||
                path.contains("/webjars")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 🔓 2️⃣ Skip public Doctor APIs
        if (path.contains("/api/doctors/register") ||
                path.contains("/api/doctors/login") ||
                path.contains("/api/doctors/check-email")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 3️⃣ Read Authorization header
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String token;

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.debug("No JWT token found in request for URI: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        token = authorizationHeader.substring(7);

        try {
            email = jwtUtility.extractEmail(token);
            log.debug("Extracted email from JWT: {}", email);
        } catch (Exception e) {
            log.warn("Invalid JWT token for URI {}: {}", request.getRequestURI(), e.getMessage());
        }

        // 4️⃣ Validate token and set authentication
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtUtility.validateToken(token, email)) {

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                String role = jwtUtility.extractRole(token);

                if (role != null && !role.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                authorities
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("JWT authentication successful for user: {} role: {}", email, role);
            } else {
                log.warn("JWT validation failed for user: {}", email);
            }
        }

        filterChain.doFilter(request, response);
    }
}
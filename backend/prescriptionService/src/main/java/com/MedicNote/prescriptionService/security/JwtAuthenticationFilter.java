package com.MedicNote.prescriptionService.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Skip Swagger endpoints
        if (path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs") ||
                path.contains("/swagger-resources") ||
                path.contains("/webjars")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Read Authorization header
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.debug("No JWT token found for URI: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);
        String email = null;

        try {
            email = jwtUtility.extractEmail(token);
            log.debug("Extracted email from JWT: {}", email);
        } catch (Exception e) {
            log.warn("Failed to extract email from JWT for URI {}: {}", request.getRequestURI(), e.getMessage());
        }

        // Validate token and set authentication in SecurityContext
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtility.validateToken(token, email)) {

                // Extract role and set as authority
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                String role = jwtUtility.extractRole(token);
                if (role != null && !role.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("JWT auth successful for user: {} role: {}", email, role);
            } else {
                log.warn("JWT validation failed for user: {}", email);
            }
        }

        filterChain.doFilter(request, response);
    }
}
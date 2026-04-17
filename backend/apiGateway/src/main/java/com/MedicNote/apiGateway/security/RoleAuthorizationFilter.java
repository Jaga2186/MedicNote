package com.MedicNote.apiGateway.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RoleAuthorizationFilter implements WebFilter {

    private final JwtUtility jwtUtil;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/doctor/register",
            "/api/auth/doctor/login",
            "/api/auth/patient/register",
            "/api/auth/patient/login",
            "/actuator"
    );

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange,@NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        // Skip public paths
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        // Only apply role checks on prescription endpoints
        if (!path.startsWith("/api/prescriptions")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        String role = jwtUtil.extractRole(token);

        log.info("Role check for path: {}, method: {}, role: {}", path, method, role);

        // ✅ DOCTOR can do everything on prescriptions
        if ("DOCTOR".equals(role)) {
            return chain.filter(exchange);
        }

        // ✅ PATIENT can only GET (view) and GET /{id}/download
        if ("PATIENT".equals(role)) {
            if (HttpMethod.GET.equals(method)) {
                // Block patient from email endpoint — that's a doctor action
                if (path.endsWith("/email")) {
                    log.warn("PATIENT tried to access email endpoint: {}", path);
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
                return chain.filter(exchange);
            }
            // Block POST, PUT, PATCH, DELETE for patients
            log.warn("PATIENT tried to perform {} on {}", method, path);
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        // Unknown role — deny
        log.warn("Unknown role '{}' tried to access: {}", role, path);
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }
}
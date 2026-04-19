package com.MedicNote.apiGateway.config;

import com.MedicNote.apiGateway.security.JwtAuthenticationFilter;
import com.MedicNote.apiGateway.security.JwtUtility;
import com.MedicNote.apiGateway.security.RoleAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtility jwtUtility;
    private final RoleAuthorizationFilter roleAuthorizationFilter;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtility);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/api/auth/doctor/register",
                                "/api/auth/doctor/login",
                                "/api/auth/patient/register",
                                "/api/auth/patient/login",
                                "/api/auth/otp/verify",
                                "/actuator/**"
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                // JWT validation runs first
                .addFilterBefore(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                // Role check runs after JWT is validated
                .addFilterAfter(roleAuthorizationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
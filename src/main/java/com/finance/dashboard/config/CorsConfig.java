package com.finance.dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS configuration — allows the frontend to call the API.
 *
 * This covers two scenarios:
 *   1. Frontend served by Spring Boot at http://localhost:8080  (same origin, no CORS needed but harmless)
 *   2. Frontend opened as a file:// or from a different port    (cross-origin — CORS required)
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow the frontend origins — covers both Spring Boot served and file:// opened
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",       // any localhost port (8080, 3000, 5173, etc.)
                "http://127.0.0.1:*",
                "file://*",                // opened directly as a file in browser
                "null"                     // some browsers send origin: null for file://
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);           // cache preflight for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // apply to ALL endpoints
        return source;
    }
}

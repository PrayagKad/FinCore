package com.finance.dashboard.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT utility — updated for jjwt 0.12.x API.
 *
 * Key changes from 0.11 → 0.12:
 *  - Jwts.builder() methods renamed (setSubject → subject, etc.)
 *  - Jwts.parserBuilder() replaced with Jwts.parser()
 *  - Keys.hmacShaKeyFor() now accepts raw bytes directly
 *  - SignatureAlgorithm enum no longer passed to signWith()
 */
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    // Build key from the plain secret string
    private SecretKey getSigningKey() {
        // Use the secret string as raw bytes — long enough for HS256 (32+ chars)
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ── Generate token (called after successful login) ────────────────────
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)                  // 0.12 API: subject() not setSubject()
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())        // 0.12 API: no algorithm arg needed
                .compact();
    }

    // ── Extract email from token ──────────────────────────────────────────
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // ── Extract role from token claims ────────────────────────────────────
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // ── Validate token — returns false if expired or tampered ─────────────
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()                     // 0.12 API: parser() not parserBuilder()
                .verifyWith(getSigningKey())      // 0.12 API: verifyWith() not setSigningKey()
                .build()
                .parseSignedClaims(token)        // 0.12 API: parseSignedClaims() not parseClaimsJws()
                .getPayload();
    }
}

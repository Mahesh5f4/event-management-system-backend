package com.eventhub.backend.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey123";
    private final long EXPIRATION = 3600000;

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // 🔥 Generate token
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getKey())
                .compact();
    }

    // 🔥 Extract all claims
    public Claims extract(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 🔥 Extract username
    public String extractUsername(String token) {
        return extract(token).getSubject();
    }

    // 🔥 Extract role
    public String extractRole(String token) {
        return extract(token).get("role", String.class);
    }

    // 🔥 Validate token (optional but good)
    public boolean isValid(String token) {
        try {
            extract(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
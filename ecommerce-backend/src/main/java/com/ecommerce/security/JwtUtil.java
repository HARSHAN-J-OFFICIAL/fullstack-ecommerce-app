package com.ecommerce.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import java.util.Date;

import java.util.function.Function;

@Component
public class JwtUtil {

    private static final String SECRET_KEY =
            "bXktc2VjcmV0LWtleS1mb3Itand0LWRlbW8tYXBwLTIwMjYtMTIzNDU2";

    private static final long JWT_EXPIRATION =
            1000 * 60 * 60 * 24;

    private Key getSignInKey() {

        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // Generate JWT token
    public String generateToken(UserDetails userDetails) {

        String role = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(Object::toString)
                .orElse("ROLE_USER");

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)
                )
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract username/email from token
    public String extractUsername(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    // Validate token
    public boolean validateToken(
            String token,
            UserDetails userDetails
    ) {

        final String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    // Check expiration
    private boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    // Extract expiration date
    private Date extractExpiration(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    // Generic claim extractor
    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    // Extract all claims
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()

                .setSigningKey(getSignInKey())

                .build()

                .parseClaimsJws(token)

                .getBody();
    }
}
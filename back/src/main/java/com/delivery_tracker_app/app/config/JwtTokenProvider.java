package com.delivery_tracker_app.app.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // Import SecretKey
import java.nio.charset.StandardCharsets; // For getBytes(StandardCharsets.UTF_8)
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration.ms}")
    private long jwtExpirationInMs;

    // Helper method to get the signing key. It explicitly returns SecretKey now.
    private SecretKey getSigningKey() {
        // Best practice: Use StandardCharsets.UTF_8 for consistent byte conversion
        // Ensure your JWT_SECRET is strong (e.g., 32 characters or more for HS256)
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Generates a JWT token for the authenticated user
    public String generateToken(Authentication authentication) {
        String username = authentication.getName(); // Principal name (username)

        // Get roles from authentication object and join them into a comma-separated string
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(username) // Non-deprecated
                .claim("roles", roles) // Custom claim for user roles
                .issuedAt(now) // Non-deprecated
                .expiration(expiryDate) // Non-deprecated
                .signWith(getSigningKey()) // Non-deprecated, now accepts SecretKey directly
                .compact(); // Build and compact the JWT into a URL-safe string
    }

    // Extracts username from a JWT token
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey()) // Now correctly accepts SecretKey
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    // Validates a JWT token
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey()) // Now correctly accepts SecretKey
                    .build()
                    .parseSignedClaims(authToken);

            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
}
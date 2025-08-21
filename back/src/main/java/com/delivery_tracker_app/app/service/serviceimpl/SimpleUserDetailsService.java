package com.delivery_tracker_app.app.service.serviceimpl;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SimpleUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(SimpleUserDetailsService.class);

    @Value("${app.user.username}")
    private String fixedUsername;

    @Value("${app.user.password}")
    private String fixedRawPassword; // This is the raw password from .env

    @Value("${app.user.roles}")
    private String fixedRolesString;

    private final PasswordEncoder passwordEncoder;

    // This will store the hashed version of the password at application startup
    private String fixedHashedPassword;

    public SimpleUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Hashes the raw password from .env on application startup.
     * This ensures the password stored in-memory (or in a real DB) is always hashed.
     * This is a simple approach for a single fixed user; for multiple users,
     * passwords would be hashed during registration and stored in a database.
     */
    @PostConstruct
    public void init() {
        if (fixedRawPassword != null && !fixedRawPassword.isEmpty()) {
            this.fixedHashedPassword = passwordEncoder.encode(fixedRawPassword);
            logger.info("Initialized fixed user '{}' with hashed password.", fixedUsername);
            // Clear the raw password from memory after hashing, for security
            this.fixedRawPassword = null;
        } else {
            logger.error("No fixed user password provided in application properties or environment variables.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Since we only have one user, check if the requested username matches the fixed one.
        if (!fixedUsername.equals(username)) {
            logger.warn("Authentication attempt for unknown user: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Parse roles from the comma-separated string
        Set<GrantedAuthority> authorities = Collections.emptySet();
        if (fixedRolesString != null && !fixedRolesString.isEmpty()) {
            authorities = Arrays.stream(fixedRolesString.split(","))
                    .map(String::trim) // Trim whitespace
                    .map(String::toUpperCase) // Conventionally roles are uppercase
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role) // Ensure ROLE_ prefix
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }

        logger.debug("Loading user details for fixed user '{}' with roles: {}", fixedUsername, authorities);

        // Return Spring Security's UserDetails object
        return new org.springframework.security.core.userdetails.User(
                fixedUsername,
                fixedHashedPassword, // Use the pre-hashed password for authentication
                authorities
        );
    }
}
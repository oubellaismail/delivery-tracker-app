package com.delivery_tracker_app.app.config;


import com.delivery_tracker_app.app.service.serviceimpl.SimpleUserDetailsService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@TestConfiguration
public class TestSecurityConfig {

    // 1. Provide a test-specific PasswordEncoder bean
    @Bean
    @Primary // Ensures this bean is chosen over any others that might be found
    public PasswordEncoder passwordEncoder() {
        // For testing, a simple encoder is often sufficient, or you can use the same
        // as your main application to ensure compatibility.
        return new BCryptPasswordEncoder();
    }

    // 2. Provide a mock for the JwtTokenProvider
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider();
    }

    // 3. Provide the SimpleUserDetailsService, now with its required PasswordEncoder
    // The passwordEncoder bean from step 1 is automatically injected here
    @Bean
    public SimpleUserDetailsService simpleUserDetailsService(PasswordEncoder passwordEncoder) {
        // We pass the provided PasswordEncoder to its constructor
        return new SimpleUserDetailsService(passwordEncoder);
    }

    // 4. Provide the JwtAuthenticationFilter, now with both of its dependencies
    // The beans from steps 2 and 3 are automatically injected here
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, SimpleUserDetailsService simpleUserDetailsService) {
        return new JwtAuthenticationFilter(jwtTokenProvider, simpleUserDetailsService);
    }

    // 5. Configure the test security filter chain using the custom filter
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
        return http.build();
    }
}
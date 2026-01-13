package com.soyucab.back.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(org.springframework.security.config.Customizer.withDefaults()) // Enable CORS integration
                .csrf(csrf -> csrf.disable()) // Disable CSRF for Stateless API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/api/test/**", "/api/eventos", "/api/eventos/**").permitAll() // Allow
                                                                                                                    // Login/Register,
                                                                                                                    // Test,
                                                                                                                    // and
                                                                                                                    // Event
                                                                                                                    // Reading
                        .anyRequest().authenticated() // Protect everything else
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

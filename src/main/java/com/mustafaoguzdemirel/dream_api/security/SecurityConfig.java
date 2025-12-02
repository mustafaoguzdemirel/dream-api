package com.mustafaoguzdemirel.dream_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security Configuration
 *
 * Bu class:
 * - Hangi endpoint'lerin korunacağını belirler
 * - JWT filter'ı Spring Security filter chain'e ekler
 * - CSRF'yi disable eder (JWT kullandığımız için gerekli)
 * - Stateless session management kullanır (JWT token-based)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Security filter chain configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF'yi disable et (JWT kullanıyoruz, cookie-based authentication yok)
                .csrf(AbstractHttpConfigurer::disable)

                // Endpoint authorization rules
                .authorizeHttpRequests(auth -> auth
                        // 🔓 Authentication endpoint'leri AÇIK (herkes erişebilir)
                        .requestMatchers("/api/auth/anonymous", "/api/auth/google").permitAll()

                        // 🔒 Diğer tüm endpoint'ler KORUMALI (authentication gerekli)
                        .anyRequest().authenticated()
                )

                // Session management: STATELESS (her request'te token kontrol edilecek)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // JWT filter'ı ekle (UsernamePasswordAuthenticationFilter'dan önce)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * AuthenticationManager bean (gerekirse kullanılmak üzere)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
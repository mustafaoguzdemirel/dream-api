package com.mustafaoguzdemirel.dream_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * JWT Authentication Filter - Her request'te çalışır
 *
 * Görevleri:
 * 1. Authorization header'dan JWT token'ı alır
 * 2. Token'ı validate eder
 * 3. Token'dan userId'yi çıkarır
 * 4. User'ı database'den bulur
 * 5. Spring Security context'e kullanıcıyı set eder
 *
 * Bu sayede controller'larda SecurityContextHolder.getContext() ile
 * authenticated user'a erişebiliriz.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1️⃣ Authorization header'ı al
        final String authHeader = request.getHeader("Authorization");

        // Header yoksa veya "Bearer " ile başlamıyorsa, filter chain'e devam et
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2️⃣ Token'ı çıkar (Bearer kısmını at)
            final String jwt = authHeader.substring(7);

            // 3️⃣ Token'dan userId'yi çıkar
            final UUID userId = jwtUtil.extractUserId(jwt);

            // 4️⃣ Eğer userId var ve kullanıcı henüz authenticate edilmemişse
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 5️⃣ Database'den kullanıcıyı bul
                UserDetails userDetails = this.userDetailsService.loadUserByUserId(userId);

                // 6️⃣ Token'ı validate et
                if (jwtUtil.validateToken(jwt, userId)) {

                    // 7️⃣ Spring Security için Authentication object oluştur
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,  // credentials - JWT kullandığımız için null
                            userDetails.getAuthorities()
                    );

                    // 8️⃣ Request detaylarını ekle
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 9️⃣ SecurityContext'e kullanıcıyı set et
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token geçersizse veya parse edilemezse, authentication olmadan devam et
            // Bu durumda SecurityConfig'deki rules devreye girer
            logger.error("JWT authentication error: " + e.getMessage());
        }

        // Filter chain'e devam et
        filterChain.doFilter(request, response);
    }
}
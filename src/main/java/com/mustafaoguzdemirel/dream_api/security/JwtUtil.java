package com.mustafaoguzdemirel.dream_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * JWT (JSON Web Token) işlemleri için utility class.
 * Token oluşturma, doğrulama ve parse etme işlemlerini yapar.
 *
 * JJWT 0.12.5 API kullanıyor (yeni API)
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Token'dan userId'yi çıkarır
     */
    public UUID extractUserId(String token) {
        String userIdStr = extractClaim(token, Claims::getSubject);
        return UUID.fromString(userIdStr);
    }

    /**
     * Token'dan expiration date'i çıkarır
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Token'dan belirli bir claim'i çıkarır
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Token'dan tüm claims'leri çıkarır
     * JJWT 0.12.5 yeni API
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Token'ın süresi dolmuş mu kontrol eder
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Yeni bir JWT token oluşturur
     * @param userId Kullanıcının UUID'si
     * @return JWT token string
     */
    public String generateToken(UUID userId) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId.toString());
    }

    /**
     * Token oluşturur (internal method)
     * JJWT 0.12.5 yeni API
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)  // subject = userId
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Token'ı validate eder
     * @param token JWT token
     * @param userId Kullanıcının UUID'si
     * @return Token geçerli mi?
     */
    public Boolean validateToken(String token, UUID userId) {
        final UUID extractedUserId = extractUserId(token);
        return (extractedUserId.equals(userId) && !isTokenExpired(token));
    }

    /**
     * Secret key'den signing key oluşturur
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
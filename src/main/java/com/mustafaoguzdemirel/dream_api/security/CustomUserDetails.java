package com.mustafaoguzdemirel.dream_api.security;

import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Spring Security için AppUser'ı UserDetails'e dönüştürür.
 * Spring Security bu class'ı kullanarak authentication yapacak.
 */
public class CustomUserDetails implements UserDetails {

    private final UUID userId;
    private final String email;
    private final String googleId;

    public CustomUserDetails(AppUser user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.googleId = user.getGoogleId();
    }

    /**
     * UserId'yi döndürür
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * Email'i döndürür
     */
    public String getEmail() {
        return email;
    }

    /**
     * Google ID'yi döndürür
     */
    public String getGoogleId() {
        return googleId;
    }

    /**
     * Kullanıcının yetkilerini döndürür.
     * Şu an için role sistemi yok, boş liste dönüyoruz.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * Password döndürür. JWT kullandığımız için password yok.
     */
    @Override
    public String getPassword() {
        return null;  // JWT kullanıyoruz, password yok
    }

    /**
     * Username döndürür. UserId'yi string olarak kullanıyoruz.
     */
    @Override
    public String getUsername() {
        return userId.toString();
    }

    /**
     * Hesap süresinin dolup dolmadığını kontrol eder
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Hesabın kilitli olup olmadığını kontrol eder
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Credential'ların (şifre vb) süresinin dolup dolmadığını kontrol eder
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Hesabın aktif olup olmadığını kontrol eder
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
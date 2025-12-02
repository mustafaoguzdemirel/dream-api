package com.mustafaoguzdemirel.dream_api.security;

import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Spring Security için UserDetailsService implementation.
 * Database'den kullanıcı bilgisini çeker ve UserDetails'e dönüştürür.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Username (bizim durumumuzda userId) ile kullanıcıyı bulur
     * @param username UserId string formatında
     * @return UserDetails object
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UUID userId = UUID.fromString(username);
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));

        return new CustomUserDetails(user);
    }

    /**
     * UUID ile direkt kullanıcı bulma
     */
    public UserDetails loadUserByUserId(UUID userId) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));

        return new CustomUserDetails(user);
    }
}
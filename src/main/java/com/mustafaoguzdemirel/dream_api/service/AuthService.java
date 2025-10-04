package com.mustafaoguzdemirel.dream_api.service;

import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser createAnonymousUser() {
        try {
            UUID userId = UUID.randomUUID();
            AppUser user = new AppUser(userId);
            return userRepository.save(user);
        } catch (DataAccessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException("Anonymous user creation failed: " + ex.getMessage(), ex);
        }
    }
}

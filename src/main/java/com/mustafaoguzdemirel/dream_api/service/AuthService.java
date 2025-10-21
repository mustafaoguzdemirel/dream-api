package com.mustafaoguzdemirel.dream_api.service;

import com.mustafaoguzdemirel.dream_api.dto.DreamDetailResponse;
import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.entity.Dream;
import com.mustafaoguzdemirel.dream_api.repository.DreamRepository;
import com.mustafaoguzdemirel.dream_api.repository.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final DreamRepository dreamRepository;

    public AuthService(UserRepository userRepository, DreamRepository dreamRepository) {
        this.userRepository = userRepository;
        this.dreamRepository = dreamRepository;
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

    public AppUser getOrCreateAnonymousUser(UUID userId) {
        return userRepository.findByUserId(userId)
                .orElseGet(() -> {
                    AppUser newUser = new AppUser(userId);
                    return userRepository.save(newUser);
                });
    }

    public DreamDetailResponse getTodayDreamIfExists(AppUser user, LocalDate today) {
        List<Dream> dreams = dreamRepository.findByUser(user);

        return dreams.stream()
                .filter(d -> d.getCreatedAt().toLocalDate().isEqual(today))
                .findFirst()
                .map(d -> new DreamDetailResponse(
                        d.getId(),
                        d.getDreamText(),
                        d.getInterpretation()
                ))
                .orElse(null);
    }


}

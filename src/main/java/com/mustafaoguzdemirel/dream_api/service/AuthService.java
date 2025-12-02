package com.mustafaoguzdemirel.dream_api.service;

import com.mustafaoguzdemirel.dream_api.dto.response.DreamDetailResponse;
import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.entity.Dream;
import com.mustafaoguzdemirel.dream_api.repository.DreamRepository;
import com.mustafaoguzdemirel.dream_api.repository.MoodAnalysisRepository;
import com.mustafaoguzdemirel.dream_api.repository.UserRepository;
import com.mustafaoguzdemirel.dream_api.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final DreamRepository dreamRepository;
    private final MoodAnalysisRepository moodAnalysisRepository;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepository,
            DreamRepository dreamRepository,
            MoodAnalysisRepository moodAnalysisRepository,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.dreamRepository = dreamRepository;
        this.moodAnalysisRepository = moodAnalysisRepository;
        this.jwtUtil = jwtUtil;
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
        List<Dream> dreams = dreamRepository.findByUserOrderByCreatedAtDesc(user);

        return dreams.stream()
                .filter(d -> d.getCreatedAt().toLocalDate().isEqual(today))
                .findFirst()
                .map(d -> new DreamDetailResponse(
                        d.getId(),
                        d.getDreamText(),
                        d.getInterpretation(),
                        d.getDetailedInterpretation(),
                        d.getCreatedAt()
                ))
                .orElse(null);
    }

    public AppUser linkOrCreateGoogleUser(String googleId, String email, String anonymousUserIdStr) {
        // 1. Daha önce Google hesabı kayıtlı mı?
        AppUser existing = userRepository.findByGoogleId(googleId).orElse(null);
        if (existing != null) return existing;

        // 2. Eğer anonim user varsa, onu Google hesabına bağla
        if (anonymousUserIdStr != null && !anonymousUserIdStr.isBlank()) {
            UUID anonId = UUID.fromString(anonymousUserIdStr);
            AppUser anonUser = userRepository.findByUserId(anonId).orElse(null);
            if (anonUser != null) {
                anonUser.setGoogleId(googleId);
                anonUser.setEmail(email);
                return userRepository.save(anonUser);
            }
        }

        // 3. Aksi halde yeni Google user oluştur
        AppUser newUser = new AppUser(UUID.randomUUID());
        newUser.setGoogleId(googleId);
        newUser.setEmail(email);
        return userRepository.save(newUser);
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        AppUser user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔹 İlişkili verileri temizle
        dreamRepository.deleteAllByUser(user);
        moodAnalysisRepository.deleteAllByUser(user);

        // 🔹 Kullanıcıyı sil
        userRepository.delete(user);
    }

}

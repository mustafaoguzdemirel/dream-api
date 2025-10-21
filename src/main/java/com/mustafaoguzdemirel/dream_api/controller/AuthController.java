package com.mustafaoguzdemirel.dream_api.controller;

import com.mustafaoguzdemirel.dream_api.dto.response.ApiResponse;
import com.mustafaoguzdemirel.dream_api.dto.response.DreamDetailResponse;
import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/anonymous")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrFetchAnonymousUser(
            @RequestBody(required = false) Map<String, String> request) {

        String userIdStr = request != null ? request.get("userId") : null;

        AppUser user;
        DreamDetailResponse todayDream = null;

        if (userIdStr != null && !userIdStr.isBlank()) {
            UUID userId = UUID.fromString(userIdStr);
            user = authService.getOrCreateAnonymousUser(userId);

            // Bugünün tarihini al
            LocalDate today = LocalDate.now();

            // Kullanıcının bugünkü rüyasını kontrol et
            todayDream = authService.getTodayDreamIfExists(user, today);
        } else {
            user = authService.createAnonymousUser();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("lastDreamInterpretedDate", user.getLastDreamInterpretedDate());
        data.put("todayDream", todayDream); // null olabilir

        return ResponseEntity.ok(ApiResponse.success("Anonymous user ready", data));
    }


}

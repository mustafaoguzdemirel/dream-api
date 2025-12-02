package com.mustafaoguzdemirel.dream_api.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.mustafaoguzdemirel.dream_api.dto.response.ApiResponse;
import com.mustafaoguzdemirel.dream_api.dto.response.DreamDetailResponse;
import com.mustafaoguzdemirel.dream_api.entity.AppUser;
import com.mustafaoguzdemirel.dream_api.security.CustomUserDetails;
import com.mustafaoguzdemirel.dream_api.security.JwtUtil;
import com.mustafaoguzdemirel.dream_api.service.AuthService;
import com.mustafaoguzdemirel.dream_api.service.GoogleVerifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
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

        // JWT token oluştur
        String token = jwtUtil.generateToken(user.getUserId());

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("token", token);  // 🔑 JWT token eklendi
        data.put("lastDreamInterpretedDate", user.getLastDreamInterpretedDate());
        data.put("todayDream", todayDream); // null olabilir

        return ResponseEntity.ok(ApiResponse.success("Anonymous user ready", data));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginWithGoogle(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");
        String anonymousUserIdStr = request.get("anonymousUserId"); // app'ten opsiyonel olarak gönderilebilir

        if (idToken == null || idToken.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("MISSING_GOOGLE_ID", "Missing Google ID token", null));
        }

        // Google token doğrulama
        GoogleIdToken.Payload payload = GoogleVerifier.verifyToken(idToken);
        if (payload == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_GOOGLE_ID", "Invalid Google ID token", null));
        }

        String googleId = payload.getSubject(); // benzersiz Google ID
        String email = payload.getEmail();

        AppUser user = authService.linkOrCreateGoogleUser(googleId, email, anonymousUserIdStr);

        // 🔸 Bugünkü rüya verisini al (anonim API ile aynı)
        LocalDate today = LocalDate.now();
        DreamDetailResponse todayDream = authService.getTodayDreamIfExists(user, today);

        // JWT token oluştur
        String token = jwtUtil.generateToken(user.getUserId());

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("token", token);  // 🔑 JWT token eklendi
        data.put("lastDreamInterpretedDate", user.getLastDreamInterpretedDate());
        data.put("todayDream", todayDream);
        data.put("email", user.getEmail());
        data.put("googleId", user.getGoogleId());

        return ResponseEntity.ok(ApiResponse.success("Google user authenticated", data));
    }

    /**
     * Helper method: JWT token'dan authenticated user'ın userId'sini çıkarır
     */
    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }

    /**
     * Hesap silme endpoint'i - JWT token'dan userId alıyor
     * NOT: Bu endpoint /api/auth/ altında olduğu için normalde açık olurdu,
     * ama biz bunu /api/user/ altına taşımalıyız. Şimdilik burada kalsın.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteAccount() {
        // JWT token'dan userId al
        UUID userId = getAuthenticatedUserId();
        authService.deleteAccount(userId);
        return ResponseEntity.ok(ApiResponse.success("Your account has been permanently deleted", null));
    }

}

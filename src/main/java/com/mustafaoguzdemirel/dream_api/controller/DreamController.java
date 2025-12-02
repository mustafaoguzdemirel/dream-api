package com.mustafaoguzdemirel.dream_api.controller;

import com.mustafaoguzdemirel.dream_api.dto.response.*;
import com.mustafaoguzdemirel.dream_api.dto.request.DreamSaveRequest;
import com.mustafaoguzdemirel.dream_api.entity.Dream;
import com.mustafaoguzdemirel.dream_api.entity.MoodAnalysis;
import com.mustafaoguzdemirel.dream_api.security.CustomUserDetails;
import com.mustafaoguzdemirel.dream_api.service.DreamService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/dream")
public class DreamController {

    private final DreamService dreamService;

    public DreamController(DreamService dreamService) {
        this.dreamService = dreamService;
    }

    /**
     * Helper method: JWT token'dan authenticated user'ın userId'sini çıkarır
     */
    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }

    @PostMapping("/interpret")
    public ResponseEntity<ApiResponse<DreamDetailResponse>> interpretDream(@RequestBody Map<String, String> request) {
        try {
            String dreamText = request.get("dreamText");

            if (dreamText == null || dreamText.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("INVALID_REQUEST", "Missing dreamText", null));
            }

            // JWT token'dan userId al
            UUID userId = getAuthenticatedUserId();
            DreamDetailResponse result = dreamService.interpretDreamForUser(userId, dreamText);

            return ResponseEntity.ok(ApiResponse.success("Dream interpreted successfully", result));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("DREAM_LIMIT_REACHED", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Unexpected error occurred", null));
        }
    }


    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Dream>> saveDream(@RequestBody DreamSaveRequest request) {
        Dream savedDream = dreamService.saveDream(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Dream saved successfully", savedDream));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Dream>>> getDreamHistory() {
        try {
            // JWT token'dan userId al
            UUID userId = getAuthenticatedUserId();
            List<Dream> dreams = dreamService.getDreamHistory(userId);
            return ResponseEntity.ok(
                    ApiResponse.success("Dream history fetched successfully", dreams)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("USER_NOT_FOUND", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Unexpected error occurred", null));
        }
    }

    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<DreamCalendarResponse>> getDreamCalendar() {
        try {
            // JWT token'dan userId al
            UUID userId = getAuthenticatedUserId();
            DreamCalendarResponse calendar = dreamService.getDreamCalendar(userId);
            return ResponseEntity.ok(ApiResponse.success("Dream calendar fetched successfully", calendar));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("USER_NOT_FOUND", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Unexpected error occurred", null));
        }
    }

    @GetMapping("/detail/{dreamId}")
    public ResponseEntity<ApiResponse<DreamDetailResponse>> getDreamDetail(@PathVariable UUID dreamId) {
        try {
            DreamDetailResponse dreamDetail = dreamService.getDreamDetail(dreamId);
            return ResponseEntity.ok(
                    ApiResponse.success("Dream detail fetched successfully", dreamDetail)
            );
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("DREAM_NOT_FOUND", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Unexpected error occurred", null));
        }
    }

    @PostMapping("/detailed-interpret/{dreamId}")
    public ResponseEntity<ApiResponse<DreamDetailResponse>> getDetailedInterpretation(@PathVariable UUID dreamId) {
        try {
            DreamDetailResponse result = dreamService.getDetailedInterpretation(dreamId);
            return ResponseEntity.ok(ApiResponse.success("Detailed interpretation generated successfully", result));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("DREAM_NOT_FOUND", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Unexpected error occurred", null));
        }
    }

    @GetMapping("/mood-analysis")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMoodAnalysis() {
        try {
            // JWT token'dan userId al
            UUID userId = getAuthenticatedUserId();
            Map<String, Object> result = dreamService.analyzeRecentDreams(userId);
            return ResponseEntity.ok(ApiResponse.success("Mood analysis generated successfully", result));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("ANALYSIS_ERROR", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/mood-history")
    public ResponseEntity<ApiResponse<List<MoodAnalysisResponse>>> getMoodHistory() {
        try {
            // JWT token'dan userId al
            UUID userId = getAuthenticatedUserId();
            List<MoodAnalysisResponse> history = dreamService.getMoodHistory(userId);
            return ResponseEntity.ok(ApiResponse.success("Mood analysis history fetched", history));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("NOT_FOUND", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", e.getMessage(), null));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<DreamListItemResponse>>> getDreamList(
            @RequestParam(defaultValue = "false") boolean isLastThree
    ) {
        try {
            // JWT token'dan userId al
            UUID userId = getAuthenticatedUserId();
            List<DreamListItemResponse> list = dreamService.getDreamList(userId, isLastThree);
            return ResponseEntity.ok(
                    ApiResponse.success("Dream list fetched successfully", list)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("USER_NOT_FOUND", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Unexpected error occurred", null));
        }
    }

}

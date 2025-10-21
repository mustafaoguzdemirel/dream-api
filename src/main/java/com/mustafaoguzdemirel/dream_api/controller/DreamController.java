package com.mustafaoguzdemirel.dream_api.controller;

import com.mustafaoguzdemirel.dream_api.dto.response.ApiResponse;
import com.mustafaoguzdemirel.dream_api.dto.response.DreamCalendarResponse;
import com.mustafaoguzdemirel.dream_api.dto.response.DreamDetailResponse;
import com.mustafaoguzdemirel.dream_api.dto.request.DreamSaveRequest;
import com.mustafaoguzdemirel.dream_api.entity.Dream;
import com.mustafaoguzdemirel.dream_api.entity.MoodAnalysis;
import com.mustafaoguzdemirel.dream_api.service.DreamService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/interpret")
    public ResponseEntity<ApiResponse<Map<String, Object>>> interpretDream(@RequestBody Map<String, String> request) {
        try {
            String dreamText = request.get("dreamText");
            String userIdStr = request.get("userId");

            if (dreamText == null || userIdStr == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("INVALID_REQUEST", "Missing userId or dreamText", null));
            }

            UUID userId = UUID.fromString(userIdStr);
            Map<String, Object> result = dreamService.interpretDreamForUser(userId, dreamText);

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

    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<List<Dream>>> getDreamHistory(@PathVariable UUID userId) {
        try {
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

    @GetMapping("/calendar/{userId}")
    public ResponseEntity<ApiResponse<DreamCalendarResponse>> getDreamCalendar(@PathVariable UUID userId) {
        try {
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
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDetailedInterpretation(@PathVariable UUID dreamId) {
        try {
            Map<String, Object> result = dreamService.getDetailedInterpretation(dreamId);
            return ResponseEntity.ok(ApiResponse.success("Detailed interpretation generated successfully", result));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("DREAM_NOT_FOUND", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Unexpected error occurred", null));
        }
    }

    @GetMapping("/mood-analysis/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMoodAnalysis(@PathVariable UUID userId) {
        try {
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

    @GetMapping("/mood-history/{userId}")
    public ResponseEntity<ApiResponse<List<MoodAnalysis>>> getMoodHistory(@PathVariable UUID userId) {
        try {
            List<MoodAnalysis> history = dreamService.getMoodHistory(userId);
            return ResponseEntity.ok(ApiResponse.success("Mood analysis history fetched", history));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("NOT_FOUND", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", e.getMessage(), null));
        }
    }

}

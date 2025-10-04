package com.mustafaoguzdemirel.dream_api.controller;

import com.mustafaoguzdemirel.dream_api.dto.ApiResponse;
import com.mustafaoguzdemirel.dream_api.dto.DreamSaveRequest;
import com.mustafaoguzdemirel.dream_api.entity.Dream;
import com.mustafaoguzdemirel.dream_api.service.DreamService;
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
        String dreamText = request.get("dreamText");

        String interpretation = dreamService.interpretDream(dreamText);

        return ResponseEntity.ok(ApiResponse.success("Dream interpreted successfully",
                Map.of("interpretation", interpretation)));
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

}

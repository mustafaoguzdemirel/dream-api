package com.mustafaoguzdemirel.dream_api.controller;

import com.mustafaoguzdemirel.dream_api.dto.request.UserAnswerRequest;
import com.mustafaoguzdemirel.dream_api.dto.response.ApiResponse;
import com.mustafaoguzdemirel.dream_api.dto.response.QuestionResponse;
import com.mustafaoguzdemirel.dream_api.security.CustomUserDetails;
import com.mustafaoguzdemirel.dream_api.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * Helper method: JWT token'dan authenticated user'ın userId'sini çıkarır
     */
    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getAllQuestionsForUser() {
        // JWT token'dan userId al
        UUID userId = getAuthenticatedUserId();
        List<QuestionResponse> questions = questionService.getAllQuestionsForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Questions fetched successfully", questions));
    }

    @PostMapping("/answer")
    public ResponseEntity<ApiResponse<String>> saveUserAnswer(@Valid @RequestBody UserAnswerRequest request) {
        questionService.saveUserAnswer(request);
        return ResponseEntity.ok(ApiResponse.success("Answer saved successfully", null));
    }

}

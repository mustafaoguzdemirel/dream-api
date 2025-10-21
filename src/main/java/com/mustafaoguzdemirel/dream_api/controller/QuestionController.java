package com.mustafaoguzdemirel.dream_api.controller;

import com.mustafaoguzdemirel.dream_api.dto.request.UserAnswerRequest;
import com.mustafaoguzdemirel.dream_api.dto.response.ApiResponse;
import com.mustafaoguzdemirel.dream_api.dto.response.QuestionResponse;
import com.mustafaoguzdemirel.dream_api.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/all/{userId}")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getAllQuestionsForUser(@PathVariable UUID userId) {
        try {
            List<QuestionResponse> questions = questionService.getAllQuestionsForUser(userId);
            return ResponseEntity.ok(ApiResponse.success("Questions fetched successfully", questions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Unexpected error occurred", null));
        }
    }

    @PostMapping("/answer")
    public ResponseEntity<ApiResponse<String>> saveUserAnswer(@RequestBody UserAnswerRequest request) {
        try {
            questionService.saveUserAnswer(request);
            return ResponseEntity.ok(ApiResponse.success("Answer saved successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("INVALID_REQUEST", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("INTERNAL_ERROR", "Unexpected error occurred", null));
        }
    }

}

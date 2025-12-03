package com.mustafaoguzdemirel.dream_api.exception;

import com.mustafaoguzdemirel.dream_api.dto.response.ApiResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for all custom and system exceptions
 * All error responses follow mobile-friendly pattern: data is always null to prevent type mismatch crashes
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("USER_NOT_FOUND", ex.getMessage(), null));
    }

    @ExceptionHandler(DreamNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleDreamNotFoundException(DreamNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("DREAM_NOT_FOUND", ex.getMessage(), null));
    }

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleQuestionNotFoundException(QuestionNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("QUESTION_NOT_FOUND", ex.getMessage(), null));
    }

    @ExceptionHandler(OptionNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptionNotFoundException(OptionNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("OPTION_NOT_FOUND", ex.getMessage(), null));
    }

    @ExceptionHandler(OpenAiException.class)
    public ResponseEntity<ApiResponse<Void>> handleOpenAiException(OpenAiException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("OPENAI_ERROR", ex.getMessage(), null));
    }

    @ExceptionHandler(AnonymousUserCreationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAnonymousUserCreationException(AnonymousUserCreationException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("USER_CREATION_ERROR", ex.getMessage(), null));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDatabaseError(DataAccessException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("DB_ERROR", "Database operation failed: " + ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericError(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("UNKNOWN_ERROR", "An unexpected error occurred", null));
    }
}
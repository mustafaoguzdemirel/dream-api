package com.mustafaoguzdemirel.dream_api.exception;

import com.mustafaoguzdemirel.dream_api.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

/**
 * Global exception handler for validation errors
 * Bu handler @Valid annotation'ı ile yakalanan validation hatalarını yakalar
 */
@ControllerAdvice
public class ValidationExceptionHandler {

    /**
     * @Valid annotation'ından gelen validation hatalarını yakalar
     * Örnek: @NotBlank, @Size, @NotNull gibi
     *
     * Tüm hataları message field'ında birleştirir, data null olur (mobil crash olmaz)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        // Tüm validation hatalarını tek bir string'de birleştir
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    return fieldName + ": " + message;
                })
                .collect(Collectors.joining(". "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        "VALIDATION_ERROR",
                        errorMessage,
                        null  // ← data null, mobil crash olmaz
                ));
    }
}
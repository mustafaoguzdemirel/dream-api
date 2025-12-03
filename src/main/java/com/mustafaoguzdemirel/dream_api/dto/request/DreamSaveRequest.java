package com.mustafaoguzdemirel.dream_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DreamSaveRequest {

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotBlank(message = "Dream text cannot be empty")
    @Size(min = 10, max = 5000, message = "Dream text must be between 10 and 5000 characters")
    private String dreamText;

    @NotBlank(message = "Interpretation cannot be empty")
    @Size(max = 2000, message = "Interpretation cannot exceed 2000 characters")
    private String interpretation;
}

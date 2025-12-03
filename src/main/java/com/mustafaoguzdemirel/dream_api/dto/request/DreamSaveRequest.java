package com.mustafaoguzdemirel.dream_api.dto.request;

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
    private UUID userId;
    private String dreamText;
    private String interpretation;
}

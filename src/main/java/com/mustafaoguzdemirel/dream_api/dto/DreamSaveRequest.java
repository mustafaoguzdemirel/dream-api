package com.mustafaoguzdemirel.dream_api.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class DreamSaveRequest {
    private UUID userId;
    private String dreamText;
    private String interpretation;

    public UUID getUserId() {
        return userId;
    }

    public String getDreamText() {
        return dreamText;
    }

    public String getInterpretation() {
        return interpretation;
    }
}

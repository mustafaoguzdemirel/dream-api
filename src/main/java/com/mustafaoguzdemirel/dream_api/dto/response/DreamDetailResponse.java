package com.mustafaoguzdemirel.dream_api.dto.response;

import java.util.UUID;

public class DreamDetailResponse {
    private UUID dreamId;
    private String dreamText;
    private String interpretation;

    public DreamDetailResponse(UUID dreamId, String dreamText, String interpretation) {
        this.dreamId = dreamId;
        this.dreamText = dreamText;
        this.interpretation = interpretation;
    }

    public UUID getDreamId() { return dreamId; }
    public String getDreamText() { return dreamText; }
    public String getInterpretation() { return interpretation; }
}

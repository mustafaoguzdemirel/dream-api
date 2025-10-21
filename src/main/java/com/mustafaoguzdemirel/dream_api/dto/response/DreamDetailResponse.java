package com.mustafaoguzdemirel.dream_api.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class DreamDetailResponse {
    private UUID dreamId;
    private String dreamText;
    private String interpretation;

    private String detailedInterpretation;
    private String date;

    public DreamDetailResponse(UUID dreamId, String dreamText, String interpretation, String detailedInterpretation, LocalDateTime createdDate) {
        this.dreamId = dreamId;
        this.dreamText = dreamText;
        this.interpretation = interpretation;
        this.detailedInterpretation = detailedInterpretation;
        this.date = createdDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public UUID getDreamId() {
        return dreamId;
    }

    public String getDreamText() {
        return dreamText;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public String getDetailedInterpretation() {
        return detailedInterpretation;
    }

    public String getDate() {
        return date;
    }
}

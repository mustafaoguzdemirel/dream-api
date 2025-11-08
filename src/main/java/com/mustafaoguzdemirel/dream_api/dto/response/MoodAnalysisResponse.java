package com.mustafaoguzdemirel.dream_api.dto.response;

import com.mustafaoguzdemirel.dream_api.dto.response.DreamListItemResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MoodAnalysisResponse {
    private UUID id;
    private List<String> dominantEmotions;
    private List<String> recurringSymbols;
    private String analysis;
    private List<DreamListItemResponse> analyzedDreams;
    private String fullDate;
    private String day;
    private String month;

    public MoodAnalysisResponse(UUID id,
                                List<String> dominantEmotions,
                                List<String> recurringSymbols,
                                String analysis,
                                List<DreamListItemResponse> analyzedDreams,
                                LocalDateTime createdAt) {

        this.id = id;
        this.dominantEmotions = dominantEmotions;
        this.recurringSymbols = recurringSymbols;
        this.analysis = analysis;
        this.analyzedDreams = analyzedDreams;

        this.fullDate = createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.day = String.valueOf(createdAt.getDayOfMonth());
        this.month = createdAt.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public List<String> getDominantEmotions() {
        return dominantEmotions;
    }

    public List<String> getRecurringSymbols() {
        return recurringSymbols;
    }

    public String getAnalysis() {
        return analysis;
    }

    public List<DreamListItemResponse> getAnalyzedDreams() {
        return analyzedDreams;
    }

    public String getFullDate() {
        return fullDate;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }
}

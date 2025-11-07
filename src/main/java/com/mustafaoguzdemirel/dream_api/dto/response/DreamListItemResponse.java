package com.mustafaoguzdemirel.dream_api.dto.response;

import java.util.UUID;

public class DreamListItemResponse {
    private int day;
    private String month;
    private String dream;
    private UUID dreamId;

    public DreamListItemResponse() {

    }

    public DreamListItemResponse(int day, String month, String dream, UUID dreamId) {
        this.day = day;
        this.month = month;
        this.dream = dream;
        this.dreamId = dreamId;
    }

    public int getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getDream() {
        return dream;
    }

    public UUID getDreamId() {
        return dreamId;
    }
}

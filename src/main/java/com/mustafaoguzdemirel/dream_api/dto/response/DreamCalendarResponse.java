package com.mustafaoguzdemirel.dream_api.dto.response;

import java.util.List;
import java.util.UUID;

public class DreamCalendarResponse {
    private int year;
    private List<MonthData> months;

    public DreamCalendarResponse(int year, List<MonthData> months) {
        this.year = year;
        this.months = months;
    }

    public int getYear() { return year; }
    public List<MonthData> getMonths() { return months; }

    public static class MonthData {
        private int month; // 1-12
        private List<DayData> days;

        public MonthData(int month, List<DayData> days) {
            this.month = month;
            this.days = days;
        }

        public int getMonth() { return month; }
        public List<DayData> getDays() { return days; }
    }

    public static class DayData {
        private int day; // 1-31
        private UUID dreamId;

        public DayData(int day, UUID dreamId) {
            this.day = day;
            this.dreamId = dreamId;
        }

        public int getDay() { return day; }
        public UUID getDreamId() { return dreamId; }
    }
}

package com.mustafaoguzdemirel.dream_api.dto.response;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
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
        private String monthName; // 🔹 eklendi
        private List<DayData> days;

        public MonthData(int month, List<DayData> days) {
            this.month = month;
            this.monthName = YearMonth.of(LocalDate.now().getYear(), month)
                    .getMonth()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            this.days = days;
        }

        public int getMonth() { return month; }

        public String getMonthName() {
            return monthName;
        }

        public List<DayData> getDays() { return days; }
    }

    public static class DayData {
        private int day; // 1-31
        private UUID dreamId;
        private boolean isCurrentMonth;

        public DayData(int day, UUID dreamId, boolean isCurrentMonth) {
            this.day = day;
            this.dreamId = dreamId;
            this.isCurrentMonth = isCurrentMonth;
        }

        public int getDay() { return day; }
        public UUID getDreamId() { return dreamId; }

        public boolean isCurrentMonth() {
            return isCurrentMonth;
        }
    }
}

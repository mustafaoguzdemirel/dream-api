package com.mustafaoguzdemirel.dream_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DreamCalendarResponse {
    private int year;
    private List<MonthData> months;

    @Getter
    public static class MonthData {
        private int month; // 1-12
        private String monthName;
        private List<DayData> days;

        public MonthData(int month, List<DayData> days) {
            this.month = month;
            this.monthName = YearMonth.of(LocalDate.now().getYear(), month)
                    .getMonth()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            this.days = days;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class DayData {
        private int day; // 1-31
        private UUID dreamId;
        private boolean isCurrentMonth;
    }
}

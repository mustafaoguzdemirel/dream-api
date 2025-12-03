package com.mustafaoguzdemirel.dream_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DreamListItemResponse {
    private int day;
    private String month;
    private String dream;
    private UUID dreamId;
}

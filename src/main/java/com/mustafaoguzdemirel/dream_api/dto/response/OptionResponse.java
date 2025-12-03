package com.mustafaoguzdemirel.dream_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OptionResponse {
    private Long id;
    private String option;
    private boolean isSelected;
}

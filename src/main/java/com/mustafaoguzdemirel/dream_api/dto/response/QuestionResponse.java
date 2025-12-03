package com.mustafaoguzdemirel.dream_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String question;
    private List<OptionResponse> options;
}


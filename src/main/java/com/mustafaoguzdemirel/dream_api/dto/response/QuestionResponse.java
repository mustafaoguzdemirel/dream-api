package com.mustafaoguzdemirel.dream_api.dto.response;

import java.util.List;

public class QuestionResponse {

    private Long id;
    private String question;
    private List<OptionResponse> options;

    public QuestionResponse(Long id, String question, List<OptionResponse> options) {
        this.id = id;
        this.question = question;
        this.options = options;
    }

    public Long getId() { return id; }
    public String getQuestion() { return question; }
    public List<OptionResponse> getOptions() { return options; }
}


package com.mustafaoguzdemirel.dream_api.dto.request;

import java.util.UUID;

public class UserAnswerRequest {
    private UUID userId;
    private Long questionId;
    private Long optionId;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }
}

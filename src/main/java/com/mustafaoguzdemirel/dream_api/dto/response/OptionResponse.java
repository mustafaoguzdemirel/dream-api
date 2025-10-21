package com.mustafaoguzdemirel.dream_api.dto.response;

public class OptionResponse {

    private Long id;
    private String option;
    private boolean isSelected;

    public OptionResponse(Long id, String option, boolean isSelected) {
        this.id = id;
        this.option = option;
        this.isSelected = isSelected;
    }

    public Long getId() { return id; }
    public String getOption() { return option; }
    public boolean isSelected() { return isSelected; }
}

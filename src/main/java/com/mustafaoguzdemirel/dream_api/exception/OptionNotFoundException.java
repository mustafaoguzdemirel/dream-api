package com.mustafaoguzdemirel.dream_api.exception;

/**
 * Exception thrown when an option is not found in the database
 */
public class OptionNotFoundException extends RuntimeException {

    public OptionNotFoundException(String message) {
        super(message);
    }

    public OptionNotFoundException(Long optionId) {
        super("Option not found with ID: " + optionId);
    }
}
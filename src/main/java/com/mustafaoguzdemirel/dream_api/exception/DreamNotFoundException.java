package com.mustafaoguzdemirel.dream_api.exception;

import java.util.UUID;

/**
 * Exception thrown when a dream is not found in the database
 */
public class DreamNotFoundException extends RuntimeException {

    public DreamNotFoundException(String message) {
        super(message);
    }

    public DreamNotFoundException(UUID dreamId) {
        super("Dream not found with ID: " + dreamId);
    }
}
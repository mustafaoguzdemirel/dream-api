package com.mustafaoguzdemirel.dream_api.exception;

import java.util.UUID;

/**
 * Exception thrown when a user is not found in the database
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(UUID userId) {
        super("User not found with ID: " + userId);
    }
}
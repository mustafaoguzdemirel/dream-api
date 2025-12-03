package com.mustafaoguzdemirel.dream_api.exception;

/**
 * Exception thrown when anonymous user creation fails
 */
public class AnonymousUserCreationException extends RuntimeException {

    public AnonymousUserCreationException(String message) {
        super(message);
    }

    public AnonymousUserCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
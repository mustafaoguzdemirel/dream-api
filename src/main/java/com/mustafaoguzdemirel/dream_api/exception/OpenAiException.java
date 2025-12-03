package com.mustafaoguzdemirel.dream_api.exception;

/**
 * Exception thrown when OpenAI API returns an error or invalid response
 */
public class OpenAiException extends RuntimeException {

    public OpenAiException(String message) {
        super(message);
    }

    public OpenAiException(String message, Throwable cause) {
        super(message, cause);
    }
}
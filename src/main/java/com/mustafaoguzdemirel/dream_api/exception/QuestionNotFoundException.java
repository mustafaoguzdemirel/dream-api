package com.mustafaoguzdemirel.dream_api.exception;

/**
 * Exception thrown when a question is not found in the database
 */
public class QuestionNotFoundException extends RuntimeException {

    public QuestionNotFoundException(String message) {
        super(message);
    }

    public QuestionNotFoundException(Long questionId) {
        super("Question not found with ID: " + questionId);
    }
}
package com.example.mockproject.exception;

public class ScraperServiceException extends Exception {
    public ScraperServiceException(String message) {
        super(message);
    }

    public ScraperServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

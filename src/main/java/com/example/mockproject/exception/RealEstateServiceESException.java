package com.example.mockproject.exception;

public class RealEstateServiceESException extends RuntimeException {
    public RealEstateServiceESException(String message) {
        super(message);
    }

    public RealEstateServiceESException(String message, Throwable cause) {
        super(message, cause);
    }

}

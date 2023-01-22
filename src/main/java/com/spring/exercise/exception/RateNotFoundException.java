package com.spring.exercise.exception;

public class RateNotFoundException extends RuntimeException {

    public RateNotFoundException() {
    }

    public RateNotFoundException(String message) {
        super(message);
    }

    public RateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateNotFoundException(Throwable cause) {
        super(cause);
    }

    public RateNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

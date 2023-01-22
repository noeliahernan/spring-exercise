package com.spring.exercise.exception;

public class RateIdMismatchException extends RuntimeException {

    public RateIdMismatchException() {
    }

    public RateIdMismatchException(String message) {
        super(message);
    }

    public RateIdMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateIdMismatchException(Throwable cause) {
        super(cause);
    }

    public RateIdMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.labreh.abot.domain.exception;

public class CloseOrderException extends RuntimeException {
    public CloseOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}

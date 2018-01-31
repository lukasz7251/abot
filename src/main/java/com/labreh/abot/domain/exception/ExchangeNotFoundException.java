package com.labreh.abot.domain.exception;

public class ExchangeNotFoundException extends RuntimeException{
    public ExchangeNotFoundException(String message) {
        super(message);
    }
}

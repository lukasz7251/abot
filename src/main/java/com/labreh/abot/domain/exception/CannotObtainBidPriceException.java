package com.labreh.abot.domain.exception;

public class CannotObtainBidPriceException extends RuntimeException{
    public CannotObtainBidPriceException(String message, Throwable cause) {
        super(message, cause);
    }
}

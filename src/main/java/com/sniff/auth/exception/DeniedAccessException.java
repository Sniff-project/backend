package com.sniff.auth.exception;

public class DeniedAccessException extends RuntimeException{
    public DeniedAccessException(String message) {
        super(message);
    }
}

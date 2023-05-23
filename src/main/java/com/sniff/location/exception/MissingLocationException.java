package com.sniff.location.exception;

public class MissingLocationException extends RuntimeException {
    public MissingLocationException(String message) {
        super(message);
    }
}

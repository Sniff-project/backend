package com.sniff.location.exception;

public class RegionNotFoundException extends RuntimeException{
    public RegionNotFoundException(String message) {
        super(message);
    }
}

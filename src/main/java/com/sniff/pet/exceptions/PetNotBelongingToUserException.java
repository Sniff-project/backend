package com.sniff.pet.exceptions;

public class PetNotBelongingToUserException extends RuntimeException {
    public PetNotBelongingToUserException(String message) {
        super(message);
    }
}

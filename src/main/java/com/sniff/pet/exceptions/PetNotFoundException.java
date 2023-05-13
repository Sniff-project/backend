package com.sniff.pet.exceptions;

public class PetNotFoundException extends RuntimeException{
    public PetNotFoundException(String message){
        super(message);
    }
}

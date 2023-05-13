package com.sniff.pet.exceptions.handler;

import com.sniff.pet.exceptions.PetNotFoundException;
import com.sniff.utils.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PetExceptionsHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(PetNotFoundException.class)
    public HttpResponse handlerPetNotFoundException(PetNotFoundException e) {
        return new HttpResponse(e.getMessage());
    }
}

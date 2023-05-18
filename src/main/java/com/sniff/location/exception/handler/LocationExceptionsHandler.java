package com.sniff.location.exception.handler;

import com.sniff.location.exception.CityNotFoundException;
import com.sniff.location.exception.RegionNotFoundException;
import com.sniff.utils.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LocationExceptionsHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            CityNotFoundException.class,
            RegionNotFoundException.class
    })
    public HttpResponse handlerLocationNotFoundException(RuntimeException e) {
        return new HttpResponse(e.getMessage());
    }
}

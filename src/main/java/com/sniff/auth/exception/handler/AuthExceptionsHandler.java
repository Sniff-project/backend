package com.sniff.auth.exception.handler;

import com.sniff.auth.exception.DeniedAccessException;
import com.sniff.utils.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionsHandler {
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(DeniedAccessException.class)
    public HttpResponse handlerBadCredentialsException(DeniedAccessException e) {
        return new HttpResponse(e.getMessage());
    }
}

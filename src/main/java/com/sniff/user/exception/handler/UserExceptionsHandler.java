package com.sniff.user.exception.handler;

import com.sniff.user.exception.InvalidPhoneException;
import com.sniff.user.exception.UserExistsException;
import com.sniff.utils.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionsHandler {
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserExistsException.class)
    public HttpResponse handlerUserExistsException(UserExistsException e) {
        return new HttpResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPhoneException.class)
    public HttpResponse handlerInvalidPhoneException(InvalidPhoneException e) {
        return new HttpResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public HttpResponse handlerUsernameNotFoundException(UsernameNotFoundException e) {
        return new HttpResponse(e.getMessage());
    }
}

package com.sniff.user.exception.handler;

import com.sniff.user.exception.InvalidPasswordException;
import com.sniff.user.exception.InvalidPhoneException;
import com.sniff.user.exception.UserExistsException;
import com.sniff.user.exception.UserNotFoundException;
import com.sniff.utils.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
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
    @ExceptionHandler({
            InvalidPhoneException.class,
            InvalidPasswordException.class
    })
    public HttpResponse handlerInvalidPhoneException(RuntimeException e) {
        return new HttpResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            UsernameNotFoundException.class,
            UserNotFoundException.class
    })
    public HttpResponse handlerUsernameNotFoundException(RuntimeException e) {
        return new HttpResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BadCredentialsException.class)
    public HttpResponse handlerBadCredentialsException(BadCredentialsException e) {
        return new HttpResponse(e.getMessage());
    }
}

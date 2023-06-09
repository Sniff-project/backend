package com.sniff.utils.exception.handler;

import com.sniff.utils.HttpResponse;
import com.sniff.utils.exception.InvalidEnumValueException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionsHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        return fieldErrors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("N/A")
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        return constraintViolations.stream()
                .collect(Collectors.toMap(
                        violation -> getFieldNameFromPath(violation.getPropertyPath()),
                        ConstraintViolation::getMessage
                ));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidEnumValueException.class)
    public HttpResponse handlerInvalidEnumValueException(InvalidEnumValueException e) {
        return new HttpResponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(e.getName(), "Invalid query parameter");
        return errorMap;
    }


    private String getFieldNameFromPath(Path propertyPath) {
        String fullPath = propertyPath.toString();
        int dotIndex = fullPath.lastIndexOf(".");
        if (dotIndex != -1) {
            return fullPath.substring(dotIndex + 1);
        }
        return fullPath;
    }
}

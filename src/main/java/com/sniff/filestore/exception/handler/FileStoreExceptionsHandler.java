package com.sniff.filestore.exception.handler;

import com.sniff.filestore.exception.FileStoreException;
import com.sniff.utils.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class FileStoreExceptionsHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            FileStoreException.class,
            MaxUploadSizeExceededException.class
    })
    public HttpResponse handlerFileStoreExceptions(RuntimeException e) {
        return new HttpResponse(e.getMessage());
    }
}

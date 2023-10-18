package com.myelth.ohi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ResponseStatusException> handleGlobalExceptions(Exception ex) {
        return Mono.just(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }
}

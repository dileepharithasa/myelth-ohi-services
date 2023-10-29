package com.myelth.ohi.exceptions;

public class JsonProcessingCustomException extends RuntimeException {
    public JsonProcessingCustomException(String message) {
        super(message);
    }

    public JsonProcessingCustomException(String message, Throwable cause) {
        super(message, cause);
    }
}

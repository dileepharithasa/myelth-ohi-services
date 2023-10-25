package com.myelth.ohi.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FeignException.UnprocessableEntity.class)
    public ResponseEntity<ApiError> handleUnProcessableEntity(FeignException.UnprocessableEntity ex) {
        String responseBody = ex.contentUTF8();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        JsonNode errorDetails = jsonNode.path("o:errorDetails");
        if (errorDetails.isArray() && errorDetails.size() > 0) {
            JsonNode firstError = errorDetails.get(0);
            String errorCode = firstError.path("o:errorCode").asText();
           String errorMessage = StringUtils.isNotEmpty(firstError.path("o:title").asText())? firstError.path("o:title").asText() : firstError.path("title").asText();
            errorMessage = StringEscapeUtils.unescapeHtml4(errorMessage);
           ApiError apiException = ApiError.builder().code(errorCode).
                    message(errorMessage).status(HttpStatus.UNPROCESSABLE_ENTITY.toString()).build();
            return new ResponseEntity<>(apiException, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return null;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

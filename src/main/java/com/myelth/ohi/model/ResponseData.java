package com.myelth.ohi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResponseData<T> {
    private String message;
    private T data;

    public ResponseData(List<Provider> providerResponses) {
        this.data = (T) providerResponses;
    }
}
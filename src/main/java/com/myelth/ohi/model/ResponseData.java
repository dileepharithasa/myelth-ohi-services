package com.myelth.ohi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResponseData<T> implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;

    private String message;
    private List<? extends T> successfulResponses;
    private List<? extends T> failedResponses;
    private T data;
    public ResponseData(List<Provider> providerResponses) {
        this.data = (T) providerResponses;
    }
}
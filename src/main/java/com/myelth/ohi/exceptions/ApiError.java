package com.myelth.ohi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiError<T>  {
    private String code;
    private String message;
    private String status;
    private T data;

}

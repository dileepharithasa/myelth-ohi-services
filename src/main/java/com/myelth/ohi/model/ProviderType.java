package com.myelth.ohi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProviderType implements Serializable {
     private String flexCodeDefinitionCode;
     private String value;
}

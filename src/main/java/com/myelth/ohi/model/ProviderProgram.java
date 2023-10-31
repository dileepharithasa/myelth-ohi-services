package com.myelth.ohi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties
public class ProviderProgram implements Serializable {
    private String providerId;
    private String programId;
    private String programName;
    private String programProceduralGroupType;
    private String effectiveDate;
    private String startDate;
    private String terminationDate;
    private String lob;
    private String rate;
    private ProviderGroup providerGroup;

}
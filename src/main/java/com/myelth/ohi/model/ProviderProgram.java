package com.myelth.ohi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.myelth.ohi.model.response.ApiItem;
import com.myelth.ohi.model.response.ApiLink;
import com.myelth.ohi.model.response.DateWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties
public class ProviderProgram implements Serializable {
    private String providerId;
    private String programId;
    private String programName;
    private String proceduralGroupType;
    private String effectiveDate;
    private String startDate;
    private String terminationDate;
    private String lob;
    private String rate;
    private ProviderGroup providerGroup;

}
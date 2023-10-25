package com.myelth.ohi.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiItem implements Serializable {
    private String id;
    private boolean active;
    private String createdBy;
    private String createdByDataSet;
    private String createdByEnvironment;
    private String lastUpdatedBy;
    private String lastUpdatedByDataSet;
    private String lastUpdatedByEnvironment;
    private int objectVersionNumber;
    private String sourceCreatedBy;
    private String sourceLastUpdatedBy;
    private String uuid;
    private String code;
    private String descr;
    private List<ApiLink> links;
    private DateWrapper creationDate;
    private DateWrapper lastUpdatedDate;
    private DateWrapper sourceCreationDate;
    private DateWrapper sourceLastUpdatedDate;
}
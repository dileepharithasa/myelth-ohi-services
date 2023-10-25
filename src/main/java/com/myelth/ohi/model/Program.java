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
public class Program implements Serializable {

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

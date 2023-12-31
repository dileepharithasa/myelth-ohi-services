package com.myelth.ohi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties
public class Provider {

    private String id;
    private String code;
    private String functionDynamicLogicId;
    private String gender;
    private String name;
    private String startDate;
    private String subtype;
    private String firstName;
    private String flexCodeDefinitionCode;
    private String value;
    private String suffix;
    private String bscTin;
    private String npi;
    private String objectLastUpdatedBy;
    private String objectVersionNumber;
    private ObjectLastUpdatedDate objectLastUpdatedDate;
    private FlexCodeSystem flexCodeSystem;
    private FunctionDynamicLogic functionDynamicLogic;
    private Language language;
    //private List<RenderingAddress> renderingAddressList = null;
   // private ProviderType BSC_PROVIDER_TYPE;

    //private List<ProviderSpecialty> providerSpecialtyList = null;

}


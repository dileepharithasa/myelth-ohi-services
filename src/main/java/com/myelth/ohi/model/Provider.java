package com.myelth.ohi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Provider implements Serializable {

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
    private String bscTIN;
    private String npi;
    private String objectLastUpdatedBy;
    private String objectVersionNumber;
    private FlexCodeSystem flexCodeSystem;
    private FunctionDynamicLogic functionDynamicLogic;
    private Language language;
    private List<RenderingAddress> renderingAddressList = null;
    @JsonProperty("BSC_PROVIDER_TYPE")
    private ProviderType BSC_PROVIDER_TYPE;
    private List<Payee> payee_details_page_new;
    private List<ProviderProgram> providerGroupAffiliationList;
    private List<ServiceAddress> serviceAddressList;

}


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
public class Payee implements Serializable {

    private String providerId;
    private String providerCode;
    private String providerName;
    private String payeeBankContactName;
    private String payeeBankContactEmail;
    private String payeeBankContactTeleNumber;
    private String payeeTransferID;
    private String payeeBankRoutingNumber;
    private String payeeEffectiveDate;
    private String payeeTermDate;
    private String payeePayToAddress1;
    private String payeeBankAccountNumber;
    private String payeeUpdateDate;
    private String payeeBankName;
    private String payeePayToCity;
    private String payeeComments;
    private String payeePayToZip;
    private String programName;
    private String programID;
    private String payeeCreateDate;
    private String payeePayToAddress;
    private String tin;
    private String legalEntity;
    private String w9EffectiveDate;
    private String w9TerminationDate;
    private FlexCodeDefinition payeePayToState;
    private FlexCodeDefinition payeePaymentMethod;
    private FlexCodeDefinition payeeBankAccountType;
}

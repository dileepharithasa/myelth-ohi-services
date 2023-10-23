package com.myelth.ohi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address_Old implements Serializable {
    private String id;
    private String city;
    private String postalCode;
    private String street;
    private String houseNumber;
    private String numberAddition;
    private String startDate;
    private String endDate;
    private CountryRegion countryRegion;
    private Country country;
    private String createdBy;
    private String lastUpdatedBy;
    private String objectVersionNumber;
}
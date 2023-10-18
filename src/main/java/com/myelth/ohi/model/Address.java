package com.myelth.ohi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties
public class Address {
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
}

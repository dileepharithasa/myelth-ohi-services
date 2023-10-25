package com.myelth.ohi.utils;

import com.myelth.ohi.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;


public class DomainPopulate {

    public static Provider populateProvider(Row row) {

        Provider providerDto = new Provider();
        String code = row.getCell(0).getStringCellValue();
        providerDto.setCode(code);
        providerDto.setName(row.getCell(1).getStringCellValue());
        providerDto.setGender("M");
        //providerDto.setSubtype("NPI");
        String npi = row.getCell(7).getStringCellValue();
        providerDto.setNpi(npi);
        String tin = row.getCell(8).getStringCellValue();
        providerDto.setBscTin(tin);
        providerDto.setValue(code);
        FlexCodeSystem flexCodeSystem = new FlexCodeSystem();
        flexCodeSystem.setId("291");
        providerDto.setFlexCodeSystem(flexCodeSystem);
        FunctionDynamicLogic functionDynamicLogic = new FunctionDynamicLogic();
        functionDynamicLogic.setId("401");
        providerDto.setFunctionDynamicLogic(functionDynamicLogic);
        Language language = new Language();
        language.setId("20");
        //create API
        providerDto.setLanguage(language);
        String startDate = row.getCell(10 ).getStringCellValue();
        if(StringUtils.isNotEmpty(startDate)) {
            providerDto.setStartDate(DateFormatConversion.convertDateToOhiFormat(startDate));
        }
       // providerDto.setStartDate("2022-01-01");
        String type = row.getCell(9).getStringCellValue();
        ProviderType providerType = new ProviderType();
        providerType.setValue(type);
        providerDto.setBSC_PROVIDER_TYPE(providerType);
        providerType.setFlexCodeDefinitionCode("bsc_Provider_type");
        ServiceAddress serviceAddress = populateAddress(row);
        List<ServiceAddress> serviceAddressList = new ArrayList<>();
        if(StringUtils.equalsAnyIgnoreCase("P", type)){
            List<RenderingAddress> renderingAddressList = new ArrayList<>();
            RenderingAddress renderingAddress = new RenderingAddress();
            renderingAddress.setServiceAddress(serviceAddress);
            renderingAddress.setStartDate("2022-06-08");
            renderingAddressList.add(renderingAddress);
            providerDto.setRenderingAddressList(renderingAddressList);
        }else if(StringUtils.containsAnyIgnoreCase(type, new String[]{"I", "F", "G"})){
            serviceAddressList.add(serviceAddress);
            providerDto.setServiceAddressList(serviceAddressList);
        }

        return providerDto;
    }

    public static ServiceAddress populateAddress(Row row) {
        ServiceAddress serviceAddress = new ServiceAddress();
        String address = row.getCell(2).getStringCellValue();
        String address1 = row.getCell(3).getStringCellValue();
        String city = row.getCell(4).getStringCellValue();
        String state = row.getCell(5).getStringCellValue();
        String zip = row.getCell(6).getStringCellValue();
        Country country = new Country();
        country.setCode("US");
        CountryRegion countryRegion = new CountryRegion();
        countryRegion.setCode(state);
        serviceAddress.setCountry(country);
        serviceAddress.setCountryRegion(countryRegion);
        serviceAddress.setStreet(address);
        serviceAddress.setHouseNumber(address1);
        serviceAddress.setCity(city);
        serviceAddress.setPostalCode(zip);
        serviceAddress.setStartDate("2022-01-01");
        return serviceAddress;
    }

    public static Payee populatePayee(Row row) {
        Payee payee = new Payee();
        payee.setProviderId(row.getCell(0).getStringCellValue());
        payee.setProviderCode(row.getCell(1).getStringCellValue());
        payee.setProviderName(row.getCell(2).getStringCellValue());
        payee.setPayeeBankContactName(row.getCell(3).getStringCellValue());
        payee.setPayeeBankContactEmail(row.getCell(4).getStringCellValue());
        payee.setPayeeBankContactTeleNumber(row.getCell(5).getStringCellValue());
        payee.setPayeeTransferID(row.getCell(6).getStringCellValue());
        payee.setPayeeBankRoutingNumber(row.getCell(7).getStringCellValue());
        payee.setPayeeEffectiveDate(row.getCell(8).getStringCellValue());
        payee.setPayeePayToAddress(row.getCell(9).getStringCellValue());
        payee.setPayeePayToAddress1(row.getCell(10).getStringCellValue());
        payee.setPayeeBankAccountNumber(row.getCell(11).getStringCellValue());
        payee.setPayeeBankName(row.getCell(12).getStringCellValue());

        payee.setPayeePayToCity(row.getCell(14).getStringCellValue());
        payee.setPayeeTermDate(row.getCell(16).getStringCellValue());
        payee.setPayeeComments(row.getCell(17).getStringCellValue());
        payee.setPayeePayToZip(row.getCell(18).getStringCellValue());
        payee.setProgramID(row.getCell(19).getStringCellValue());

        FlexCodeDefinition payeeBankAccountType = new FlexCodeDefinition();
        payeeBankAccountType.setFlexCodeDefinitionCode("BSC_Bank_Account_Type");
        payeeBankAccountType.setValue(row.getCell(13).getStringCellValue());
        payee.setPayeeBankAccountType(payeeBankAccountType);

        FlexCodeDefinition payeePayToState = new FlexCodeDefinition();
        payeePayToState.setFlexCodeDefinitionCode("STATES");
        payeePayToState.setValue(row.getCell(15).getStringCellValue());
        payee.setPayeePayToState(payeePayToState);

        FlexCodeDefinition payeePaymentMethod = new FlexCodeDefinition();
        payeePaymentMethod.setFlexCodeDefinitionCode("BSC_Payment_Method");
        payeePaymentMethod.setValue(row.getCell(20).getStringCellValue());
        payee.setPayeePaymentMethod(payeePaymentMethod);

        return payee;
    }

    public static ProviderProgram populateProgram(Row row) {
        ProviderProgram providerProgram = new ProviderProgram();
        providerProgram.setProviderId(row.getCell(0).getStringCellValue());
        providerProgram.setProgramName(row.getCell(1).getStringCellValue());
        providerProgram.setProceduralGroupType(row.getCell(2).getStringCellValue());
        providerProgram.setEffectiveDate(row.getCell(3).getStringCellValue());
        providerProgram.setTerminationDate(row.getCell(4).getStringCellValue());
        providerProgram.setLob(row.getCell(5).getStringCellValue());
        providerProgram.setRate(row.getCell(6).getStringCellValue());
        if(StringUtils.isNotEmpty(providerProgram.getEffectiveDate())) {
            providerProgram.setStartDate(DateFormatConversion.convertDateToOhiFormat(providerProgram.getEffectiveDate()));
        }
        return providerProgram;
    }
}

package com.myelth.ohi.utils;

import com.myelth.ohi.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class DomainBuilder {
    private DomainBuilder() {
    }

    public static Provider buildProvider(Row row) {

        Provider provider = new Provider();
        String code = row.getCell(0).getStringCellValue();
        provider.setCode(code);
        provider.setName(row.getCell(1).getStringCellValue());
        provider.setGender("M");
        //provider.setSubtype("NPI");
        String npi = row.getCell(7).getStringCellValue();
        provider.setNpi(npi);
        String tin = row.getCell(8).getStringCellValue();
        provider.setBscTin(tin);
        provider.setValue(code);
        FlexCodeSystem flexCodeSystem = new FlexCodeSystem();
        flexCodeSystem.setId("291");
        provider.setFlexCodeSystem(flexCodeSystem);
        FunctionDynamicLogic functionDynamicLogic = new FunctionDynamicLogic();
        functionDynamicLogic.setId("401");
        provider.setFunctionDynamicLogic(functionDynamicLogic);
        Language language = new Language();
        language.setId("20");
        //create API
        provider.setLanguage(language);
        String startDate = row.getCell(10).getStringCellValue();
        if (StringUtils.isNotEmpty(startDate)) {
            provider.setStartDate(DateFormatConversion.convertDateToOhiFormat(startDate));
        }
        String type = row.getCell(9).getStringCellValue();
        ProviderType providerType = new ProviderType();
        providerType.setValue(type);
        provider.setBSC_PROVIDER_TYPE(providerType);
        providerType.setFlexCodeDefinitionCode("bsc_Provider_type");
        ServiceAddress serviceAddress = buildAddress(row);
        List<ServiceAddress> serviceAddressList = new ArrayList<>();
        if (StringUtils.equalsAnyIgnoreCase("P", type)) {
            List<RenderingAddress> renderingAddressList = new ArrayList<>();
            RenderingAddress renderingAddress = new RenderingAddress();
            renderingAddress.setServiceAddress(serviceAddress);
            renderingAddress.setStartDate(DateFormatConversion.convertDateToOhiFormat(startDate));
            renderingAddressList.add(renderingAddress);
            provider.setRenderingAddressList(renderingAddressList);
        } else if (StringUtils.containsAnyIgnoreCase(type, new String[]{"I", "F", "G"})) {
            serviceAddressList.add(serviceAddress);
            provider.setServiceAddressList(serviceAddressList);
        }

        return provider;
    }

    public static ServiceAddress buildAddress(Row row) {
        ServiceAddress serviceAddress = new ServiceAddress();
        Country country = new Country();
        country.setCode("US");
        CountryRegion countryRegion = new CountryRegion();
        countryRegion.setCode(row.getCell(5).getStringCellValue());
        serviceAddress.setCountry(country);
        serviceAddress.setCountryRegion(countryRegion);
        serviceAddress.setStreet(row.getCell(2).getStringCellValue());
        serviceAddress.setHouseNumber(row.getCell(3).getStringCellValue());
        serviceAddress.setCity(row.getCell(4).getStringCellValue());
        serviceAddress.setPostalCode(row.getCell(6).getStringCellValue());
        String startDate = row.getCell(10).getStringCellValue();
        serviceAddress.setStartDate(DateFormatConversion.convertDateToOhiFormat(startDate));
        return serviceAddress;
    }

    public static Payee buildPayee(Row row) {
        Payee payee = new Payee();
        payee.setProviderId(row.getCell(0).getStringCellValue()); //        ProviderID
        payee.setProgramID(row.getCell(1).getStringCellValue()); //        Program ID
        payee.setProgramName(row.getCell(2).getStringCellValue()); //        Program Name
        payee.setPayeeEffectiveDate(row.getCell(3).getStringCellValue());//Effective Date
        payee.setPayeeTermDate(row.getCell(4).getStringCellValue()); //Term Date
        payee.setTin(row.getCell(5).getStringCellValue());//Tax Identification Number (TIN)
        payee.setLegalEntity(row.getCell(6).getStringCellValue()); //Legal Entity Name
        payee.setW9EffectiveDate(row.getCell(7).getStringCellValue()); //W9 Effective Date
        payee.setW9TerminationDate(row.getCell(8).getStringCellValue()); //W9 Termination Date
        String cell9Val = row.getCell(9).getStringCellValue();
        String paymentType = null;
        if("Check".equalsIgnoreCase(cell9Val))  paymentType = "CHK";
        else if("EFT".equalsIgnoreCase(cell9Val))  paymentType = "EFT";

        FlexCodeDefinition payeePaymentMethod = new FlexCodeDefinition();
        payeePaymentMethod.setFlexCodeDefinitionCode("BSC_Payment_Method");
        payeePaymentMethod.setValue(paymentType);
        payee.setPayeePaymentMethod(payeePaymentMethod);

        payee.setPayeeBankName(row.getCell(10).getStringCellValue()); //Bank Name

        String cell10Val = row.getCell(11).getStringCellValue();
        String accountType = null;
        switch (cell10Val.toLowerCase()) {
            case "Checkings":
                accountType = "C";
                break;
            case "EFT":
                accountType = "S";
                break;
        }
        FlexCodeDefinition payeeBankAccountType = new FlexCodeDefinition();
        payeeBankAccountType.setFlexCodeDefinitionCode("BSC_Bank_Account_Type");
        payeeBankAccountType.setValue(accountType);
        payee.setPayeeBankAccountType(payeeBankAccountType);// Bank Account Type


        payee.setPayeeBankRoutingNumber(row.getCell(12).getStringCellValue()); //Routing Number
        payee.setPayeeBankAccountNumber(row.getCell(13).getStringCellValue()); // Account Number
        payee.setPayeeTransferID(row.getCell(14).getStringCellValue()); //Transfer ID
        payee.setPayeeBankContactName(row.getCell(15).getStringCellValue()); // Bank Contact Name
        payee.setPayeeBankContactTeleNumber(row.getCell(16).getStringCellValue()); //Bank Contact Telephone Number
        payee.setPayeeBankContactEmail(row.getCell(17).getStringCellValue()); //Bank Contact Email
        payee.setPayeePayToAddress(row.getCell(18).getStringCellValue()); // Pay-To Address
        payee.setPayeePayToAddress1(row.getCell(19).getStringCellValue()); //Pay-To Address 1
        payee.setPayeePayToCity(row.getCell(20).getStringCellValue()); //Pay-To City

        FlexCodeDefinition payeePayToState = new FlexCodeDefinition();
        payeePayToState.setFlexCodeDefinitionCode("STATES");
        payeePayToState.setValue(row.getCell(21).getStringCellValue());
        payee.setPayeePayToState(payeePayToState);//Pay-To State

        payee.setPayeePayToZip(row.getCell(22).getStringCellValue()); //Pay-To Zip
        payee.setPayeeUpdateDate(row.getCell(23).getStringCellValue()); //;Update Date
        payee.setPayeeComments(row.getCell(24).getStringCellValue()); //Comments
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        payee.setPayeeCreateDate(DateFormatConversion.convertDateToOhiFormat(todayDate));
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
        if (StringUtils.isNotEmpty(providerProgram.getEffectiveDate())) {
            providerProgram.setStartDate(DateFormatConversion.convertDateToOhiFormat(providerProgram.getEffectiveDate()));
        }
        return providerProgram;
    }
}

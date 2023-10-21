package com.myelth.ohi.controller;

import com.myelth.ohi.model.*;
import com.myelth.ohi.service.OHIProviderService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ohi-providers")
public class OHIProviderController {
    @Autowired
    OHIProviderService ohiProviderService;

    @PostMapping
    public List<Optional<Provider>> createProviders(@RequestBody List<Provider> providers) throws Exception {
        return ohiProviderService.createProviders(providers);
    }

    @PostMapping(path = "/create", consumes = {"multipart/form-data"})
    public List<Optional<Provider>> createProvider(@RequestParam("file") MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Provider> providerListToSave = new ArrayList<>();
            sheet.forEach(row -> {
                if (isNotEmptyRow(row)) {
                    providerListToSave.add(populateProvider(row));
                }
            });
            return this.createProviders(providerListToSave);
        }
    }

    private static boolean isNotEmptyRow(Row row) {
        for (Cell cell : row) {
            if (cell.getCellType() != CellType.BLANK) {
                return true;
            }
        }
        return false;
    }

    private Provider populateProvider(Row row) {

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
        providerDto.setLanguage(language);
        providerDto.setStartDate("2022-01-01");
        String type = row.getCell(9).getStringCellValue();
        ProviderType providerType = new ProviderType();
        providerType.setValue(type);
        providerDto.setBSC_PROVIDER_TYPE(providerType);
        providerType.setFlexCodeDefinitionCode("bsc_Provider_type");
        ServiceAddress serviceAddress = populateAddress(row);
        List<RenderingAddress> renderingAddressList = new ArrayList<>();
        RenderingAddress renderingAddress = new RenderingAddress();
        renderingAddress.setServiceAddress(serviceAddress);
        renderingAddress.setStartDate("2022-06-08");
        renderingAddressList.add(renderingAddress);
        providerDto.setRenderingAddressList(renderingAddressList);
        return providerDto;
    }

    private ServiceAddress populateAddress(Row row) {
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

}

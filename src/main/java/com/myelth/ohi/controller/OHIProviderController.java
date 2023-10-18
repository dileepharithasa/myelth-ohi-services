package com.myelth.ohi.controller;

import ch.qos.logback.classic.encoder.JsonEncoder;
import ch.qos.logback.core.net.ObjectWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myelth.ohi.model.*;
import com.myelth.ohi.service.OHIProviderService;
import io.swagger.v3.core.util.Json;
import org.apache.commons.math3.analysis.function.Add;
import org.apache.poi.ss.formula.functions.Count;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ohi-providers")
public class OHIProviderController {
    private final OHIProviderService ohiProviderService;
    public OHIProviderController(OHIProviderService employeeService) {
        this.ohiProviderService = employeeService;
    }
    @PostMapping("/create-provider")
    public Flux<Provider> createProviders(@RequestBody List<Provider> providers) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        String json =  mapper.writeValueAsString(providers.get(0));
        return Flux.fromIterable(providers)
                .flatMap(provider -> ohiProviderService.createProvider(provider))
                .collectList()
                .flatMapMany(Flux::fromIterable);
    }

    @PostMapping("/post-provider")
    public Flux<Provider> createMultipleEmployees(@RequestParam("file") MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Provider> providerListToSave = new ArrayList<>();
            sheet.forEach(row -> {
                if (isNotEmptyRow(row)) {
                    try {
                        providerListToSave.add(populateProvider(row));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            Flux<Provider>  fluxProvider = createProviders(providerListToSave);
            return fluxProvider;
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
    private Provider populateProvider(Row row) throws JsonProcessingException {

        Provider providerDto = new Provider();
        String code = row.getCell(0).getStringCellValue();
        providerDto.setCode(code);
        providerDto.setName(row.getCell(1).getStringCellValue());
        providerDto.setGender("M");
       // providerDto.setSubtype("NPI");
       // String npi = row.getCell(7).getStringCellValue();
       // providerDto.setNpi(npi);
      //  String tin = row.getCell(8).getStringCellValue();
       // providerDto.setBscTin(tin);
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
      //  ProviderType providerType = new ProviderType();
      //  providerType.setValue(type);
       // providerDto.setBSC_PROVIDER_TYPE(providerType);
       // providerType.setFlexCodeDefinitionCode("BSCA_Provider_ID");
        ProviderSpecialty sp = new ProviderSpecialty();
//        Address savedAddress = populateAddress(row);
//        List<RenderingAddress> lit = new ArrayList<>();
//        RenderingAddress addObj = new RenderingAddress();
//        ServiceAddress sAdd = new ServiceAddress();
//        sAdd.setId(savedAddress.getId());
//        addObj.setServiceAddress(sAdd);
//        addObj.setStartDate("2022-06-08");
//        lit.add(addObj);
//        providerDto.setRenderingAddressList(lit);
        return providerDto;
    }

    private Address populateAddress(Row row) throws JsonProcessingException {
        Address dbAddress = new Address();
        String address = row.getCell(2).getStringCellValue();
        String address1 = row.getCell(3).getStringCellValue();
        String city = row.getCell(4).getStringCellValue();
        String state = row.getCell(5).getStringCellValue();
        String zip = row.getCell(6).getStringCellValue();
       Country country = new Country();
       country.setCode("US");
       CountryRegion countryRegion = new CountryRegion();
       countryRegion.setCode(state);
        Address add = new Address();
        add.setCountry(country);
        add.setCountryRegion(countryRegion);
        add.setStreet(address);
        add.setHouseNumber(address1);
        add.setCity(city);
        add.setPostalCode(zip);
        add.setStartDate("2022-01-01");
        ObjectMapper mapper = new ObjectMapper();
        String json =  mapper.writeValueAsString(add);
        Mono<Address> addressMono =  ohiProviderService.createServiceAddress(add);

        Optional<Address> optionalAddress = addressMono.blockOptional();
        if (optionalAddress.isPresent()) {
            Address addressSaved = optionalAddress.get();
         dbAddress.setId(addressSaved.getId());
        }
        return  dbAddress;
    }

     /*providerDto.setNpi(npi);
     providerDto.setSubtype("NPI");

     providerDto.setBscTin(tin);
     providerDto.setFlexCodeDefinitionCode("BSCA_Provider_ID");
     String speciality = row.getCell(10).getStringCellValue();
     String program = row.getCell(11).getStringCellValue();
      String providerEntity = row.getCell(12).getStringCellValue();
     providerDto.setSubtype("NPI");*/
}

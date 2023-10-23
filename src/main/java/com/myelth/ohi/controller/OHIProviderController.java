package com.myelth.ohi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myelth.ohi.model.*;
import com.myelth.ohi.service.OHIProviderService;
import com.myelth.ohi.utils.DomainPopulate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xpath.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    public List<Optional<Provider>> processFiles(@RequestParam("file") List<MultipartFile> files) throws Exception {

        List<Provider> providerListToSave = new ArrayList<>();
        List<Payee> PayeeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(files)) {
            files.forEach(file -> {
                Workbook workbook = null;
                try {
                    workbook = new XSSFWorkbook(file.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Sheet sheet = workbook.getSheetAt(0);
                String fileName = file.getOriginalFilename();
                if (fileName.startsWith("provider")) {
                    sheet.forEach(row -> {
                        if (isNotEmptyRow(row)) {
                            providerListToSave.add(DomainPopulate.populateProvider(row));
                        }
                    });

                } else if (fileName.startsWith("payee")) {

                    sheet.forEach(row -> {
                        if (isNotEmptyRow(row)) {
                            PayeeList.add(DomainPopulate.populatePayee(row));
                        }
                    });
                }

            });

        } else {
            throw new Exception("Files are missing");
        }
        List<Payee> payeesTobeUpdated = new ArrayList<>();
        Map<String, List<Payee>> payeeMap = PayeeList.stream()
                .collect(Collectors.groupingBy(Payee::getProviderCode));

        providerListToSave.forEach(provider -> {
            List<Payee> providerPayeeList = new ArrayList<>();
            String key = provider.getCode();
            if (payeeMap.containsKey(key)) {
                List<Payee> payeeList = payeeMap.get(key);
                provider.setPayee_details_page_new(payeeList);
            } else {
                payeesTobeUpdated.addAll(payeeMap.get(provider.getCode()));
            }
        });

        return this.createProviders(providerListToSave);
    }


    @PostMapping(path = "/add", consumes = {"multipart/form-data"})
    public List<Optional<Provider>> uploadFiles(@RequestParam("file") List<MultipartFile> files) throws Exception {
        List<DomainObject<?>> listToPopulate = new ArrayList<>();
        List<Provider> providerListToSave = new ArrayList<>();
        List<Payee> payeeList = new ArrayList<>();

        if (CollectionUtils.isEmpty(files)) {
            throw new IllegalArgumentException("No files are uploaded");
        }
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            try (InputStream inputStream = file.getInputStream()) {
                Workbook workbook = new XSSFWorkbook(inputStream);
                Sheet sheet = workbook.getSheetAt(0);
                final boolean[] flag = {Boolean.TRUE};
                for (Row row : sheet) {
                    if (flag[0]) {
                        flag[0] = Boolean.FALSE;
                        continue;
                    }
                    boolean isRowEmpty = StreamSupport.stream(row.spliterator(), false)
                            .allMatch(cell -> cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType._NONE);

                    if (!isRowEmpty) {
                                DomainObject<?> domainObject = populateDomainObject(row, fileName);
                        if (domainObject != null) {
                            listToPopulate.add(domainObject);
                            if (domainObject.getObject() instanceof Provider) {
                                providerListToSave.add((Provider) domainObject.getObject());
                            } else if (domainObject.getObject() instanceof Payee) {
                                payeeList.add((Payee) domainObject.getObject());
                            }
                        }
                    }
                }
            } catch (IOException e) {
            }
        }
        List<Payee> payeesTobeUpdated = new ArrayList<>();
        Map<String, List<Payee>> payeeMap = payeeList.stream()
                .collect(Collectors.groupingBy(Payee::getProviderCode));

        providerListToSave.forEach(provider -> {
            List<Payee> providerPayeeList = new ArrayList<>();
            String key = provider.getCode();
            if (payeeMap.containsKey(key)) {
                List<Payee> payeeListForProvider = payeeMap.get(key);
                provider.setPayee_details_page_new(payeeListForProvider);
            } else {
                payeesTobeUpdated.addAll(payeeMap.get(provider.getCode()));
            }
        });
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(providerListToSave);
        return createProviders(providerListToSave);
    }


    private DomainObject<?> populateDomainObject(Row row, String fileName) {
        if (fileName.startsWith("Provider")) {
            Provider provider = DomainPopulate.populateProvider(row);
            return new DomainObject<>(provider);
        } else if (fileName.startsWith("Payee")) {
                Payee payee = DomainPopulate.populatePayee(row);
            return new DomainObject<>(payee);
        }
        return null;
    }

    private static boolean isNotEmptyRow(Row row) {
        for (Cell cell : row) {
            if (cell.getCellType() != CellType.BLANK) {
                return true;
            }
        }
        return false;
    }


}

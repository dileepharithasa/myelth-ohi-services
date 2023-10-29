package com.myelth.ohi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myelth.ohi.exceptions.ApiError;
import com.myelth.ohi.exceptions.JsonProcessingCustomException;
import com.myelth.ohi.external.OhiFeignClient;
import com.myelth.ohi.model.*;
import com.myelth.ohi.model.response.ApiItem;
import com.myelth.ohi.model.response.ApiResponse;
import com.myelth.ohi.utils.DomainBuilder;
import com.myelth.ohi.utils.ExcelGenerator;
import feign.FeignException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class OHIProviderService {

    @Autowired
    private OhiFeignClient ohiFeignClient;
    @Autowired
    private OHIProviderService selfOhiService;

    public List<Provider> createProviders(List<Provider> providerList){
        List<Provider> successResponses = new ArrayList<>();
        List<ApiError> errorResponses = new ArrayList<>();
            providerList.stream().forEach( provider->{
                if(StringUtils.equals("P", provider.getBSC_PROVIDER_TYPE().getValue())) {
                    List<RenderingAddress> renderingAddressList = new ArrayList<>();
                    provider.getRenderingAddressList().forEach(r->{
                        RenderingAddress reD = new RenderingAddress();
                        reD.setStartDate(r.getStartDate());

                        Optional<ServiceAddress> serviceAddressOptional = this.createServiceAddress(r.getServiceAddress());
                        if(serviceAddressOptional.isPresent()){
                            reD.setServiceAddress(ServiceAddress.builder().id(serviceAddressOptional.get().getId()).startDate(serviceAddressOptional.get().getStartDate()).build());
                        }
                        renderingAddressList.add(reD);
                    });
                    provider.setRenderingAddressList(renderingAddressList);
                    try {
                        Optional<Provider> providerResponse = ohiFeignClient.createIndividualProviders(provider);
                        providerResponse.ifPresent(providerCreated -> successResponses.add(providerCreated));

                    }catch (FeignException.UnprocessableEntity ex){
                        buildFailureResponse(ex,provider);
                        Optional.ofNullable(buildFailureResponse(ex, provider))
                                .ifPresent(errorResponses::add);
                    }
                }
                try {
                    Optional<Provider> providerResponse = ohiFeignClient.createOrganizationProviders(provider);
                    providerResponse.ifPresent(providerCreated -> successResponses.add(providerCreated));
                }catch (FeignException.UnprocessableEntity ex){
                    Optional.ofNullable(buildFailureResponse(ex, provider))
                            .ifPresent(errorResponses::add);

                }

            });
            if(CollectionUtils.isNotEmpty(errorResponses)){
                ExcelGenerator.generateProviderFallOut(errorResponses);
            }

            return successResponses;
    }

    private ApiError buildFailureResponse(FeignException.UnprocessableEntity ex, Provider provider) {
        ApiError apiException = null;
        String responseBody = ex.contentUTF8();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            throw new JsonProcessingCustomException("Error processing JSON response", e);

        }
        JsonNode errorDetails = jsonNode.path("o:errorDetails");
        if (errorDetails.isArray() && errorDetails.size() > 0) {
            JsonNode firstError = errorDetails.get(0);
            String errorCode = firstError.path("o:errorCode").asText();
            String errorMessage = StringUtils.isNotEmpty(firstError.path("o:title").asText())? firstError.path("o:title").asText() : firstError.path("title").asText();
            errorMessage = StringEscapeUtils.unescapeHtml4(errorMessage);
            apiException = ApiError.builder().code(errorCode).
                    message(errorMessage).status(HttpStatus.UNPROCESSABLE_ENTITY.toString()).build();
        }
        apiException.setData(provider);
        return apiException;
    }

    public Optional<Provider> createProvider(Provider provider) {
        ServiceAddress serviceAddress = provider.getRenderingAddressList().get(0).getServiceAddress();
       Optional<ServiceAddress> optionalServiceAddress =  createServiceAddress(serviceAddress);
        if(optionalServiceAddress.isPresent()){
            provider.getRenderingAddressList().get(0).
                    setServiceAddress(ServiceAddress.builder().id(optionalServiceAddress.get().getId()).build());
        }
        return ohiFeignClient.createIndividualProviders(provider);
    }
    public Optional<ServiceAddress> getServiceAddress(SearchCriteria searchCriteria) {
        return  ohiFeignClient.getServiceAddress(searchCriteria);
    }

    public Optional<ServiceAddress> createServiceAddress(ServiceAddress serviceAddress) {
        String query = getQueryString(serviceAddress);
        Resource resource = new Resource();
        resource.setQ(query);
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setResource(resource);
        Optional<ServiceAddress> optionalAddress = this.getServiceAddress(searchCriteria);
        return (optionalAddress.isPresent()) ? optionalAddress : ohiFeignClient.createServiceAddress(serviceAddress);
    }
    private static String getQueryString(ServiceAddress address) {
        List<String> conditions = new ArrayList<>();

        if (StringUtils.isNoneEmpty(address.getStartDate())) {
            conditions.add("startDate.eq('%s')");
        }
        if (StringUtils.isNoneEmpty(address.getCity())) {
            conditions.add("city.eq('%s')");
        }
        if (StringUtils.isNoneEmpty(address.getPostalCode())) {
            conditions.add("postalCode.eq('%s')");
        }
        if (StringUtils.isNoneEmpty(address.getStreet())) {
            conditions.add("street.eq('%s')");
        }
        if (StringUtils.isNoneEmpty(address.getHouseNumber())) {
            conditions.add("houseNumber.eq('%s')");
        }

        StringJoiner joiner = new StringJoiner(".and.");
        for (String condition : conditions) {
            joiner.add(condition);
        }

        String query = joiner.toString();
        return String.format(query, address.getStartDate(), address.getCity(), address.getPostalCode(),
                address.getStreet(), address.getHouseNumber());
    }



    @Cacheable(value = "programsCache", key = "#result ?: 'nullResult'")
    public Map<String, Program> getCachedProviderGroups() {
        Resource resource = new Resource();
        resource.setQ("");
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setResource(resource);
        ResponseEntity<String> response = ohiFeignClient.getProviderGroups(searchCriteria);
        String jsonResponse = response.getBody();
        ApiResponse apiResponse = null;
        try {
            apiResponse = new ObjectMapper().readValue(jsonResponse, ApiResponse.class);
        } catch (JsonProcessingException e) {
            throw new JsonProcessingCustomException("Error processing JSON response", e);
        }
        List<ApiItem> apiItems = apiResponse.getItems();
        List<Program> ps = apiItems.stream().map(apiItem -> new ObjectMapper().convertValue(apiItem, Program.class))
                .collect(Collectors.toList());
        Map<String, Program> programMap = new HashMap<>();
        for (Program p : ps) {
            if (programMap.put(p.getDescr(), p) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        return programMap != null ? programMap : Collections.emptyMap();
    }

    public List<Provider> loadProvidersToOhi(List<MultipartFile> files)  {
        List<DomainObject<?>> listToPopulate = new ArrayList<>();
        List<Provider> providerListToSave ;
        List<Payee> payeeList = new ArrayList<>();
        List<ProviderProgram> providerProgramList = new ArrayList<>();

        if (CollectionUtils.isEmpty(files)) {
            throw new IllegalArgumentException("No files are uploaded");
        }
        Map<String, Provider> providerRequestMap = new HashMap<>();
        for (MultipartFile file : files) {
            loadProviderFromFile(listToPopulate, payeeList, providerProgramList, providerRequestMap, file);
        }
        providerListToSave = new ArrayList<>(providerRequestMap.values());
        List<Payee> payeesTobeUpdated = new ArrayList<>();
        ArrayList<ProviderProgram> providerProgramsTobeUpdated = new ArrayList<>();
        Map<String, Program> programsCachedMap = selfOhiService.getCachedProviderGroups();

        programDataLookupForPayee(payeeList, programsCachedMap);
        Map<String, List<Payee>> payeeMap = payeeList.stream().collect(Collectors.groupingBy(Payee::getProviderId));

        programDataLookupForProviderGroup(providerProgramList, programsCachedMap);
        Map<String, List<ProviderProgram>> providerProgramMap = providerProgramList.stream().collect(Collectors.groupingBy(ProviderProgram::getProviderId));
        providerListToSave.forEach(provider -> {
            String key = provider.getCode();
            List<Payee> payeeListForProvider = payeeMap.getOrDefault(key, new ArrayList<>());
            provider.setPayee_details_page_new(payeeListForProvider);
            if (!payeeMap.containsKey(key)) {
                payeesTobeUpdated.addAll(payeeListForProvider);
            }
            List<ProviderProgram> providerProgramListToSave = providerProgramMap.getOrDefault(key, new ArrayList<>());
            provider.setProviderGroupAffiliationList(providerProgramListToSave);
            if (!providerProgramMap.containsKey(key)) {
                providerProgramsTobeUpdated.addAll(providerProgramListToSave);
            }

        });
        return this.createProviders(providerListToSave);
    }

    private static void programDataLookupForProviderGroup(List<ProviderProgram> providerProgramList, Map<String, Program> programsCachedMap) {
        providerProgramList.forEach(providerProgram->{
            if(programsCachedMap.containsKey(providerProgram.getProgramName())){
                Program cachedProgram = programsCachedMap.get(providerProgram.getProgramName());
                providerProgram.setProviderGroup(ProviderGroup.builder().id(cachedProgram.getId()).build());
                providerProgram.setProgramId(cachedProgram.getId());
            }
        });
    }

    private static void programDataLookupForPayee(List<Payee> payeeList, Map<String, Program> programsCachedMap) {
        payeeList.forEach(payee->{
            if(programsCachedMap.containsKey(payee.getProgramName())){
                Program cachedProgram = programsCachedMap.get(payee.getProgramName());
                payee.setProgramID(cachedProgram.getCode());
            }
        });
    }

    private void loadProviderFromFile(List<DomainObject<?>> listToPopulate, List<Payee> payeeList, List<ProviderProgram> providerProgramList, Map<String, Provider> providerRequestMap, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            boolean flag = Boolean.TRUE;
            for (Row row : sheet) {
                if (flag) {
                    flag = Boolean.FALSE;
                    continue;
                }
                boolean isRowEmpty = StreamSupport.stream(row.spliterator(), false)
                        .allMatch(cell -> cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType._NONE);
                if (!isRowEmpty) {
                    processEachRow(listToPopulate, payeeList, providerProgramList, providerRequestMap, fileName, row);
                }
            }
        } catch (IOException e) {
           //error
        }
    }

    private void processEachRow(List<DomainObject<?>> listToPopulate, List<Payee> payeeList, List<ProviderProgram> providerProgramList, Map<String, Provider> providerRequestMap, String fileName, Row row) {
        DomainObject<?> domainObject = populateDomainObject(row, fileName);
        if (domainObject != null) {
            listToPopulate.add(domainObject);
            if (domainObject.getObject() instanceof Provider) {
                Provider providerReq = (Provider) domainObject.getObject();
                if(providerRequestMap.containsKey(providerReq.getId())){
                    Provider updatedProviderReq  = providerRequestMap.get(providerReq.getId());
                    updatedProviderReq.getRenderingAddressList().addAll(providerReq.getRenderingAddressList());
                    providerRequestMap.put(providerReq.getId(), updatedProviderReq);
                }else {
                    providerRequestMap.put(providerReq.getId(), providerReq);
                }
            } else if (domainObject.getObject() instanceof Payee) {
                payeeList.add((Payee) domainObject.getObject());
            }else if (domainObject.getObject() instanceof ProviderProgram) {
                providerProgramList.add((ProviderProgram) domainObject.getObject());
            }
        }
    }
    private DomainObject<?> populateDomainObject(Row row, String fileName) {
        if (fileName.startsWith("Provider")) {
            Provider provider = DomainBuilder.buildProvider(row);
            return new DomainObject<>(provider);
        } else if (fileName.contains("Payee")) {
            Payee payee = DomainBuilder.buildPayee(row);
            return new DomainObject<>(payee);
        }else if (fileName.startsWith("Program")) {
            ProviderProgram providerProgram = DomainBuilder.populateProgram(row);
            return new DomainObject<>(providerProgram);
        }
        return null;
    }


}

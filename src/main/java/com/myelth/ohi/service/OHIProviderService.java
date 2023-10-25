package com.myelth.ohi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myelth.ohi.external.OhiFeignClient;
import com.myelth.ohi.model.*;
import com.myelth.ohi.model.response.ApiItem;
import com.myelth.ohi.model.response.ApiResponse;
import com.myelth.ohi.utils.DomainPopulate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
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

    public List<Optional<Provider>> createProviders(List<Provider> providerList){
        return providerList.stream()
                .map(provider -> {
                    if(StringUtils.equals("P", provider.getBSC_PROVIDER_TYPE().getValue())) {
                        Optional<ServiceAddress> serviceAddress = this.createServiceAddress(provider.getRenderingAddressList().get(0).getServiceAddress());
                        provider.getRenderingAddressList().get(0).setServiceAddress(ServiceAddress.builder().id(serviceAddress.get().getId()).build());
                        return ohiFeignClient.createIndividualProviders(provider);
                    }
                    return ohiFeignClient.createOrganizationProviders(provider);
                })
                .collect(Collectors.toList());
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
        Optional<ServiceAddress> serviceAddress = ohiFeignClient.getServiceAddress(searchCriteria);
        return serviceAddress;
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
        StringBuilder builder = new StringBuilder();

        if (StringUtils.isNoneEmpty(address.getStartDate())) {
            builder.append("startDate.eq('%s')");
        }
        if (StringUtils.isNoneEmpty(address.getCity())) {
            builder.append(".and.");
            builder.append("city.eq('%s')");
        }
        if (StringUtils.isNoneEmpty(address.getPostalCode())) {
            builder.append(".and.");
            builder.append("postalCode.eq('%s')");
        }
        if (StringUtils.isNoneEmpty(address.getStreet())) {
            builder.append(".and.");
            builder.append("street.eq('%s')");
        }
        if (StringUtils.isNoneEmpty(address.getHouseNumber())) {
            builder.append(".and.");
            builder.append("houseNumber.eq('%s')");
        }
        String query = String.format(builder.toString(), address.getStartDate(), address.getCity(),
                address.getPostalCode(), address.getStreet(), address.getHouseNumber());
        return query;
    }

    @Cacheable(value = "programsCache", key = "#result ?: 'nullResult'")
    public Map<String, Program> getCachedProviderGroups() throws JsonProcessingException {
        Resource resource = new Resource();
        resource.setQ("");
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setResource(resource);
        ResponseEntity<String> response = ohiFeignClient.getProviderGroups(searchCriteria);
        String jsonResponse = response.getBody();
        ApiResponse apiResponse = new ObjectMapper().readValue(jsonResponse, ApiResponse.class);
        List<ApiItem> apiItems = apiResponse.getItems();
        List<Optional<Program>> programs = new ArrayList<>();

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

    public List<Optional<Provider>> loadProvidersToOhi(List<MultipartFile> files) throws Exception {
        List<DomainObject<?>> listToPopulate = new ArrayList<>();
        List<Provider> providerListToSave = new ArrayList<>();
        List<Payee> payeeList = new ArrayList<>();
        List<ProviderProgram> providerProgramList = new ArrayList<>();

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
                            }else if (domainObject.getObject() instanceof ProviderProgram) {
                                providerProgramList.add((ProviderProgram) domainObject.getObject());
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new Exception("Error in file processing");
            }
        }
        List<Payee> payeesTobeUpdated = new ArrayList<>();
        ArrayList<Payee> providerProgramsTobeUpdated = new ArrayList<>();
        Map<String, List<Payee>> payeeMap = payeeList.stream().collect(Collectors.groupingBy(Payee::getProviderCode));
        Map<String, Program> programsCachedMap = selfOhiService.getCachedProviderGroups();
        providerProgramList.forEach(providerProgram->{
            if(programsCachedMap.containsKey(providerProgram.getProgramName())){
                Program cachedProgram = programsCachedMap.get(providerProgram.getProgramName());
                providerProgram.setProviderGroup(ProviderGroup.builder().id(cachedProgram.getId()).build());
            }
        });
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
                providerProgramsTobeUpdated.addAll(payeeListForProvider);
            }

        });
        return this.createProviders(providerListToSave);
    }
    private DomainObject<?> populateDomainObject(Row row, String fileName) {
        if (fileName.startsWith("Provider")) {
            Provider provider = DomainPopulate.populateProvider(row);
            return new DomainObject<>(provider);
        } else if (fileName.contains("Payee")) {
            Payee payee = DomainPopulate.populatePayee(row);
            return new DomainObject<>(payee);
        }else if (fileName.startsWith("Program")) {
            ProviderProgram providerProgram = DomainPopulate.populateProgram(row);
            return new DomainObject<>(providerProgram);
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

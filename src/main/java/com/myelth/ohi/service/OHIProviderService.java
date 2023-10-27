package com.myelth.ohi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myelth.ohi.model.Program;
import com.myelth.ohi.model.Provider;
import com.myelth.ohi.model.SearchCriteria;
import com.myelth.ohi.model.ServiceAddress;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OHIProviderService {
    List<Optional<Provider>> createProviders(List<Provider> providerList) ;
    Optional<Provider> createProvider(Provider provider);

    Optional<ServiceAddress> getServiceAddress(SearchCriteria searchCriteria);

    Optional<ServiceAddress> createServiceAddress(ServiceAddress serviceAddress);

    Map<String, Program> getCachedProviderGroups() throws JsonProcessingException;

    List<Optional<Provider>> loadProvidersToOhi(List<MultipartFile> files) throws Exception;

}

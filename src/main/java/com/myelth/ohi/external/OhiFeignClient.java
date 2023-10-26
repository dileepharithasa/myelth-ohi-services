package com.myelth.ohi.external;

import com.myelth.ohi.config.FeignClientConfiguration;
import com.myelth.ohi.model.Provider;
import com.myelth.ohi.model.SearchCriteria;
import com.myelth.ohi.model.ServiceAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@FeignClient(name = "provider", url = "${ohi.baseUrl}", configuration = FeignClientConfiguration.class)
@Service
public interface OhiFeignClient {

    @GetMapping("/serviceaddresses/search")
    Optional<ServiceAddress> getServiceAddress(SearchCriteria searchCriteria);

    @PostMapping("/serviceaddresses")
    Optional<ServiceAddress> createServiceAddress(ServiceAddress serviceAddress);

    @PostMapping("/individualproviders")
    Optional<Provider> createIndividualProviders(Provider provider);

    @PostMapping("/organizationproviders")
    Optional<Provider> createOrganizationProviders(Provider provider);

    @GetMapping("/individualproviders/{providerId}")
    Optional<ServiceAddress> getIndividualProvider(String providerId);

    @PostMapping("/individualproviders/search")
    Optional<ServiceAddress> getIndividualProvider(SearchCriteria searchCriteria);

    @PostMapping(value="/providergroups/search", consumes = "application/vnd.oracle.insurance.resource+json", produces = "application/vnd.oracle.insurance.resource+json")
    ResponseEntity<String> getProviderGroups(SearchCriteria searchCriteria);

}
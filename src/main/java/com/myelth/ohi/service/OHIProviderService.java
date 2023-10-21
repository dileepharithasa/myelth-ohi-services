package com.myelth.ohi.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myelth.ohi.external.OhiFeignClient;
import com.myelth.ohi.model.Provider;
import com.myelth.ohi.model.Resource;
import com.myelth.ohi.model.SearchCriteria;
import com.myelth.ohi.model.ServiceAddress;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OHIProviderService {

    @Autowired
    private OhiFeignClient ohiFeignClient;


    private final String SERVICE_ADDRESS_URI = "/serviceaddresses";
    private final String SERVICE_ADDRESS_SEARCH_URI = "/serviceaddresses/search";
    private final String INDIVIDUAL_PROVIDER_URI ="/individualproviders";


    @Autowired
    private WebClient ohiWebClient;
    public List<Optional<Provider>> createProviders(List<Provider> providerList){

         return providerList.stream()
                .map(provider -> {

                    Optional<ServiceAddress> serviceAddress = this.createServiceAddress(provider.getRenderingAddressList().get(0).getServiceAddress());
                    provider.getRenderingAddressList().get(0).setServiceAddress(ServiceAddress.builder().id(serviceAddress.get().getId()).build());
                    return ohiFeignClient.createIndividualProviders(provider);

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
        String json = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
             json = mapper.writeValueAsString(provider);
        }catch (Exception ex){
            ex.printStackTrace();
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

}

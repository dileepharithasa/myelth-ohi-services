package com.myelth.ohi.service;

import com.myelth.ohi.model.CreateProviderRequest;
import com.myelth.ohi.model.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ApiService {



    public Mono<String> callExternalApiInLoop(CreateProviderRequest createProviderRequest) {
        // Initialize an empty StringBuilder to accumulate responses
        StringBuilder responseBuilder = new StringBuilder();
        try {

            return Mono.defer(() -> {
                Mono<Provider> result = Mono.empty();

                for (Provider pro : createProviderRequest.getProviderList()) {
                    // Make an API request and append the response to the StringBuilder
                    result = result.then(Mono.fromSupplier(() -> {
                        Provider response =
                                webClient().post()
                                        .uri("/api/generic/individualproviders") // Replace with your API endpoint
                                        .retrieve()
                                        .bodyToMono(Provider.class)
                                        .block();

                        responseBuilder.append(response).append("\n");
                        return response;
                    }));
                }

                return result;
            }).thenReturn(responseBuilder.toString());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://devohi.myelth.com:7003/").defaultHeaders(httpHeaders -> {
                    httpHeaders.setBasicAuth("dileep1", "Ohipass!23");
                })
                .build();
    }
}
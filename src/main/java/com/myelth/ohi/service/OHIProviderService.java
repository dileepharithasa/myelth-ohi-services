package com.myelth.ohi.service;

import com.myelth.ohi.model.Address;
import com.myelth.ohi.model.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OHIProviderService {

    @Autowired
    public OHIProviderService(WebClient.Builder webClientBuilder) {
    }

    public Mono<Provider> createProvider(Provider provider) {
        return webClient()
            .post()
            .uri("/api/generic/individualproviders")
            .body(Mono.just(provider), Provider.class)
            .retrieve()
            .bodyToMono(Provider.class);
    }
    public Mono<Address> createServiceAddress(Address address) {
        return webClient()
                .post()
                .uri("api/generic/serviceaddresses")
                .body(Mono.just(address), Address.class)
                .retrieve()
                .bodyToMono(Address.class);

    }

    public Mono<String> createAndHandleProvider(Provider provider) {
        return webClient()
                .post()
                .uri("/api/generic/individualproviders")
                .body(Mono.just(provider), Provider.class)
                .retrieve()
                .bodyToMono(Provider.class)
                .flatMap(createdProvider -> {
                    if (createdProvider != null) {
                        // Successfully created the provider, return a success message
                        return Mono.just("Provider created successfully with ID: " + createdProvider.getId());
                    } else {
                        // Failed to create the provider, return a failure message
                        return Mono.just("Failed to create the provider");
                    }
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    // Handle HTTP status errors (4xx or 5xx) here
                    if (ex.getStatusCode().is4xxClientError() || ex.getStatusCode().is5xxServerError()) {
                        // You can log the error or return a specific error message
                        return Mono.just("HTTP error: " + ex.getRawStatusCode() + " - " + ex.getStatusText());
                    } else {
                        // Re-throw other exceptions
                        return Mono.error(ex);
                    }
                });
    }

    public Flux<String>  createAndHandleProviderNew(Provider provider) {
        return Flux.defer(() ->
                webClient()
                        .post()
                        .uri("/api/generic/individualproviders")
                        .body(Mono.just(provider), Provider.class)
                        .retrieve()
                        .bodyToMono(Provider.class)
                        .flatMap(createdProvider -> {
                            if (createdProvider != null) {
                                // Successfully created the provider, emit a success message
                                return Mono.just("Provider created successfully with ID: " + createdProvider.getId());
                            } else {
                                // Failed to create the provider, emit a failure message
                                return Mono.just("Failed to create the provider");
                            }
                        })
                        .onErrorResume(WebClientResponseException.class, ex -> {
                            // Handle HTTP status errors (4xx or 5xx) here
                            if (ex.getStatusCode().is4xxClientError() || ex.getStatusCode().is5xxServerError()) {
                                // You can log the error or emit a specific error message
                                return Mono.just("HTTP error: " + ex.getRawStatusCode() + " - " + ex.getStatusText());
                            } else {
                                // Re-throw other exceptions
                                return Mono.error(ex);
                            }
                        })
        );
    }



    private WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://devohi.myelth.com:7003/").defaultHeaders(httpHeaders -> {
                    httpHeaders.setBasicAuth("dileep1", "Ohipass!23");
                })
                .build();
    }
}

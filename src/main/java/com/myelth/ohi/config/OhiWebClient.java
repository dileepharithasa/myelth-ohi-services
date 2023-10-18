package com.myelth.ohi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

//@Configuration
public class OhiWebClient {
    private WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://devohi.myelth.com:7003/").defaultHeaders(httpHeaders -> {
                    httpHeaders.setBasicAuth("dileep1", "Ohipass!23");
                })
                .build();
    }
}

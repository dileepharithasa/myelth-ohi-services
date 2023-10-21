package com.myelth.ohi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OhiWebClient {

    @Value("${ohi.baseurl}")
    private String ohiBaseUrl;

    @Value("${ohi.userName}")
    private String ohiUserName;

    @Value("${ohi.password}")
    private String ohiPassword;

    @Bean
    public WebClient OhiWebClient() {
        return WebClient.builder()
                .baseUrl(ohiBaseUrl).defaultHeaders(httpHeaders -> {
                    httpHeaders.setBasicAuth(ohiUserName, ohiPassword);
                })
                .build();
    }
}

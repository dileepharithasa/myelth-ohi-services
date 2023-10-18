package com.myelth.ohi.controller;

import com.myelth.ohi.model.CreateProviderRequest;
import com.myelth.ohi.model.Provider;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/providers")
public class ProviderController {

    @PostMapping
    @RequestMapping("/addProvider")
    public ResponseEntity createProvider(@RequestBody CreateProviderRequest createProviderRequest) {
        List<Provider> providerList = createProviderRequest.getProviderList();
        String url = "http://devohi.myelth.com:7003/api/generic/individualproviders";

        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication("dileep1", "Ohipass!23")
                .build();
        List<Provider> list = new ArrayList<>();

        for (Provider p : providerList) {

            ResponseEntity<Provider> response =
                    restTemplate.postForEntity(url, p, Provider.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                list.add(response.getBody());
            } else {
                // Handle error responsejee
                System.out.println("error");
            }
        }

        // Process the response
        return new ResponseEntity<>(list, HttpStatus.CREATED);


    }

    private WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://devohi.myelth.com:7003/")
                .build();
    }
}

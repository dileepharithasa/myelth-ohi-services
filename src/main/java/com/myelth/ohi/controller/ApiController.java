package com.myelth.ohi.controller;

import com.myelth.ohi.model.CreateProviderRequest;
import com.myelth.ohi.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    ApiService apiService;

    @PostMapping("/call-api")
    public Mono<String> callExternalApi(@RequestBody CreateProviderRequest createProviderRequest) {

        return apiService.callExternalApiInLoop(createProviderRequest);
    }
}
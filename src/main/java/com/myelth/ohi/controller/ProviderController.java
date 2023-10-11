package com.myelth.ohi.controller;

import com.myelth.ohi.openapi.api.ProvidersApi;
import com.myelth.ohi.openapi.model.InlineResponse200;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ProviderController implements ProvidersApi {

    @Override
    public ResponseEntity<InlineResponse200> createProviders(List<MultipartFile> filename) {
        return new ResponseEntity<>(new InlineResponse200(), HttpStatus.CREATED);
    }
}

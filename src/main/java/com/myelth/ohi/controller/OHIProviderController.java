package com.myelth.ohi.controller;

import com.myelth.ohi.model.Provider;
import com.myelth.ohi.model.ResponseData;
import com.myelth.ohi.service.OHIProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ohi-providers")
public class OHIProviderController {
    @Autowired
    OHIProviderService ohiProviderService;

    @PostMapping("/create")
    public List<Optional<Provider>> createProviders(@RequestBody List<Provider> providers) {
        return ohiProviderService.createProviders(providers);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseData> loadProvidersToOHI(@RequestParam("file") List<MultipartFile> files) throws Exception  {
        List<Optional<Provider>> list =  ohiProviderService.loadProvidersToOhi(files);
        return new ResponseEntity<>(ResponseData.builder().data(list).build(), HttpStatus.CREATED);
    }
}

package com.myelth.ohi.controller;

import com.myelth.ohi.model.Provider;
import com.myelth.ohi.model.ResponseData;
import com.myelth.ohi.service.impl.OhiProviderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/ohi-providers")
public class OhiProviderController {
    @Autowired
    OhiProviderServiceImpl ohiProviderService;

    @PostMapping("/create")
    public ResponseEntity<ResponseData> createProviders(@RequestBody List<Provider> providers) {
        List<Provider> list =  ohiProviderService.createProviders(providers);
        return  new ResponseEntity<>(ResponseData.builder().data(list).build(), HttpStatus.CREATED);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseData> loadProvidersToOHI(@RequestParam("file") List<MultipartFile> files)  {
        List<Provider> list =  ohiProviderService.loadProvidersToOhi(files);
        return new ResponseEntity<>(ResponseData.builder().data(list).build(), HttpStatus.CREATED);
    }
}

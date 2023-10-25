package com.myelth.ohi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myelth.ohi.exceptions.ApiError;
import com.myelth.ohi.model.*;
import com.myelth.ohi.service.OHIProviderService;
import com.myelth.ohi.utils.DomainPopulate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/ohi-providers")
public class OHIProviderController {
    @Autowired
    OHIProviderService ohiProviderService;

    @PostMapping("/create")
    public List<Optional<Provider>> createProviders(@RequestBody List<Provider> providers) throws Exception {
        return ohiProviderService.createProviders(providers);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseData> loadProvidersToOHI(@RequestParam("file") List<MultipartFile> files) throws JsonProcessingException, Exception  {
        List<Optional<Provider>> list =  ohiProviderService.loadProvidersToOhi(files);
        return new ResponseEntity<>(ResponseData.builder().data(list).build(), HttpStatus.CREATED);
    }
}

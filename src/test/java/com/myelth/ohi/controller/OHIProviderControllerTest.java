package com.myelth.ohi.controller;

import com.myelth.ohi.model.MockData;
import com.myelth.ohi.model.Provider;
import com.myelth.ohi.model.ResponseData;
import com.myelth.ohi.service.impl.OhiProviderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.events.ProcessingInstruction;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OHIProviderControllerTest {
    /**
     * Implementation TO-DO
     */
    @InjectMocks
    private OhiProviderController providerController;

    @Mock
    private OhiProviderServiceImpl providerService;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testCreateProviders() {
        List<Provider> providers = new ArrayList<>();
        Provider mockedProvider = MockData.createMockedProvider();
        providers.add(mockedProvider);
        Mockito.when(providerService.createProviders(providers)).thenReturn(providers);
        ResponseEntity<ResponseData> response = providerController.createProviders(providers);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testLoadProvidersToOHI() {
        List<MultipartFile> files = new ArrayList<>();
        List<Provider> expectedProviders = new ArrayList<>();
        Provider mockedProvider = MockData.createMockedProvider();
        expectedProviders.add(mockedProvider);
        Mockito.when(providerService.loadProvidersToOhi(files)).thenReturn(expectedProviders);
        ResponseEntity<ResponseData> response = providerController.loadProvidersToOHI(files);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testLoadProvidersToOHIWithEmptyFiles() {
        List<MultipartFile> files = new ArrayList<>();
        ResponseEntity<ResponseData> response = providerController.loadProvidersToOHI(files);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}

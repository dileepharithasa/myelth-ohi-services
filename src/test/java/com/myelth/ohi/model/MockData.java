package com.myelth.ohi.model;

import com.myelth.ohi.model.Provider;

public class MockData {

    public static Provider createMockedProvider() {
        Provider mockedProvider = new Provider();
        mockedProvider.setId("1");
        mockedProvider.setCode("PROV123");
        mockedProvider.setName("Mocked Provider Name");
        mockedProvider.setNpi("1234567890");
        // Set other properties as needed
        return mockedProvider;
    }
}

package com.myelth.ohi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myelth.ohi.service.impl.OhiProviderServiceImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class MyelthOhiServicesApplication {

	@Autowired
	private OhiProviderServiceImpl ohiProviderService;

	public static void main(String[] args) {
		SpringApplication.run(MyelthOhiServicesApplication.class, args);
	}

	@PostConstruct
	public void loadDataOnStartup() throws JsonProcessingException {
		 ohiProviderService.getCachedProviderGroups();
	}

}

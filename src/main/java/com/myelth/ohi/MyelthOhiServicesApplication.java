package com.myelth.ohi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.myelth.ohi.model.Program;
import com.myelth.ohi.model.Resource;
import com.myelth.ohi.model.SearchCriteria;
import com.myelth.ohi.model.response.ApiItem;
import com.myelth.ohi.service.OHIProviderService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class MyelthOhiServicesApplication {

	@Autowired
	private OHIProviderService ohiProviderService;

	public static void main(String[] args) {
		SpringApplication.run(MyelthOhiServicesApplication.class, args);
	}

	@PostConstruct
	public void loadDataOnStartup() throws JsonProcessingException {
		 ohiProviderService.getCachedProviderGroups();
	}

}

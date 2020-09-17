package com.paypay.baymax.core.config;

import javax.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import com.paypay.baymax.commons.util.GsonBuild;

@Configuration
public class ConfiguracionApp {
	
	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}

	@Bean("gsonCreate")
	public Gson gsonCreate() {
		return new GsonBuild().getGson();
	}

	@Bean("gsonCreateDate")
	public Gson gsonCreateDate() {
		return new GsonBuild().getGsonDate();
	}

	

	@Bean
	public RestTemplate getRestTemplate() {
		RestTemplateBuilder builder = new RestTemplateBuilder();
		return builder.build();
	}

	@PostConstruct
	public void init() {
		
	}

}

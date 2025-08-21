package com.example.sbre.config;

import com.example.sbre.SbreApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SbreApplication sbreApplication;

    WebConfig(SbreApplication sbreApplication) {
        this.sbreApplication = sbreApplication;
    }

	@Override  // corsorigin에 관환 메서드
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:5173");
	}

	
	
}

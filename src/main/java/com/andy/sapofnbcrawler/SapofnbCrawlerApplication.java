package com.andy.sapofnbcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
	externalDocs = @ExternalDocumentation(
			)
)
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAware")
public class SapofnbCrawlerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SapofnbCrawlerApplication.class, args);
    }
    
}

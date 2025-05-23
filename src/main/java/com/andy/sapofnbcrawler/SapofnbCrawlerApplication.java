package com.andy.sapofnbcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "Sapofnb Crawler", description = "Thông tin đặt cơm trưa TSB đến SAPO", version = "v1"), externalDocs = @ExternalDocumentation(description = "No document"))
@SpringBootApplication
//@EnableTransactionManagement
@EnableJpaAuditing(auditorAwareRef = "auditAware")
public class SapofnbCrawlerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SapofnbCrawlerApplication.class, args);
	}


//    @Bean(name = "transactionManager")
//    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
}

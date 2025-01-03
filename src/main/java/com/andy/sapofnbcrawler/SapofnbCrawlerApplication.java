package com.andy.sapofnbcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAware")
public class SapofnbCrawlerApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SapofnbCrawlerApplication.class, args);
    }
    
}

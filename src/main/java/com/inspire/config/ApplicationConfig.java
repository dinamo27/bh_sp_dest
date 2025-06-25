package com.inspire.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.inspire.repository")
@EntityScan(basePackages = "com.inspire.model")
public class ApplicationConfig {
    // Bean definitions would go here if needed
}
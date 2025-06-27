package com.example.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

@Configuration
@ComponentScan(basePackages = {"com.example.project.service", "com.example.service"})
public class PackageScanConfig {
}
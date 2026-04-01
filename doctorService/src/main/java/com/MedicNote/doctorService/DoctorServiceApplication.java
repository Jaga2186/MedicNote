package com.MedicNote.doctorService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Doctor Service Application - Main Entry Point
 * 
 * This is the main Spring Boot application class for the Doctor Service microservice.
 * 
 * Features enabled:
 * - Spring Boot Auto Configuration
 * - Eureka Service Discovery Client for microservice registration
 * 
 * Environment Configuration:
 * - Uses .env file for environment variables (loaded by EnvConfig)
 * - Application properties are in application.yaml
 * 
 * @author MedicNote
 * @version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class DoctorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoctorServiceApplication.class, args);
	}

}
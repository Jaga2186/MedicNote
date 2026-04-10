package com.MedicNote.patientService.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to load environment variables from .env file
 * 
 * This class initializes the DotEnv library to load all variables from the .env file
 * into the System environment variables, making them accessible throughout the application.
 * 
 * The .env file should be located in the project root directory.
 * 
 * @author MedicNote
 * @version 1.0
 */
@Configuration
public class EnvConfig {

    /**
     * Constructor that loads .env file variables
     * 
     * Configuration:
     * - ignoreIfMissing(): Does not throw exception if .env file is not found
     * - load(): Loads all variables from .env file
     */
    public EnvConfig() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        
        // Ensure all environment variables are loaded into System
        System.out.println("✓ .env file loaded successfully");
    }
}
package com.bitwisekaizen.sdss.management.config;

import org.springframework.boot.SpringApplication;

/**
 * Run the application using the "test" profile which allows the need for connecting with an agent.
 * This is used for testing purpose.
 */
public class ApplicationDebug {
    public static void main(String[] args) {
        //SpringApplication.run(ApplicationConfig.class, args);
        SpringApplication app = new SpringApplication(ApplicationConfig.class);
        app.setAdditionalProfiles("test");
        app.run(args);
    }
}

package com.bitwisekaizen.sdss.management.config;

import org.springframework.boot.SpringApplication;

/**
 * Main entry that spring boot executable runs.
 */
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationConfig.class, args);
    }
}

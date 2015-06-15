package com.bitwisekaizen.sdss.management.service;

/**
 * Exception for events that are unhandled and shouldn't be caught.
 */
public class UnhandledException extends RuntimeException {
    public UnhandledException(String message) {
        super(message);
    }
}

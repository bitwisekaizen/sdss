package com.bitwisekaizen.sdss.management.service;

/**
 * Information about a particular storage agent.
 */
public class StorageAgent {

    private final String serverUrl;

    public StorageAgent(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }
}

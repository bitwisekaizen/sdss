package com.bitwisekaizen.sdss.management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Factory to create the storage agent client.
 */
@Component
public class StorageAgentFactory {

    private final String serverUrl;

    @Autowired
    public StorageAgentFactory(
            @Value("${app.storage.agent.url}") String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * Get all healthy storage agent nodes.
     *
     * @return all healthy storage agent nodes.
     */
    public List<StorageAgent> getHealthyStorageAgents() {
        return Arrays.asList(new StorageAgent(serverUrl));
    }
}

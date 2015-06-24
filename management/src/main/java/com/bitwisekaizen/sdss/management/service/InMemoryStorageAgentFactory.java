package com.bitwisekaizen.sdss.management.service;

import java.util.Arrays;
import java.util.List;

/**
 * In-memory factory used for testing.
 */
public class InMemoryStorageAgentFactory extends StorageAgentFactory {

    public InMemoryStorageAgentFactory() {
        super(null);
    }

    @Override
    public void start() {

    }

    @Override
    public List<StorageAgent> getHealthyStorageAgents() {
        return Arrays.asList(new StorageAgent("test.example.com", 8080));
    }
}

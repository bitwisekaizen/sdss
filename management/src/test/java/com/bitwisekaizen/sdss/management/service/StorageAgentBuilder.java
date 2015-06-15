package com.bitwisekaizen.sdss.management.service;

import org.apache.commons.lang3.builder.Builder;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class StorageAgentBuilder implements Builder<StorageAgent> {
    private String serverUrl = "http://" + randomAlphabetic(4) + ".example.com";

    private StorageAgentBuilder() {
    }

    public static StorageAgentBuilder aStorageAgent() {
        return new StorageAgentBuilder();
    }

    public StorageAgentBuilder withServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        return this;
    }

    public StorageAgent build() {
        return new StorageAgent(serverUrl);
    }
}
package com.bitwisekaizen.sdss.management.service;

import org.apache.commons.lang3.builder.Builder;

import java.util.Random;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class StorageAgentBuilder implements Builder<StorageAgent> {
    private String agentNode = randomAlphabetic(4) + ".example.com";;
    private int agentNodePort = new Random().nextInt(3000);

    private StorageAgentBuilder() {
    }

    public static StorageAgentBuilder aStorageAgent() {
        return new StorageAgentBuilder();
    }

    public StorageAgentBuilder withAgentNode(String agentNode) {
        this.agentNode = agentNode;
        return this;
    }

    public StorageAgent build() {
        return new StorageAgent(agentNode, agentNodePort);
    }
}
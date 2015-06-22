package com.bitwisekaizen.sdss.management.entity;

import org.apache.commons.lang3.builder.Builder;

import java.util.UUID;

public class AgentNodeAffinityEntityBuilder implements Builder<AgentNodeAffinityEntity> {
    private String affinityKey = UUID.randomUUID().toString();
    private String storageAgentNode = UUID.randomUUID().toString();

    private AgentNodeAffinityEntityBuilder() {
    }

    public static AgentNodeAffinityEntityBuilder anAgentNodeAffinityEntity() {
        return new AgentNodeAffinityEntityBuilder();
    }

    public AgentNodeAffinityEntityBuilder withAffinityKey(String affinityKey) {
        this.affinityKey = affinityKey;
        return this;
    }

    public AgentNodeAffinityEntityBuilder withStorageAgentNode(String storageAgentNode) {
        this.storageAgentNode = storageAgentNode;
        return this;
    }

    public AgentNodeAffinityEntity build() {
        return new AgentNodeAffinityEntity(affinityKey, storageAgentNode);
    }
}
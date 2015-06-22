package com.bitwisekaizen.sdss.management.dto;

import org.apache.commons.lang3.builder.Builder;

import java.util.UUID;

public class AgentNodeAffinityBuilder implements Builder<AgentNodeAffinity> {
    private String affinityKey = UUID.randomUUID().toString();
    private String storageAgentNode = UUID.randomUUID().toString();

    private AgentNodeAffinityBuilder() {
    }

    public static AgentNodeAffinityBuilder anAgentNodeAffinity() {
        return new AgentNodeAffinityBuilder();
    }

    public AgentNodeAffinityBuilder withAffinityKey(String affinityKey) {
        this.affinityKey = affinityKey;
        return this;
    }

    public AgentNodeAffinityBuilder withStorageAgentNode(String storageAgentNode) {
        this.storageAgentNode = storageAgentNode;
        return this;
    }

    public AgentNodeAffinity build() {
        return new AgentNodeAffinity(affinityKey, storageAgentNode);
    }
}
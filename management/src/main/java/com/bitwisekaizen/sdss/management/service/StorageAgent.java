package com.bitwisekaizen.sdss.management.service;

/**
 * Information about a particular storage agent.
 */
public class StorageAgent {

    private final String agentNode;
    private int agentNodePort;

    public StorageAgent(String agentNode, int agentNodePort) {
        this.agentNode = agentNode;
        this.agentNodePort = agentNodePort;
    }

    public String getAgentNode() {
        return agentNode;
    }

    public String getServerUrl() {
        return "http://" + agentNode + ":" + agentNodePort;
    }

    public int getAgentNodePort() {
        return agentNodePort;
    }
}

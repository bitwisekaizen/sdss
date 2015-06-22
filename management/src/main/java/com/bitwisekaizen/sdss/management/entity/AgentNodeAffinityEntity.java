package com.bitwisekaizen.sdss.management.entity;

import javax.persistence.*;

/**
 * Entity that represents an agent node affinity that stores mapping of a key to the preferred agent node.
 */
//@Entity
//@Table(name = "agent_node_affinity")
public class AgentNodeAffinityEntity {

    @Id
    @Column(name = "affinity_key")
    private String affinityKey;

    @Column(name = "agent_node")
    private String agentNode;

    protected AgentNodeAffinityEntity() {
    }

    public AgentNodeAffinityEntity(String affinityKey, String agentNode) {
        this();
        this.affinityKey = affinityKey;
        this.agentNode = agentNode;
    }

    /**
     * Get the affinity key.
     *
     * @return affinity key.
     */
    public String getAffinityKey() {
        return affinityKey;
    }

    /**
     * IP location on which this target server exists.
     *
     * @return IP location on which this target server exists.
     */
    public String getAgentNode() {
        return agentNode;
    }

    @Override
    public String toString() {
        return String.format("AgentNodeAffinityEntity[key='%s', node='%s']", affinityKey, agentNode);
    }

    public void setAgentNode(String agentNode) {
        this.agentNode = agentNode;
    }
}
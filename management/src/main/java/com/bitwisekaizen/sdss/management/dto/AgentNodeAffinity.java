package com.bitwisekaizen.sdss.management.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Affinity that determines which storage agent node are preferred for a key.
 */
@ApiModel(description = "Affinity that determines which storage agent node are preferred for a key.")
public class AgentNodeAffinity {

    private String affinityKey;

    private String agentNode;

    // Json serialization
    private AgentNodeAffinity() {
    }

    public AgentNodeAffinity(String affinityKey, String agentNode) {
        this.affinityKey = affinityKey;
        this.agentNode = agentNode;
    }

    /**
     * Get the affinity key that is used to map to a specific agent node.
     *
     * @return affinity key.
     */
    @ApiModelProperty(value = "Affinity key that is used to map to a specific agent node", required = true)
    public String getAffinityKey() {
        return affinityKey;
    }

    /**
     * Get the storage node agent that the affinity key should be mapped to.
     *
     * @return the storage agent node.
     */
    @ApiModelProperty(value = "The storage node agent that the affinity key should be mapped to.", required = true)
    public String getAgentNode() {
        return agentNode;
    }

    /**
     * Update the specified storage agent.
     *
     * @param agentNode node to update.
     */
    public void setAgentNode(String agentNode) {
        this.agentNode = agentNode;
    }
}

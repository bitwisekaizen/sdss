package com.bitwisekaizen.sdss.management.service;

import javax.ws.rs.NotFoundException;

/**
 * Exception thrown when agent node is not found for an affinity key.
 */
public class AgentNodeNotFoundException extends NotFoundException {
    public AgentNodeNotFoundException(String agentNode) {
        super(String.format("Agent node %s not found.  Not healhty?", agentNode));
    }
}

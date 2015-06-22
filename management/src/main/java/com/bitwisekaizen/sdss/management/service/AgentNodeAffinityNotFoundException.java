package com.bitwisekaizen.sdss.management.service;

import javax.ws.rs.NotFoundException;

/**
 * Exception thrown when agent node affinity is not found.
 */
public class AgentNodeAffinityNotFoundException extends NotFoundException {

    public AgentNodeAffinityNotFoundException(String key) {
        super(String.format("Affinity key %s not found", key));
    }
}

package com.bitwisekaizen.sdss.management.acceptance.operations;

import com.bitwisekaizen.sdss.management.dto.AgentNodeAffinity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.util.List;

/**
 * Management operations against agent node affinity.
 */
public class AgentNodeAffinityOperations {

    private WebTarget webTarget;

    public AgentNodeAffinityOperations(WebTarget webTarget) {
        this.webTarget = webTarget;
    }

    /**
     * Create an agent node affinity.  If affinity with specified key already exists, then update it.
     *
     * @param affinity affinity to create or update.
     * @return AgentNode created/updated.
     */
    public AgentNodeAffinity createOrUpdateAgentNodeAffinity(AgentNodeAffinity affinity) {
        return webTarget.path("api").path("affinities").request().post(
                Entity.json(affinity), AgentNodeAffinity.class);
    }

    /**
     * Get the agent node affinity.
     *
     * @param key affinity key of the affinity to get
     * @return Agent node found.
     */
    public AgentNodeAffinity getAgentNodeAffinity(String key) {
        return webTarget.path("api").path("affinities").path(key).request().get(AgentNodeAffinity.class);
    }

    /**
     * Delete the specified agent node affinity.
     *
     * @param agentNodeAffinity affinity to delete
     */
    public void deleteAgentNodeAffinity(AgentNodeAffinity agentNodeAffinity) {
        webTarget.path("api").path("affinities").path(agentNodeAffinity.getAffinityKey()).request().delete(byte[].class);
    }

    /**
     * Get all agent node affinities in the system.
     *
     * @return agent node affinities.
     */
    public List<AgentNodeAffinity> getAllAgentNodeAffinities() {
        return webTarget.path("api").path("affinities").request().get(new GenericType<List<AgentNodeAffinity>>() {});
    }

    /**
     * Delete all agent node affinities.
     */
    public void deleteAllAgentNodeAffinities() {
        for (AgentNodeAffinity affinity : getAllAgentNodeAffinities()) {
            deleteAgentNodeAffinity(affinity);
        }
    }
}

package com.bitwisekaizen.sdss.agentclient;


import javax.ws.rs.client.WebTarget;
import java.util.List;

/**
 * Client that can interact with the storage agent.
 */
public class StorageAgentClient {

    private WebTarget webTarget;

    public StorageAgentClient(WebTarget webTarget) {
        this.webTarget = webTarget;
    }

    /**
     * Create an ISCSI target with the specified spec.
     *
     * @param iscsiTarget target to create.
     */
    public AccessibleIscsiTarget createIscsiTarget(IscsiTarget iscsiTarget) {

        return null;
    }

    /**
     * Delete the specified ISCSI target.
     *
     * @param targetName name of the target to delete
     */
    public void deleteIscsiTarget(String targetName) {

    }

    /**
     * Get all the ISCSI targets on the agent.
     *
     * @return All the ISCSI targets on the agent.
     */
    public List<AccessibleIscsiTarget> getAllIscsiTargets() {
        return null;
    }
}

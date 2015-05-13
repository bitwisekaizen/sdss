package com.bitwisekaizen.sdss.agentclient;


import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
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
        return webTarget.path("api").path("accessibleiscsitargets").request().post(Entity.json(iscsiTarget),
                AccessibleIscsiTarget.class);
    }

    /**
     * Delete the specified ISCSI target.
     *
     * @param targetName name of the target to delete
     */
    public void deleteIscsiTarget(String targetName) {
        webTarget.path("api").path("accessibleiscsitargets").path(targetName).request().delete(byte[].class);
    }

    /**
     * Get all the ISCSI targets on the agent.
     *
     * @return All the ISCSI targets on the agent.
     */
    public List<AccessibleIscsiTarget> getAllIscsiTargets() {
        return webTarget.path("api").path("accessibleiscsitargets").request().get(
                new GenericType<List<AccessibleIscsiTarget>>() {});
    }
}

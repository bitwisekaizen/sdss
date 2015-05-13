package com.bitwisekaizen.sdss.agent.web.resource;

import com.bitwisekaizen.sdss.agent.service.AccessibleIscsiTargetService;
import com.bitwisekaizen.sdss.agent.service.DuplicateTargetNameException;
import com.bitwisekaizen.sdss.agent.service.IscsiTargetNotFoundException;
import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


/**
 * Resource to manage ISCSI target that can be accessed via their storage network addresses.
 */
@Component
@Path("accessibleiscsitargets")
public class AccessibleIscsiTargetResource {
    final static Logger logger = LoggerFactory.getLogger(AccessibleIscsiTargetResource.class);

    @Autowired
    private AccessibleIscsiTargetService iscsiTargetService;

    /**
     * Create the ISCSI target with the given spec.
     *
     * @param iscsiTarget ISCSI target to create
     * @return the created ISCSI target.
     * @throws DuplicateTargetNameException if the target name already exists.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AccessibleIscsiTarget createAccessibleIscsiTarget(IscsiTarget iscsiTarget) {
        return iscsiTargetService.createAccessbileIscsiTarget(iscsiTarget);
    }

    /**
     * Delete the ISCSI target with the specified ID.
     *
     * @param targetName UUID of the ISCSI target to delete
     * @throws IscsiTargetNotFoundException if target is not found
     */
    @DELETE
    @Path("{targetName}")
    public void deleteUniqueIscsiTarget(@PathParam("targetName") String targetName) {
        iscsiTargetService.deleteAccessibleIscsiTarget(targetName);
    }

    /**
     * Get all the ISCSI targets in the system.
     *
     * @return all the ISCSI targets in the system.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<AccessibleIscsiTarget> getAllUniqueIscsiTargets() {
        return iscsiTargetService.getAllAccessibleIscsiTargets();
    }

}

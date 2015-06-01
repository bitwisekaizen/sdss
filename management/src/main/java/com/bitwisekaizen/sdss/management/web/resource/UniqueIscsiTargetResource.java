package com.bitwisekaizen.sdss.management.web.resource;

import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.management.service.IscsiTargetNotFoundException;
import com.bitwisekaizen.sdss.management.dto.UniqueIscsiTarget;
import com.bitwisekaizen.sdss.management.service.IscsiTargetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;


/**
 * Resource to manage ISCSI target that is uniquely identifiable to the storage management system.
 */
@Component
@Path("uniqueiscsitargets")
public class UniqueIscsiTargetResource {
    final static Logger logger = LoggerFactory.getLogger(UniqueIscsiTargetResource.class);

    @Autowired
    private IscsiTargetService iscsiTargetService;

    /**
     * Create the ISCSI target with the given spec.
     *
     * @param iscsiTarget ISCSI target to create
     * @return the created ISCSI target.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UniqueIscsiTarget createUniqueIscsiTarget(IscsiTarget iscsiTarget) {
        return iscsiTargetService.createUniqueIscsiTarget(iscsiTarget);
    }

    /**
     * Get the ISCSI target with the specified ID.
     *
     * @param uuid UUID of the ISCSI target to get
     * @throws IscsiTargetNotFoundException if target is not found
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{uuid}")
    public UniqueIscsiTarget getUniqueIscsiTarget(@PathParam("uuid") String uuid) {
        return iscsiTargetService.getUniqueIscsiTarget(uuid);
    }

    /**
     * Delete the ISCSI target with the specified ID.
     *
     * @param uuid UUID of the ISCSI target to delete
     * @throws IscsiTargetNotFoundException if target is not found
     */
    @DELETE
    @Path("{uuid}")
    public void deleteUniqueIscsiTarget(@PathParam("uuid") String uuid) {
        iscsiTargetService.deleteIscsiUniqueTarget(uuid);
    }

    /**
     * Get all the ISCSI targets in the system.
     *
     * @return all the ISCSI targets in the system.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<UniqueIscsiTarget> getAllUniqueIscsiTargets() {
        return iscsiTargetService.getAllUniqueIscsiTargets();
    }

}

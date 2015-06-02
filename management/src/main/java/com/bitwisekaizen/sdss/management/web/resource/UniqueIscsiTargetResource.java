package com.bitwisekaizen.sdss.management.web.resource;

import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.management.service.DuplicateTargetNameException;
import com.bitwisekaizen.sdss.management.service.IscsiTargetNotFoundException;
import com.bitwisekaizen.sdss.management.dto.UniqueIscsiTarget;
import com.bitwisekaizen.sdss.management.service.IscsiTargetService;
import com.wordnik.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.apache.http.HttpStatus.*;


/**
 * Resource to manage ISCSI target that is uniquely identifiable to the storage management system.
 */
@Component
@Path("uniqueiscsitargets")
@Api(value="/uniqueiscsitargets: Operations on ISCSI targets")
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
    @ApiOperation(value = "Creates a unique network-accessible ISCSI target given ISCSI spec", position = 0)
    @ApiResponses(value = {
            @ApiResponse(code = SC_BAD_REQUEST, message = "Invalid target name supplied"),
            @ApiResponse(code = SC_CONFLICT, message = "Target name already exists")})
    public UniqueIscsiTarget createUniqueIscsiTarget(
            @ApiParam(value = "ISCSI target to create a unique ISCSI target for", required = true) IscsiTarget iscsiTarget)
            throws DuplicateTargetNameException, ConstraintViolationException {
        return iscsiTargetService.createUniqueIscsiTarget(iscsiTarget);
    }

    /**
     * Get the ISCSI target with the specified UUID.
     *
     * @param uuid UUID of the ISCSI target to get
     * @throws IscsiTargetNotFoundException if target is not found
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{uuid}")
    @ApiOperation(value = "Get the unique network-accessible ISCSI based on its UUID", position = 1)
    @ApiResponses(value = {
            @ApiResponse(code = SC_NOT_FOUND, message = "Target not found")})
    public UniqueIscsiTarget getUniqueIscsiTarget(
            @ApiParam(value = "UUID of the ISCSI target", required = true) @PathParam("uuid") String uuid)
            throws IscsiTargetNotFoundException {
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
    @ApiOperation(value = "Delete the unique network-accessible ISCSI target based on its UUID", position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = SC_NOT_FOUND, message = "Target not found")})
    public void deleteUniqueIscsiTarget(
            @ApiParam(value = "UUID of the ISCSI target", required = true) @PathParam("uuid") String uuid)
            throws IscsiTargetNotFoundException {
        iscsiTargetService.deleteIscsiUniqueTarget(uuid);
    }

    /**
     * Get all the ISCSI targets in the system.
     *
     * @return all the ISCSI targets in the system.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get all unique network-accessible ISCSI targets", position = 10,
            response = UniqueIscsiTarget.class, responseContainer="List")
    public List<UniqueIscsiTarget> getAllUniqueIscsiTargets() {
        return iscsiTargetService.getAllUniqueIscsiTargets();
    }

}

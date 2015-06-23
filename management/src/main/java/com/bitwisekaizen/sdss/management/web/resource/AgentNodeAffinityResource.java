package com.bitwisekaizen.sdss.management.web.resource;

import com.bitwisekaizen.sdss.management.dto.AgentNodeAffinity;
import com.bitwisekaizen.sdss.management.service.AgentNodeAffinityNotFoundException;
import com.bitwisekaizen.sdss.management.service.AgentNodeAffinityService;
import com.wordnik.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;


/**
 * Resource to manage agent node affinity.
 */
@Component
@Path("affinities")
@Api(value = "/affinities: Operations to manage agent node affinity")
public class AgentNodeAffinityResource {
    final static Logger logger = LoggerFactory.getLogger(AgentNodeAffinityResource.class);

    @Autowired
    private AgentNodeAffinityService agentNodeAffinityService;

    /**
     * Create the storage node affinity with the given spec.  If it exists, then update it.
     *
     * @param affinity affinity to create
     * @return the created affinity.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create or update the storage node affinity with the given spec", position = 0)
    @ApiResponses(value = {
            @ApiResponse(code = SC_BAD_REQUEST, message = "Invalid values")})
    public AgentNodeAffinity createOrUpdateAgentAffinity(
            @ApiParam(value = "Agent node affinity to create/update", required = true) AgentNodeAffinity affinity)
            throws ConstraintViolationException {
        return agentNodeAffinityService.createOrUpdateAgentAffinity(affinity);
    }

    /**
     * Get the agent node affinity with the specified key.
     *
     * @param affinityKey affinity key that will be retrieved
     * @throws AgentNodeAffinityNotFoundException if affinity is not found
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{affinityKey}")
    @ApiOperation(value = "Get the agent node affinity with the specified key.", position = 1)
    @ApiResponses(value = {
            @ApiResponse(code = SC_NOT_FOUND, message = "Affinity key not found")})
    public AgentNodeAffinity getAgentAffinity(
            @ApiParam(value = "Key of the affinity", required = true) @PathParam("affinityKey") String affinityKey)
            throws AgentNodeAffinityNotFoundException {
        return agentNodeAffinityService.getAgentAffinity(affinityKey);
    }

    /**
     * Delete the agent node affinity with the specified key.
     *
     * @param affinityKey affinity key to delete
     * @throws AgentNodeAffinityNotFoundException if affinity is not found
     */
    @DELETE
    @Path("{affinityKey}")
    @ApiOperation(value = "Delete the agent node affinity with the specified key.", position = 2)
    @ApiResponses(value = {
            @ApiResponse(code = SC_NOT_FOUND, message = "Affinity key not found")})
    public void deleteAgentAffinity(
            @ApiParam(value = "Key of the affinity", required = true) @PathParam("affinityKey") String affinityKey)
            throws AgentNodeAffinityNotFoundException {
        agentNodeAffinityService.deleteAgentAffinity(affinityKey);
    }

    /**
     * Get all the agent node affinities
     *
     * @return all the agent node affinities
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get all the agent node affinities", position = 10,
            response = AgentNodeAffinity.class, responseContainer = "List")
    public List<AgentNodeAffinity> getAllAgentAffinities() {
        return agentNodeAffinityService.getAllAgentAffinities();
    }
}

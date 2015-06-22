package com.bitwisekaizen.sdss.management.service;

import com.bitwisekaizen.sdss.management.dto.AgentNodeAffinity;
import com.bitwisekaizen.sdss.management.repository.AgentNodeAffinityRepository;
import com.bitwisekaizen.sdss.management.validation.DtoValidator;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.PathParam;
import java.util.List;


/**
 * Service to manage agent node affinity.
 */
@Service
public class AgentNodeAffinityService {
    final static Logger logger = LoggerFactory.getLogger(AgentNodeAffinityService.class);

    @Autowired
    public AgentNodeAffinityService(AgentNodeAffinityRepository agentNodeAffinityRepository,
                                    DtoValidator dtoValidator) {

    }

    /**
     * Create the storage node affinity with the given spec.  If it exists, then update it.
     *
     * @param affinity affinity to create
     * @return the created affinity.
     */
    public AgentNodeAffinity createOrUpdateAgentAffinity(AgentNodeAffinity affinity)
            throws ConstraintViolationException {
        return null;
    }

    /**
     * Get the agent node affinity with the specified key.
     *
     * @param affinityKey affinity key that will be retrieved
     * @throws AgentNodeAffinityNotFoundException if affinity is not found
     */
    public AgentNodeAffinity getAgentAffinity(String affinityKey)
            throws AgentNodeAffinityNotFoundException {
        return null;
    }

    /**
     * Delete the agent node affinity with the specified key.
     *
     * @param affinityKey affinity key to delete
     * @throws AgentNodeAffinityNotFoundException if affinity is not found
     */
    public void deleteAgentAffinity(String affinityKey)
            throws AgentNodeAffinityNotFoundException {
    }

    /**
     * Get all the agent node affinities
     *
     * @return all the agent node affinities
     */
    public List<AgentNodeAffinity> getAllAgentAffinities() {
        return null;
    }

}

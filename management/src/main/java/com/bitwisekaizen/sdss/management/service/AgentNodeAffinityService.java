package com.bitwisekaizen.sdss.management.service;

import com.bitwisekaizen.sdss.management.dto.AgentNodeAffinity;
import com.bitwisekaizen.sdss.management.entity.AgentNodeAffinityEntity;
import com.bitwisekaizen.sdss.management.repository.AgentNodeAffinityRepository;
import com.bitwisekaizen.sdss.management.validation.DtoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;


/**
 * Service to manage agent node affinity.
 */
//@Service
@Transactional
public class AgentNodeAffinityService {
    final static Logger logger = LoggerFactory.getLogger(AgentNodeAffinityService.class);
    private AgentNodeAffinityRepository agentNodeAffinityRepository;
    private DtoValidator dtoValidator;

    //@Autowired
    public AgentNodeAffinityService(AgentNodeAffinityRepository agentNodeAffinityRepository,
                                    DtoValidator dtoValidator) {
        this.agentNodeAffinityRepository = agentNodeAffinityRepository;
        this.dtoValidator = dtoValidator;
    }

    /**
     * Create the storage node affinity with the given spec.  If it exists, then update it.
     *
     * @param affinity affinity to create
     * @return the created affinity.
     */
    public AgentNodeAffinity createOrUpdateAgentAffinity(AgentNodeAffinity affinity)
            throws ConstraintViolationException {
        dtoValidator.validate(affinity);

        AgentNodeAffinityEntity affinityToSave = agentNodeAffinityRepository.findOne(affinity.getAffinityKey());
        if (affinityToSave == null) {
            affinityToSave = new AgentNodeAffinityEntity(affinity.getAffinityKey(), affinity.getAgentNode());
        } else {
            affinityToSave.setAgentNode(affinity.getAgentNode());
        }

        agentNodeAffinityRepository.save(affinityToSave);

        return convert(affinityToSave);
    }

    private AgentNodeAffinity convert(AgentNodeAffinityEntity affinityEntity) {
        return new AgentNodeAffinity(affinityEntity.getAffinityKey(), affinityEntity.getAgentNode());
    }

    /**
     * Get the agent node affinity with the specified key.
     *
     * @param affinityKey affinity key that will be retrieved
     * @throws AgentNodeAffinityNotFoundException if affinity is not found
     */
    public AgentNodeAffinity getAgentAffinity(String affinityKey)
            throws AgentNodeAffinityNotFoundException {
        AgentNodeAffinityEntity affinity = agentNodeAffinityRepository.findOne(affinityKey);

        if (affinity == null) {
            throw new AgentNodeAffinityNotFoundException(affinityKey);
        }

        return convert(affinity);
    }

    /**
     * Delete the agent node affinity with the specified key.
     *
     * @param affinityKey affinity key to delete
     * @throws AgentNodeAffinityNotFoundException if affinity is not found
     */
    public void deleteAgentAffinity(String affinityKey)
            throws AgentNodeAffinityNotFoundException {
        AgentNodeAffinityEntity affinity = agentNodeAffinityRepository.findOne(affinityKey);

        if (affinity == null) {
            throw new AgentNodeAffinityNotFoundException(affinityKey);
        }

        agentNodeAffinityRepository.delete(affinity);
    }

    /**
     * Get all the agent node affinities
     *
     * @return all the agent node affinities
     */
    public List<AgentNodeAffinity> getAllAgentAffinities() {
        Iterable<AgentNodeAffinityEntity> affinities = agentNodeAffinityRepository.findAll();
        List<AgentNodeAffinity> convertedAffinities = new ArrayList<>();
        for (AgentNodeAffinityEntity affinityEntity : affinities) {
            convertedAffinities.add(convert(affinityEntity));
        }

        return convertedAffinities;
    }

}

package com.bitwisekaizen.sdss.management.service;

import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.management.entity.AgentNodeAffinityEntity;
import com.bitwisekaizen.sdss.management.repository.AgentNodeAffinityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * Calculates the best storage agent to use based on the ISCSI target to create.
 */
@Component
public class AffinityBasedStorageAgentRetriever {

    private final Random random = new Random();
    private boolean runningIntegrationTest;
    private StorageAgentFactory storageAgentFactory;
    private AgentNodeAffinityRepository agentNodeAffinityRepository;

    @Autowired
    public AffinityBasedStorageAgentRetriever(
            @Value("${app.running.integration.test}") boolean runningIntegrationTest,
            StorageAgentFactory storageAgentFactory, AgentNodeAffinityRepository agentNodeAffinityRepository) {
        this.runningIntegrationTest = runningIntegrationTest;
        this.storageAgentFactory = storageAgentFactory;
        this.agentNodeAffinityRepository = agentNodeAffinityRepository;
    }

    /**
     * Get the best storage agent to use based on the desired ISCSI target to create.
     *
     * @param iscsiTarget ISCSI target to create
     * @return best storage agent to use based on the desired ISCSI target to create.
     */
    public StorageAgent getStorageAgent(IscsiTarget iscsiTarget) {
        AgentNodeAffinityEntity affinityEntity = agentNodeAffinityRepository.findOne(iscsiTarget.getAffinityKey());
        if (affinityEntity == null) {
            throw new AgentNodeAffinityNotFoundException(iscsiTarget.getAffinityKey());
        }

        if (runningIntegrationTest) {
            return new StorageAgent(affinityEntity.getAgentNode(), 8080);
        }

        List<StorageAgent> healthyStorageAgents = storageAgentFactory.getHealthyStorageAgents();
        if (healthyStorageAgents.isEmpty()) {
            throw new UnhandledException("No available storage nodes");
        }

        for (StorageAgent storageAgent : healthyStorageAgents) {
            if (storageAgent.getAgentNode().equals(affinityEntity.getAgentNode())) {
                return storageAgent;
            }
        }

        throw new AgentNodeNotFoundException(iscsiTarget.getAffinityKey());
    }
}

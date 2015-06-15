package com.bitwisekaizen.sdss.management.service;

import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Calculates the best storage agent to use based on the ISCSI target to create.
 */
@Component
public class BestStorageAgentCalculator {

    private StorageAgentFactory storageAgentFactory;

    @Autowired
    public BestStorageAgentCalculator(StorageAgentFactory storageAgentFactory) {
        this.storageAgentFactory = storageAgentFactory;
    }

    /**
     * Get the best storage agent to use based on the desired ISCSI target to create.
     *
     * @param iscsiTarget ISCSI target to create
     * @return best storage agent to use based on the desired ISCSI target to create.
     */
    public StorageAgent getBestStorageAgent(IscsiTarget iscsiTarget) {
        List<StorageAgent> healthyStorageAgents = storageAgentFactory.getHealthyStorageAgents();

        // @todo: do smarter way to choose storage agent based on the iscsi target to create

        return healthyStorageAgents.get(0);
    }
}

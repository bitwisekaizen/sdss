package com.bitwisekaizen.sdss.agent.service;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import org.springframework.stereotype.Service;

/**
 * Service that manages ISCSI targets using LIO.
 */
@Service
public class LioBackedIscsiTargetService {
    /**
     * Create the specified ISCSI target.
     *
     * @param iscsiTarget target to create
     * @return the created target.
     */
    public AccessibleIscsiTarget createIscsiTarget(IscsiTarget iscsiTarget) {
        return null;
    }

    /**
     * Delete the target with the specified name.
     *
     * @param targetName target name to delete
     */
    public void delete(String targetName) {

    }
}

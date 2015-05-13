package com.bitwisekaizen.sdss.agent.service;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Repository that stores the network-accessible ISCSI targets.
 */
@Service
public class AccessibleIscsiTargetRepository {

    /**
     * Target to save.
     *
     * @param accessibleIscsiTarget target to save.
     */
    public void save(AccessibleIscsiTarget accessibleIscsiTarget) {
    }

    /**
     * Find the target with the specified name.
     *
     * @param targetName target to find
     * @return target if found; else, null.
     */
    public AccessibleIscsiTarget findByTargetName(String targetName) {
        return null;
    }

    /**
     * Delete the target with the specified name.  Noops occur if target doesn't exist.
     *
     * @param targetName target to delete
     */
    public void delete(String targetName) {

    }

    /**
     * Get all the ISCSI targets.
     *
     * @return all ISCSI targets.
     */
    public List<AccessibleIscsiTarget> findAll() {
        return null;
    }
}

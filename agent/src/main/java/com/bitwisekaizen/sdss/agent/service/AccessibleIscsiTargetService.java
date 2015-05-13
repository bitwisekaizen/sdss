package com.bitwisekaizen.sdss.agent.service;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to manage ISCSI targets.
 */
@Service
public class AccessibleIscsiTargetService {

    /**
     * Create the ISCSI target with the given spec.
     *
     * @param iscsiTarget ISCSI target to create
     * @return the created ISCSI target.
     * @throws DuplicateTargetNameException if the target name already exists.
     */
    public AccessibleIscsiTarget createAccessbileIscsiTarget(IscsiTarget iscsiTarget) {
        return null;
    }

    /**
     * Delete the ISCSI target with the specified ID.
     *
     * @param targetName UUID of the ISCSI target to delete
     * @throws IscsiTargetNotFoundException if target is not found
     */
    public void deleteAccessibleIscsiTarget(String targetName) {

    }

    /**
     * Get all the ISCSI targets in the system.
     *
     * @return all the ISCSI targets in the system.
     */
    public List<AccessibleIscsiTarget> getAllAccessibleIscsiTargets() {
        return null;
    }
}

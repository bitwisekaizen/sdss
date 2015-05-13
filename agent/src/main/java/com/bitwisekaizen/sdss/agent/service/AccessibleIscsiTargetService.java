package com.bitwisekaizen.sdss.agent.service;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to manage ISCSI targets.
 */
@Service
public class AccessibleIscsiTargetService {

    private AccessibleIscsiTargetRepository accessibleIscsiTargetRepository;
    private LioBackedIscsiTargetService lioBackedStorageService;

    @Autowired
    public AccessibleIscsiTargetService(AccessibleIscsiTargetRepository accessibleIscsiTargetRepository,
                                        LioBackedIscsiTargetService lioBackedStorageService) {

        this.accessibleIscsiTargetRepository = accessibleIscsiTargetRepository;
        this.lioBackedStorageService = lioBackedStorageService;
    }

    /**
     * Create the ISCSI target with the given spec.
     *
     * @param iscsiTarget ISCSI target to create
     * @return the created ISCSI target.
     * @throws DuplicateTargetNameException if the target name already exists.
     */
    public AccessibleIscsiTarget createAccessbileIscsiTarget(IscsiTarget iscsiTarget) {
        AccessibleIscsiTarget accessibleIscsiTarget = accessibleIscsiTargetRepository.findByTargetName(
                iscsiTarget.getTargetName());
        if (accessibleIscsiTarget != null) {
            throw new DuplicateTargetNameException(iscsiTarget.getTargetName());
        }

        accessibleIscsiTarget = lioBackedStorageService.createIscsiTarget(iscsiTarget);
        accessibleIscsiTargetRepository.save(accessibleIscsiTarget);

        return accessibleIscsiTarget;
    }

    /**
     * Delete the ISCSI target with the specified ID.
     *
     * @param targetName UUID of the ISCSI target to delete
     * @throws IscsiTargetNotFoundException if target is not found
     */
    public void deleteAccessibleIscsiTarget(String targetName) {
        AccessibleIscsiTarget accessibleIscsiTarget = accessibleIscsiTargetRepository.findByTargetName(targetName);
        if (accessibleIscsiTarget == null) {
            throw new IscsiTargetNotFoundException(targetName);
        }

        lioBackedStorageService.delete(targetName);
        accessibleIscsiTargetRepository.delete(targetName);
    }

    /**
     * Get all the ISCSI targets in the system.
     *
     * @return all the ISCSI targets in the system.
     */
    public List<AccessibleIscsiTarget> getAllAccessibleIscsiTargets() {
        return accessibleIscsiTargetRepository.findAll();
    }
}

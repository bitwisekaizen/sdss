package com.bitwisekaizen.sdss.management.service;

import com.bitwisekaizen.sdss.management.agent.StorageAgentClient;
import com.bitwisekaizen.sdss.management.dto.IscsiTarget;
import com.bitwisekaizen.sdss.management.dto.UniqueIscsiTarget;
import com.bitwisekaizen.sdss.management.entity.InitiatorIqnEntity;
import com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntity;
import com.bitwisekaizen.sdss.management.repository.UniqueIscsiTargetRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to manage ISCSI targets such as creation and deletion.
 */
public class IscsiTargetService {

    private UniqueIscsiTargetRepository uniqueIscsiTargetRepository;
    private StorageAgentClient storageAgentClient;

    public IscsiTargetService(UniqueIscsiTargetRepository uniqueIscsiTargetRepository,
                              StorageAgentClient storageAgentClient) {
        this.uniqueIscsiTargetRepository = uniqueIscsiTargetRepository;
        this.storageAgentClient = storageAgentClient;
    }

    /**
     * Create the ISCSI target with the given spec.
     *
     * @param iscsiTarget ISCSI target to create
     * @return the created ISCSI target.
     */
    public UniqueIscsiTarget createUniqueIscsiTarget(IscsiTarget iscsiTarget) {
        UniqueIscsiTargetEntity uniqueIscsiTargetEntity =
                uniqueIscsiTargetRepository.save(convertToIscsiTargetEntity(iscsiTarget, storageAgentClient));
        storageAgentClient.createIscsiTarget(iscsiTarget);

        return convertToUniqueIscsiTarget(uniqueIscsiTargetEntity);
    }

    /**
     * Get the ISCSI target with the specified ID.
     *
     * @param uuid UUID of the ISCSI target to get
     * @throws IscsiTargetNotFoundException if target is not found
     */
    public UniqueIscsiTarget getUniqueIscsiTarget(String uuid) throws IscsiTargetNotFoundException {
        UniqueIscsiTargetEntity uniqueIscsiTargetEntity = uniqueIscsiTargetRepository.findByUuid(uuid);
        if (uniqueIscsiTargetEntity == null) {
            throw new IscsiTargetNotFoundException(uuid);
        }

        return convertToUniqueIscsiTarget(uniqueIscsiTargetEntity);
    }

    /**
     * Get all the ISCSI targets in the system.
     *
     * @return all the ISCSI targets in the system.
     */
    public List<UniqueIscsiTarget> getAllUniqueIscsiTargets() {
        List<UniqueIscsiTarget> iscsiTargets = new ArrayList<>();
        for (UniqueIscsiTargetEntity uniqueIscsiTargetEntity : uniqueIscsiTargetRepository.findAll()) {
            iscsiTargets.add(convertToUniqueIscsiTarget(uniqueIscsiTargetEntity));
        }

        return iscsiTargets;
    }

    /**
     * Delete the ISCSI target with the specified ID.
     *
     * @param uuid UUID of the ISCSI target to delete
     * @throws IscsiTargetNotFoundException if target is not found
     */
    public void deleteIscsiUniqueTarget(String uuid) throws IscsiTargetNotFoundException {
        UniqueIscsiTargetEntity uniqueIscsiTargetEntity = uniqueIscsiTargetRepository.findByUuid(uuid);
        if (uniqueIscsiTargetEntity == null) {
            throw new IscsiTargetNotFoundException(uuid);
        }

        uniqueIscsiTargetRepository.delete(uniqueIscsiTargetEntity.getUuid());
        storageAgentClient.deleteIscsiTarget(uniqueIscsiTargetEntity.getUuid());
    }

    private UniqueIscsiTargetEntity convertToIscsiTargetEntity(IscsiTarget iscsiTarget,
                                                         StorageAgentClient storageAgentClient) {
        List<InitiatorIqnEntity> initiatorIqnEntities = new ArrayList<>();

        for (String iqn : iscsiTarget.getHostIscsiQualifiedNames()) {
            initiatorIqnEntities.add(new InitiatorIqnEntity(iqn));
        }

        return new UniqueIscsiTargetEntity(initiatorIqnEntities, iscsiTarget.getCapacityInMb(),
                iscsiTarget.getTargetName(), storageAgentClient.getStorageIpAddress());
    }

    private UniqueIscsiTarget convertToUniqueIscsiTarget(UniqueIscsiTargetEntity uniqueIscsiTargetEntity) {
        IscsiTarget iscsiTarget = convertToIscsiTarget(uniqueIscsiTargetEntity);
        return new UniqueIscsiTarget(uniqueIscsiTargetEntity.getUuid(), uniqueIscsiTargetEntity.getStorageHost(), iscsiTarget);
    }

    private IscsiTarget convertToIscsiTarget(UniqueIscsiTargetEntity uniqueIscsiTargetEntity) {
        List<String> hostIscsiQualifiedNames = new ArrayList<>();
        for (InitiatorIqnEntity initiatorIqnEntity : uniqueIscsiTargetEntity.getInitiatorIqnEntities()) {
            hostIscsiQualifiedNames.add(initiatorIqnEntity.getIqn());
        }

        return new IscsiTarget(hostIscsiQualifiedNames, uniqueIscsiTargetEntity.getCapacityInMb(),
                uniqueIscsiTargetEntity.getTargetName());
    }
}

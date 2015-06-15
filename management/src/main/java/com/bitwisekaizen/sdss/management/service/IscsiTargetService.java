package com.bitwisekaizen.sdss.management.service;

import com.bitwisekaizen.sdss.agentclient.AccessibleIscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.agentclient.StorageAgentClient;
import com.bitwisekaizen.sdss.management.dto.UniqueIscsiTarget;
import com.bitwisekaizen.sdss.management.entity.InitiatorIqnEntity;
import com.bitwisekaizen.sdss.management.entity.UniqueIscsiTargetEntity;
import com.bitwisekaizen.sdss.management.repository.UniqueIscsiTargetRepository;
import com.bitwisekaizen.sdss.management.validation.DtoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to manage ISCSI targets such as creation and deletion.
 */
@Service
@Transactional
public class IscsiTargetService {
    final static Logger logger = LoggerFactory.getLogger(IscsiTargetService.class);

    private UniqueIscsiTargetRepository uniqueIscsiTargetRepository;
    private StorageAgentClientFactory storageAgentClientFactory;
    private DtoValidator dtoValidator;

    @Autowired
    public IscsiTargetService(UniqueIscsiTargetRepository uniqueIscsiTargetRepository,
                              StorageAgentClientFactory storageAgentClientFactory, DtoValidator dtoValidator) {
        this.uniqueIscsiTargetRepository = uniqueIscsiTargetRepository;
        this.storageAgentClientFactory = storageAgentClientFactory;
        this.dtoValidator = dtoValidator;
    }

    /**
     * Create the ISCSI target with the given spec.
     *
     * @param iscsiTarget ISCSI target to create
     * @return the created ISCSI target.
     * @throws DuplicateTargetNameException if name already exists
     * @throws ConstraintViolationException if ISCSI target spec is invalid
     */
    public UniqueIscsiTarget createUniqueIscsiTarget(IscsiTarget iscsiTarget) throws DuplicateTargetNameException,
            ConstraintViolationException {
        logger.info("Creating unique iscsi target named: " + iscsiTarget.getTargetName());
        dtoValidator.validate(iscsiTarget);

        if (uniqueIscsiTargetRepository.findByTargetName(iscsiTarget.getTargetName()) != null) {
            throw new DuplicateTargetNameException(iscsiTarget.getTargetName());
        }

        StorageAgentClient storageAgentClient = storageAgentClientFactory.getBestStorageAgent(iscsiTarget);
        logger.info("Storage agent selected: " + storageAgentClient.getStorageAgentUrl());
        AccessibleIscsiTarget accessibleIscsiTarget = storageAgentClient.createIscsiTarget(iscsiTarget);

        UniqueIscsiTargetEntity uniqueIscsiTargetEntity =
                uniqueIscsiTargetRepository.save(
                        convertToUniqueIscsiTargetEntity(accessibleIscsiTarget, storageAgentClient));

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

        StorageAgentClient storageAgentClient =
                storageAgentClientFactory.getStorageClientUsedInCreating(uniqueIscsiTargetEntity);

        storageAgentClient.deleteIscsiTarget(uniqueIscsiTargetEntity.getTargetName());
    }

    private UniqueIscsiTargetEntity convertToUniqueIscsiTargetEntity(AccessibleIscsiTarget accessibleIscsiTarget,
                                                                     StorageAgentClient storageAgentClient) {
        List<InitiatorIqnEntity> initiatorIqnEntities = new ArrayList<>();

        IscsiTarget iscsiTarget = accessibleIscsiTarget.getIscsiTarget();
        for (String iqn : iscsiTarget.getHostIscsiQualifiedNames()) {
            initiatorIqnEntities.add(new InitiatorIqnEntity(iqn));
        }

        return new UniqueIscsiTargetEntity(initiatorIqnEntities, iscsiTarget.getCapacityInMb(),
                iscsiTarget.getTargetName(), storageAgentClient.getStorageAgentUrl(),
                accessibleIscsiTarget.getStorageNetworkAddresses().get(0));
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

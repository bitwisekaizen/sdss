package com.bitwisekaizen.sdss.management.entity;

import java.util.List;
import java.util.UUID;

/**
 * Entity that represents a unique ISCSI target in the system.
 */
public class UniqueIscsiTargetEntity {

    private long id;

    private final String uuid;

    private List<InitiatorIqnEntity> initiatorIqnEntities;

    private int capacityInMb;
    private String targetName;
    private String storageIpAddress;

    protected UniqueIscsiTargetEntity() {
        this.uuid = UUID.randomUUID().toString();
    }

    public UniqueIscsiTargetEntity(List<InitiatorIqnEntity> initiatorIqnEntities, int capacityInMb, String targetName,
                                   String storageIpAddress) {
        this();
        this.initiatorIqnEntities = initiatorIqnEntities;
        this.capacityInMb = capacityInMb;
        this.targetName = targetName;
        this.storageIpAddress = storageIpAddress;
    }

    /**
     * Get the unique DB ID.
     *
     * @return ID of the ISCSI target.
     */
    public long getId() {
        return id;
    }

    /**
     * Get the unique UUID.
     *
     * @return UUID of the ISCSI target.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Get the LUN target capacity in MB.
     *
     * @return the LUN target capacity in MB.
     */
    public int getCapacityInMb() {
        return capacityInMb;
    }

    /**
     * Get the list of ISCSI qualified names of the hosts, permissible to access this target.
     *
     * @return the list of ISCSI qualified names of the hosts, permissible to access this target.
     */
    public List<InitiatorIqnEntity> getHostIscsiQualifiedNames() {
        return initiatorIqnEntities;
    }

    /**
     * Get the target name.
     *
     * @return target name.
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * IP location on which this target exists.
     *
     * @return IP location on which this target exists.
     */
    public String getStorageIpAddress() {
        return storageIpAddress;
    }

    @Override
    public String toString() {
        return String.format("IscsiTargetEntity[id=%d, uuid='%s', targetName='%s']", id, uuid, targetName);
    }
}
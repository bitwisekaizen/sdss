package com.bitwisekaizen.sdss.dto;

/**
 * ISCSI target currently managed by the SDSS.
 */
public class IscsiTarget {

    private String uuid;
    private String storageIpAddress;
    private IscsiTargetSpec iscsiTargetSpec;


    // Json serialization
    private IscsiTarget() {
    }

    public IscsiTarget(String uuid, String storageIpAddress, IscsiTargetSpec iscsiTargetSpec) {
        this.uuid = uuid;
        this.storageIpAddress = storageIpAddress;
        this.iscsiTargetSpec = iscsiTargetSpec;
    }

    /**
     * Get the UUID associated with the ISCSI target.
     *
     * @return UUID of the ISCSI target.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Get the storage server IP that the target is located in.
     *
     * @return the storage server IP that the target is located in.
     */
    public String getStorageIpAddress() {
        return storageIpAddress;
    }

    /**
     * Get spec describing the ISCSI target.
     *
     * @return ISCSI target spec.
     */
    public IscsiTargetSpec getIscsiTargetSpec() {
        return iscsiTargetSpec;
    }
}

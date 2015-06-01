package com.bitwisekaizen.sdss.management.dto;

import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Unique ISCSI target currently managed by the SDSS.
 */
public class UniqueIscsiTarget {

    private String uuid;
    private String storageIpAddress;
    private IscsiTarget iscsiTarget;


    // Json serialization
    private UniqueIscsiTarget() {
    }

    public UniqueIscsiTarget(String uuid, String storageIpAddress, IscsiTarget iscsiTarget) {
        this.uuid = uuid;
        this.storageIpAddress = storageIpAddress;
        this.iscsiTarget = iscsiTarget;
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
    public IscsiTarget getIscsiTarget() {
        return iscsiTarget;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

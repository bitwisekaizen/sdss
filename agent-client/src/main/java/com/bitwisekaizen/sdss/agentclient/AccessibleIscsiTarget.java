package com.bitwisekaizen.sdss.agentclient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * ISCSI target that is accessible on the network.
 */
public class AccessibleIscsiTarget {

    private List<String> storageNetworkAddresses;
    private IscsiTarget iscsiTarget;

    // Json serialization
    private AccessibleIscsiTarget() {
    }

    public AccessibleIscsiTarget(IscsiTarget iscsiTarget, List<String> storageNetworkAddresses) {
        this.iscsiTarget = iscsiTarget;
        this.storageNetworkAddresses = storageNetworkAddresses;
    }

    /**
     * List of IP addresses that the target are bounded to.
     *
     * @return IP addresses that the target are bounded to.
     */
    public List<String> getStorageNetworkAddresses() {
        return storageNetworkAddresses;
    }

    public IscsiTarget getIscsiTarget() {
        return iscsiTarget;
    }

    @JsonIgnore
    public String getTargetName() {
        return iscsiTarget.getTargetName();
    }
}

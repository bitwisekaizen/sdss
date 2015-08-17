package com.bitwisekaizen.sdss.management.dto;

import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Unique ISCSI target currently managed by the SDSS.
 */
@ApiModel(description = "Unique network-accessible ISCSI target managed by the SDSS")
public class UniqueIscsiTarget {

    private String uuid;
    private String storageIpAddress;
    private String storageAgentUrl;
    private IscsiTarget iscsiTarget;


    // Json serialization
    private UniqueIscsiTarget() {
    }

    public UniqueIscsiTarget(String uuid, String storageIpAddress, String storageAgentUrl, IscsiTarget iscsiTarget) {
        this.uuid = uuid;
        this.storageIpAddress = storageIpAddress;
        this.storageAgentUrl = storageAgentUrl;
        this.iscsiTarget = iscsiTarget;
    }

    /**
     * Get the UUID associated with the ISCSI target.
     *
     * @return UUID of the ISCSI target.
     */
    @ApiModelProperty(value = "ISCSI target unique UUID", required = true)
    public String getUuid() {
        return uuid;
    }

    /**
     * Get the storage server IP that the target is located in.
     *
     * @return the storage server IP that the target is located in.
     */
    @ApiModelProperty(value = "IP address that the ISCSI target can be accessed with", required = true)
    public String getStorageIpAddress() {
        return storageIpAddress;
    }

    /**
     * Get the management URL of the agent node.
     *
     * @return mangement URL of the agent node.
     */
    @ApiModelProperty(value = "Management URL of the agent node.", required = true)
    public String getStorageAgentUrl() {
        return storageAgentUrl;
    }

    /**
     * Get spec describing the ISCSI target.
     *
     * @return ISCSI target spec.
     */
    @ApiModelProperty(value = "ISCSI target spec", required = true)
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

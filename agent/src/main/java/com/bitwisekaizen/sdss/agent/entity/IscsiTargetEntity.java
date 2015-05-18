package com.bitwisekaizen.sdss.agent.entity;

import java.util.List;

/**
 * Spec that describes the ISCSI target
 */
public class IscsiTargetEntity {

    private List<String> hostIscsiQualifiedNames;
    private int capacityInMb;
    private String targetName;

    // Json serialization
    private IscsiTargetEntity() {
    }

    public IscsiTargetEntity(List<String> hostIscsiQualifiedNames, int capacityInMb, String targetName) {
        this.hostIscsiQualifiedNames = hostIscsiQualifiedNames;
        this.capacityInMb = capacityInMb;
        this.targetName = targetName;
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
    public List<String> getHostIscsiQualifiedNames() {
        return hostIscsiQualifiedNames;
    }

    /**
     * Get the target name.
     *
     * @return target name.
     */
    public String getTargetName() {
        return targetName;
    }
}

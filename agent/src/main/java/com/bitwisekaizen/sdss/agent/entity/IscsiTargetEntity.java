package com.bitwisekaizen.sdss.agent.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Spec that describes the ISCSI target
 */
@Entity
@Table(name = "iscsi_target")
public class IscsiTargetEntity {

    @Id
    @Column(name = "target_name")
    private String targetName;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @Column(name = "iqn")
    @CollectionTable(name = "host_iqn", joinColumns = @JoinColumn(name = "iscsi_target_target_name"))
    private List<String> hostIscsiQualifiedNames;

    @Column(name = "capacity_mb")
    private int capacityInMb;

    @Column(name = "affinity_key")
    private String affinityKey;

    // Json serialization
    private IscsiTargetEntity() {
    }

    public IscsiTargetEntity(List<String> hostIscsiQualifiedNames, int capacityInMb, String targetName,
                             String affinityKey) {
        this.hostIscsiQualifiedNames = hostIscsiQualifiedNames;
        this.capacityInMb = capacityInMb;
        this.targetName = targetName;
        this.affinityKey = affinityKey;
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

    public String getAffinityKey() {
        return affinityKey;
    }
}

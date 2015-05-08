package com.bitwisekaizen.sdss.management.entity;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Entity that represents a unique ISCSI target in the system.
 */
@Entity
@Table(name = "unique_iscsi_target")
public class UniqueIscsiTargetEntity {

    @Id
    @Column(name = "uuid")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String uuid;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="unique_iscsi_target_uuid", referencedColumnName = "uuid")
    private List<InitiatorIqnEntity> initiatorIqnEntities;

    @Column(name = "capacity_mb")
    private int capacityInMb;

    @Column(name = "target_name")
    private String targetName;

    @Column(name = "storage_host")
    private String storageHost;

    protected UniqueIscsiTargetEntity() {
    }

    public UniqueIscsiTargetEntity(List<InitiatorIqnEntity> initiatorIqnEntities, int capacityInMb, String targetName,
                                   String storageHost) {
        this();
        this.initiatorIqnEntities = initiatorIqnEntities;
        this.capacityInMb = capacityInMb;
        this.targetName = targetName;
        this.storageHost = storageHost;
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
    public List<InitiatorIqnEntity> getInitiatorIqnEntities() {
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
     * Host/IP location on which this target exists.
     *
     * @return Host/IP location on which this target exists.
     */
    public String getStorageHost() {
        return storageHost;
    }

    @Override
    public String toString() {
        return String.format("IscsiTargetEntity[uuid='%s', targetName='%s']", uuid, targetName);
    }

    @VisibleForTesting
    protected void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
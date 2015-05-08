package com.bitwisekaizen.sdss.management.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Entity that represents an initiator's ISCSI qualified name.
 */
@Entity
@Table(name = "initiator_iqn")
public class InitiatorIqnEntity {

    @Id
    @Column(name = "uuid")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String uuid;

    @Column(name = "iqn")
    private String iqn;

    // Hibernate serialization
    protected InitiatorIqnEntity() {
    }

    public InitiatorIqnEntity(String iqn) {
        this();
        this.iqn = iqn;
    }

    /**
     * Get the ISCSI qualified name of the initiator.
     *
     * @return the ISCSI qualified name of the initiator.
     */
    public String getIqn() {
        return iqn;
    }

    @Override
    public String toString() {
        return String.format("InitiatorIqnEntity[uuid=%d, iqn='%s']", uuid, iqn);
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
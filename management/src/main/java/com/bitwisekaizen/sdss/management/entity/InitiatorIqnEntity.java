package com.bitwisekaizen.sdss.management.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;
import java.util.UUID;

/**
 * Entity that represents an initiator's ISCSI qualified name.
 */
public class InitiatorIqnEntity {

    private long id;
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
        return String.format("InitiatorIqnEntity[id=%d, iqn='%s']", id, iqn);
    }
}
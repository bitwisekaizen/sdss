package com.bitwisekaizen.sdss.management.entity;

import com.bitwisekaizen.sdss.management.dto.IscsiTarget;
import org.apache.commons.lang3.builder.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class IscsiTargetEntityBuilder implements Builder<UniqueIscsiTargetEntity> {

    private List<InitiatorIqnEntity> initiatorIqnEntities = new ArrayList<>();
    private int capacityInMb = ThreadLocalRandom.current().nextInt(0, 100000);
    private String targetName = "targetName-" + UUID.randomUUID().toString();
    private String storageIpAddress = "storageIp-" + UUID.randomUUID().toString();;

    private IscsiTargetEntityBuilder() {
    }

    public static IscsiTargetEntityBuilder anIscsiTargetEntity() {
        return new IscsiTargetEntityBuilder();
    }

    public static IscsiTargetEntityBuilder anIscsiTargetEntityFrom(IscsiTarget iscsiTarget) {
        IscsiTargetEntityBuilder iscsiTargetEntityBuilder = anIscsiTargetEntity();
        for (String iqn : iscsiTarget.getHostIscsiQualifiedNames()) {
            iscsiTargetEntityBuilder.initiatorIqnEntities.add(new InitiatorIqnEntity(iqn));
        }

        iscsiTargetEntityBuilder.capacityInMb = iscsiTarget.getCapacityInMb();
        iscsiTargetEntityBuilder.targetName = iscsiTarget.getTargetName();
        return iscsiTargetEntityBuilder;
    }

    @Override public UniqueIscsiTargetEntity build() {
        return new UniqueIscsiTargetEntity(initiatorIqnEntities, capacityInMb, targetName, storageIpAddress);
    }

    public IscsiTargetEntityBuilder withStorageIpAddress(String storageIpAddress) {
        this.storageIpAddress = storageIpAddress;
        return this;
    }
}
package com.bitwisekaizen.sdss.agent.entity;

import org.apache.commons.lang3.builder.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class IscsiTargetEntityBuilder implements Builder<IscsiTargetEntity> {
    private static final Random random = new Random();

    private List<String> hostIscsiQualifiedNames = new ArrayList<>();
    private int capacityInMb = ThreadLocalRandom.current().nextInt(10000, 20000);
    private String targetName = "targetName-" + UUID.randomUUID().toString();
    private String affinityKey = "affinityKey-" + UUID.randomUUID().toString();

    public static IscsiTargetEntityBuilder anIscsiTargetEntity() {
        IscsiTargetEntityBuilder builder = new IscsiTargetEntityBuilder();
        builder.hostIscsiQualifiedNames.add(UUID.randomUUID().toString());
        builder.hostIscsiQualifiedNames.add(UUID.randomUUID().toString());
        return builder;
    }

    @Override
    public IscsiTargetEntity build() {
        return new IscsiTargetEntity(hostIscsiQualifiedNames, capacityInMb, targetName, affinityKey);
    }

    public IscsiTargetEntityBuilder withTargetName(String targetName) {
        this.targetName = targetName;
        return this;
    }
}

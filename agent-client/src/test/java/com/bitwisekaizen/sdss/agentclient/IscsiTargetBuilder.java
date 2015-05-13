package com.bitwisekaizen.sdss.agentclient;

import org.apache.commons.lang3.builder.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class IscsiTargetBuilder implements Builder<IscsiTarget> {
    private static final Random random = new Random();

    private List<String> hostIscsiQualifiedNames = new ArrayList<>();
    private int capacityInMb = ThreadLocalRandom.current().nextInt(100, 2000);
    private String targetName = UUID.randomUUID().toString();

    public static IscsiTargetBuilder anIscsiTarget() {
        return new IscsiTargetBuilder();
    }

    @Override
    public IscsiTarget build() {
        return new IscsiTarget(hostIscsiQualifiedNames, capacityInMb, targetName);
    }

    public IscsiTargetBuilder withTargetName(String targetName) {
        this.targetName = targetName;
        return this;
    }
}

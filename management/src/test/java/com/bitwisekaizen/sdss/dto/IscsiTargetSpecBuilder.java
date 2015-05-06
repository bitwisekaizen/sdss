package com.bitwisekaizen.sdss.dto;

import org.apache.commons.lang3.builder.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class IscsiTargetSpecBuilder implements Builder<IscsiTargetSpec> {
    private static final Random random = new Random();

    private List<String> hostIscsiQualifiedNames = new ArrayList<>();
    private int capacityInMb = ThreadLocalRandom.current().nextInt(100, 2000);
    private String targetName = UUID.randomUUID().toString();

    public static IscsiTargetSpecBuilder aIscsiTargetSpec() {
        return new IscsiTargetSpecBuilder();
    }

    @Override
    public IscsiTargetSpec build() {
        return new IscsiTargetSpec(hostIscsiQualifiedNames, capacityInMb, targetName);
    }

    public IscsiTargetSpecBuilder withTargetName(String targetName) {
        this.targetName = targetName;
        return this;
    }
}

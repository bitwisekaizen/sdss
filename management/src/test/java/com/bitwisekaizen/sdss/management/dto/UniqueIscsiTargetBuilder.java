package com.bitwisekaizen.sdss.management.dto;

import com.google.common.net.InetAddresses;
import org.apache.commons.lang3.builder.Builder;

import java.util.Random;
import java.util.UUID;

public class UniqueIscsiTargetBuilder implements Builder<UniqueIscsiTarget> {
    private static final Random random = new Random();

    private String uuid = UUID.randomUUID().toString();
    private String storageIpAddress = InetAddresses.fromInteger(random.nextInt()).getHostAddress();
    private IscsiTarget iscsiTarget = IscsiTargetBuilder.anIscsiTarget().build();

    public static UniqueIscsiTargetBuilder aUniqueIscsiTarget() {
        return new UniqueIscsiTargetBuilder();
    }

    @Override
    public UniqueIscsiTarget build() {
        return new UniqueIscsiTarget(uuid, storageIpAddress, iscsiTarget);
    }
}

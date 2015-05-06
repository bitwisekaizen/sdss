package com.bitwisekaizen.sdss.dto;

import com.google.common.net.InetAddresses;
import org.apache.commons.lang3.builder.Builder;

import java.util.Random;
import java.util.UUID;

import static com.bitwisekaizen.sdss.dto.IscsiTargetSpecBuilder.aIscsiTargetSpec;

public class IscsiTargetBuilder implements Builder<IscsiTarget> {
    private static final Random random = new Random();

    private String uuid = UUID.randomUUID().toString();
    private String storageIpAddress = InetAddresses.fromInteger(random.nextInt()).getHostAddress();
    private IscsiTargetSpec iscsiTargetSpec = aIscsiTargetSpec().build();

    public static IscsiTargetBuilder aIscsiTarget() {
        return new IscsiTargetBuilder();
    }

    @Override
    public IscsiTarget build() {
        return new IscsiTarget(uuid, storageIpAddress, iscsiTargetSpec);
    }
}

package com.bitwisekaizen.sdss.management.dto;

import com.bitwisekaizen.sdss.agentclient.IscsiTarget;
import com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder;
import com.google.common.net.InetAddresses;
import org.apache.commons.lang3.builder.Builder;

import java.util.Random;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public class UniqueIscsiTargetBuilder implements Builder<UniqueIscsiTarget> {
    private static final Random random = new Random();

    private String uuid = UUID.randomUUID().toString();
    private String storageIpAddress = InetAddresses.fromInteger(random.nextInt()).getHostAddress();
    private String storageAgentUrl = "http://" + randomAlphabetic(4) + ".example.com";
    private IscsiTarget iscsiTarget = IscsiTargetBuilder.anIscsiTarget().build();

    public static UniqueIscsiTargetBuilder aUniqueIscsiTarget() {
        return new UniqueIscsiTargetBuilder();
    }

    @Override
    public UniqueIscsiTarget build() {
        return new UniqueIscsiTarget(uuid, storageIpAddress, storageAgentUrl, iscsiTarget);
    }
}

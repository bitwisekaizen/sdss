package com.bitwisekaizen.sdss.agentclient;

import org.apache.commons.lang3.builder.Builder;

import java.util.ArrayList;
import java.util.List;

public class AccessibleIscsiTargetBuilder implements Builder<AccessibleIscsiTarget> {
    private IscsiTarget iscsiTarget = IscsiTargetBuilder.anIscsiTarget().build();
    private List<String> storageNetworkAddresses = new ArrayList<>();

    public static AccessibleIscsiTargetBuilder anAccessibleIscsiTarget() {
        return new AccessibleIscsiTargetBuilder();
    }

    @Override
    public AccessibleIscsiTarget build() {
        return new AccessibleIscsiTarget(iscsiTarget, storageNetworkAddresses);
    }
}

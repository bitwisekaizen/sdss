package com.bitwisekaizen.sdss.management.entity;

import org.apache.commons.lang3.builder.Builder;

import java.util.UUID;

public class InitiatorIqnEntityBuilder implements Builder<InitiatorIqnEntity>{
    private String iqn = "iqn-" + UUID.randomUUID();

    public static final InitiatorIqnEntityBuilder anUnsavedInitiatorIqnEntity() {
        return new InitiatorIqnEntityBuilder();
    }

    @Override
    public InitiatorIqnEntity build() {
        return new InitiatorIqnEntity(iqn);
    }

    public InitiatorIqnEntityBuilder withIqn(String iqn) {
        this.iqn = iqn;
        return this;
    }
}
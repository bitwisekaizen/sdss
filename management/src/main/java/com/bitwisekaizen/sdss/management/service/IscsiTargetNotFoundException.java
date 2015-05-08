package com.bitwisekaizen.sdss.management.service;

import javax.ws.rs.NotFoundException;

/**
 * Exception thrown when ISCSI target is not found.
 */
public class IscsiTargetNotFoundException extends NotFoundException {

    public IscsiTargetNotFoundException(String uuid) {
        super(String.format("ISCSI target %s not found", uuid));
    }
}

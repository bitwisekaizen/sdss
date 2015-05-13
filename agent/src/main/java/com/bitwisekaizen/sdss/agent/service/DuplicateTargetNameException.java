package com.bitwisekaizen.sdss.agent.service;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

/**
 *
 */
public class DuplicateTargetNameException extends ClientErrorException {

    public DuplicateTargetNameException(String targetName) {
        super(targetName + " already exists", Response.Status.CONFLICT);
    }
}

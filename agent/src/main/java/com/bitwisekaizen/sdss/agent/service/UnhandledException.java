package com.bitwisekaizen.sdss.agent.service;

import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Server error that is not expected and not recoverable.
 */
public class UnhandledException extends ServerErrorException {
    public UnhandledException(String message) {
        super(message, Response.Status.INTERNAL_SERVER_ERROR);
    }

    public UnhandledException(Exception e) {
        super(Response.Status.INTERNAL_SERVER_ERROR, e);
    }
}

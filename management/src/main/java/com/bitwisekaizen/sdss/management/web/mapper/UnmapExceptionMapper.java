package com.bitwisekaizen.sdss.management.web.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handler to display exception that isn't mapped.
 */
@Provider
public class UnmapExceptionMapper implements ExceptionMapper<Exception> {
    final static Logger logger = LoggerFactory.getLogger(UnmapExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        logger.error(this.getClass().getSimpleName(), exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                exception.getMessage()).type("text/plain").build();
    }
}

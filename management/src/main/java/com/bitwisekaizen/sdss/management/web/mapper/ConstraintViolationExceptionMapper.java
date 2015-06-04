package com.bitwisekaizen.sdss.management.web.mapper;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Handler to display all the constraint violations.
 */
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    final static Logger logger = LoggerFactory.getLogger(ConstraintViolationException.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        logger.error(this.getClass().getSimpleName(), exception);
        return Response.status(HttpStatus.SC_BAD_REQUEST).entity(exception.getConstraintViolations().toString())
                .type("text/plain").build();
    }
}

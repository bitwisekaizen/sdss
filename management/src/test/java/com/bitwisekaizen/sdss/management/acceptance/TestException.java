package com.bitwisekaizen.sdss.management.acceptance;

/**
 * Exception thrown by the tests.  This is mainly used to avoid throwing checked exceptions.
 */
public class TestException extends RuntimeException {
    public TestException(String message) {
        super(message);
    }
}

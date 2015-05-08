package com.bitwisekaizen.sdss.management.testng;


import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

/**
 * Listener to print out log information and exceptions for TestNg suite run.
 */
public class TestNgLoggingListener extends TestListenerAdapter implements ISuiteListener {
    private boolean alreadyCalled = false;

    private Logger logger;

    public TestNgLoggingListener() {
        logger = LoggerFactory.getLogger(TestNgLoggingListener.class);
    }

    protected TestNgLoggingListener(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void beforeConfiguration(ITestResult result) {
        super.beforeConfiguration(result);
        if (skipPrinting(result)) {
            return;
        }

        logger.info("#########################################");
        logger.info(methodNameFrom(result) + " Configuration method is starting");
    }

    @Override
    public void onConfigurationSuccess(ITestResult result) {
        super.onConfigurationSuccess(result);
        if (skipPrinting(result)) {
            return;
        }

        logger.info(methodNameFrom(result) + " Configuration method is SUCCESS.");
    }

    @Override
    public void onConfigurationFailure(ITestResult result) {
        super.onConfigurationFailure(result);

        printOutAnyWebApplicationExceptionMessage(result.getThrowable());
        logger.info(methodNameFrom(result) + " Configuration method is FAILURE.");
    }

    @Override
    public void onConfigurationSkip(ITestResult result) {
        super.onConfigurationSkip(result);

        logger.info(methodNameFrom(result) + " Configuration method is SKIP.");
    }

    @Override
    public void onTestStart(ITestResult result) {
        super.onTestStart(result);

        logger.info("#########################################");
        logger.info(methodNameFrom(result) + " Test method is starting");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        super.onTestSuccess(result);

        logger.info(methodNameFrom(result) + " Test method is SUCCESS.");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        super.onTestFailure(result);

        processFailure(result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        super.onTestFailedButWithinSuccessPercentage(result);

        processFailure(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        super.onTestSkipped(result);

        logger.info(methodNameFrom(result) + " Test method is SKIP.");
    }

    private void processFailure(ITestResult result) {
        printOutAnyWebApplicationExceptionMessage(result.getThrowable());
        logger.info(methodNameFrom(result) + " Test method is FAILURE.", result.getThrowable());
    }

    private String methodNameFrom(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName() + "." + result.getName();
    }

    /**
     * Skip printing configuration methods from spring context.
     *
     * @param result result to check if printing should be skipped
     * @return true means to skip.
     * @see org.springframework.test.context.testng.AbstractTestNGSpringContextTests
     */
    private boolean skipPrinting(ITestResult result) {
        return result.getName().contains("springTestContext");
    }

    private void printOutAnyWebApplicationExceptionMessage(Throwable throwable) {
        if (throwable instanceof WebApplicationException) {
            WebApplicationException exception = (WebApplicationException) throwable;
            logger.error("WebApplicationException message is {}", exception.getMessage());
            Response response = exception.getResponse();
            try {
                if (response.hasEntity()) {
                    if (response.getEntity() instanceof InputStream) {
                        try {
                            logger.error("Error response body: "
                                    + IOUtils.toString((InputStream) response.getEntity()));
                        } catch (IOException e) {}
                    } else {
                        logger.error("Error response body: " + response.getEntity());
                    }
                }
            } finally {
                response.close();
            }
        }
    }

    @Override
    public void onStart(ISuite suite) {
    }

    @Override
    public void onFinish(ISuite suite) {
        if (alreadyCalled) {
            return;
        }
        alreadyCalled = true;

        logger.info("Total tests:   " + getAllTestMethods().length);
        logger.info("Passing tests: " + getPassedTests().size());
        logger.info("Failed tests:  " + (getFailedTests().size() + getFailedButWithinSuccessPercentageTests().size()));
    }
}

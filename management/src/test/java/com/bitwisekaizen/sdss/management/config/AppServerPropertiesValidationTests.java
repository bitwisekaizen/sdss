package com.bitwisekaizen.sdss.management.config;

import com.bitwisekaizen.sdss.management.config.AppServerProperties;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AppServerPropertiesValidationTests {

    private Validator validator;
    private AppServerProperties appServerProperties;

    @BeforeClass
    public void beforeClass() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeMethod
    public void beforeMethod() {
        appServerProperties = new AppServerProperties();
    }

    @Test
    public void canSetToValidPort() {
        appServerProperties.setPort(80);

        Set<ConstraintViolation<AppServerProperties>> result = validator.validate(appServerProperties);
        assertThat(result, empty());
    }

    @Test
    public void cannotSetPortToNegativeValue() {
        appServerProperties.setPort(-1);

        Set<ConstraintViolation<AppServerProperties>> result = validator.validate(appServerProperties);
        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getMessage(), equalTo("must be between 0 and 65535"));
    }

    @Test
    public void cannotSetPortToValueTooLargeNegativeValue() {
        appServerProperties.setPort(999999);

        Set<ConstraintViolation<AppServerProperties>> result = validator.validate(appServerProperties);
        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getMessage(), equalTo("must be between 0 and 65535"));
    }
}

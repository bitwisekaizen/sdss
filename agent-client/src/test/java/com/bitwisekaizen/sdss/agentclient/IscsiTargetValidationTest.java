package com.bitwisekaizen.sdss.agentclient;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static com.bitwisekaizen.sdss.agentclient.IscsiTargetBuilder.anIscsiTarget;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class IscsiTargetValidationTest {

    private Validator validator;

    @BeforeClass
    public void beforeClass() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void validationShouldPassForValidIscsiTarget() {
        Set<ConstraintViolation<IscsiTarget>> violations = getViolations(anIscsiTarget().build());

        assertThat(violations, hasSize(0));
    }

    @Test
    public void validationShouldFailForEmptyTargetName() {
        Set<ConstraintViolation<IscsiTarget>> violations = getViolations(anIscsiTarget().withTargetName("").build());

        assertThat(violations, hasSize(1));
        assertThat(violations, hasItem(matchingMessage("target.name.empty")));
    }

    protected <T> Set<ConstraintViolation<T>> getViolations(T objectToValidate) {
        return validator.validate(objectToValidate);
    }

    private TypeSafeMatcher<ConstraintViolation<IscsiTarget>> matchingMessage(final String expectedMessage) {
        return new TypeSafeMatcher<ConstraintViolation<IscsiTarget>>() {
            @Override
            protected boolean matchesSafely(ConstraintViolation<IscsiTarget> constraintViolation) {
                return constraintViolation.getMessage().equals(expectedMessage);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expectedMessage);
            }
        };
    }
}

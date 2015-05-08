package com.bitwisekaizen.sdss.management.dto;

import com.bitwisekaizen.sdss.management.validation.DtoValidator;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.Set;

import static com.bitwisekaizen.sdss.management.dto.IscsiTargetBuilder.anIscsiTarget;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class IscsiTargetValidationTest {

    private DtoValidator dtoValidator;

    @BeforeClass
    public void beforeClass() {
        dtoValidator = new DtoValidator();
    }

    @BeforeMethod
    public void beforeMethod() {

    }

    @Test
    public void validationShouldPassForValidIscsiTarget() {
        Set<ConstraintViolation<?>> violations = getViolations(anIscsiTarget().build());

        assertThat(violations, hasSize(0));
    }

    @Test
    public void validationShouldFailForEmptyTargetName() {
        Set<ConstraintViolation<?>> violations = getViolations(anIscsiTarget().withTargetName("").build());

        assertThat(violations, hasSize(1));
        assertThat(violations, hasItem(matchingMessage("target.name.empty")));
    }

    protected Set<ConstraintViolation<?>> getViolations(Object objectToValidate) {
        try {
            dtoValidator.validate(objectToValidate);
            return new HashSet<>();
        } catch (ConstraintViolationException e) {
            return e.getConstraintViolations();
        }
    }

    private TypeSafeMatcher<ConstraintViolation<?>> matchingMessage(final String expectedMessage) {
        return new TypeSafeMatcher<ConstraintViolation<?>>() {
            @Override
            protected boolean matchesSafely(ConstraintViolation<?> constraintViolation) {
                return constraintViolation.getMessage().equals(expectedMessage);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expectedMessage);
            }
        };
    }
}

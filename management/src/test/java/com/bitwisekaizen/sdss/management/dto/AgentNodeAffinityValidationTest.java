package com.bitwisekaizen.sdss.management.dto;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static com.bitwisekaizen.sdss.management.dto.AgentNodeAffinityBuilder.anAgentNodeAffinity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class AgentNodeAffinityValidationTest {

    private Validator validator;

    @BeforeClass
    public void beforeClass() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void validationShouldPassForValidAffinity() {
        Set<ConstraintViolation<AgentNodeAffinity>> violations = getViolations(anAgentNodeAffinity().build());

        assertThat(violations, hasSize(0));
    }

    @Test
    public void validationShouldFailForEmptyAffinityKey() {
        Set<ConstraintViolation<AgentNodeAffinity>> violations = getViolations(anAgentNodeAffinity().
                withAffinityKey("").build());

        assertThat(violations, hasSize(1));
        assertThat(violations, hasItem(matchingMessage("affinity.key.empty")));
    }

    @Test
    public void validationShouldFailForEmptyAgentNode() {
        Set<ConstraintViolation<AgentNodeAffinity>> violations = getViolations(anAgentNodeAffinity().
                withStorageAgentNode("").build());

        assertThat(violations, hasSize(1));
        assertThat(violations, hasItem(matchingMessage("agent.node.value.empty")));
    }

    protected <T> Set<ConstraintViolation<T>> getViolations(T objectToValidate) {
        return validator.validate(objectToValidate);
    }

    private TypeSafeMatcher<ConstraintViolation<AgentNodeAffinity>> matchingMessage(final String expectedMessage) {
        return new TypeSafeMatcher<ConstraintViolation<AgentNodeAffinity>>() {
            @Override
            protected boolean matchesSafely(ConstraintViolation<AgentNodeAffinity> constraintViolation) {
                return constraintViolation.getMessage().equals(expectedMessage);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expectedMessage);
            }
        };
    }
}

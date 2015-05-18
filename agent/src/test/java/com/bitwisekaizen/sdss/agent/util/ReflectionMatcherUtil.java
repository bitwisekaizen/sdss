package com.bitwisekaizen.sdss.agent.util;

import junit.framework.AssertionFailedError;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;


/**
 * Utility for testing equality using matcher and reflection.
 */
public class ReflectionMatcherUtil {

    /**
     * Matches the specified object using deep equality using reflection.
     *
     * @param expectedObject object to check
     * @return matcher.
     */
    public static <T> TypeSafeMatcher<T> reflectionMatching(final T expectedObject) {
        return new TypeSafeMatcher<T>() {
            public T actualObject;

            @Override
            protected boolean matchesSafely(T actualObject) {
                this.actualObject = actualObject;
                try {
                    assertReflectionEquals(expectedObject, actualObject);
                } catch (AssertionFailedError assertionFailedError) {
                    return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected " + expectedObject.toString() + " but got " + actualObject);
            }
        };
    }
}
package com.bitwisekaizen.sdss.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Object that wants custom validation can apply this Annotation on a boolean method within that object.
 */
@Target({TYPE, ANNOTATION_TYPE, METHOD})
@Retention(RUNTIME)
@Constraint(validatedBy = MethodValidator.class)
public @interface ValidationMethod {
    String message() default "is not valid";

    Class<?>[] groups() default { };

   	Class<? extends Payload>[] payload() default { };
}

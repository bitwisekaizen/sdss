package com.bitwisekaizen.sdss.agent.validation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator of DTO requests.
 */
@Component
public class DtoValidator {
    private static final Logger logger = LoggerFactory.getLogger(DtoValidator.class);

    private final Validator validator;

    public DtoValidator() {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * Validate the specified object if the object is in the specified group.
     *
     * @param object object to verify
     * @param groups group to verify; if empty, all groups are verified
     * @throws ConstraintViolationException if validation fails
     */
    public void validate(final Object object, final Class<?>... groups) {
        Set<ConstraintViolation<?>> violations = new HashSet<ConstraintViolation<?>>();
        violations.addAll(validator.validate(object, groups));
        if (!violations.isEmpty()) {
            for (ConstraintViolation<?> violation : violations) {
                if (violation.getPropertyPath() != null) {
                    logger.error("ConstraintViolation error for field " + violation.getPropertyPath().toString() +
                            " in object " + violation.getRootBeanClass() + ": " + violation.getMessage());
                }
            }
            throw new ConstraintViolationException(violations);
        }
    }
}

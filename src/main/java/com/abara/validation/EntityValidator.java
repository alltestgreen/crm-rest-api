package com.abara.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EntityValidator {

    private static final Logger LOG = LoggerFactory.getLogger(EntityValidator.class);

    @Autowired
    private Validator validator;

    public Optional<ValidationResult> validate(Object obj) {
        Set<ConstraintViolation<Object>> violations = validator.validate(obj);
        if (!violations.isEmpty()) {
            Map<String, String> errors = violations.stream()
                    .collect(Collectors.toMap(v -> v.getPropertyPath().toString(), ConstraintViolation::getMessage));

            ValidationResult validationResult = new ValidationResult(obj.getClass().getSimpleName(), errors);

            LOG.debug("Validation failed: " + validationResult);
            return Optional.of(validationResult);
        }
        return Optional.empty();
    }

}

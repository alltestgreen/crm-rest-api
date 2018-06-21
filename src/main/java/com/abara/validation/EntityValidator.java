package com.abara.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EntityValidator {

    @Autowired
    private Validator validator;

    public ValidationResult validate(Object obj) {
        Set<ConstraintViolation<Object>> violations = validator.validate(obj);
        if (!violations.isEmpty()) {
            List<String> errors = violations.stream().map(v -> v.getPropertyPath() + ":" + v.getMessage()).collect(Collectors.toList());
            return new ValidationResult(obj.getClass().getSimpleName(), errors);
        }
        return new ValidationResult(obj.getClass().getSimpleName(), null);
    }

}

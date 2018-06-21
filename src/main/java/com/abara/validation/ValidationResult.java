package com.abara.validation;

import java.util.List;

public class ValidationResult {

    private final String entityName;
    private final List<String> errors;

    ValidationResult(String entityName, List<String> errors) {
        this.entityName = entityName;
        this.errors = errors;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public String getEntityName() {
        return entityName;
    }

    public List<String> getErrors() {
        return errors;
    }

}

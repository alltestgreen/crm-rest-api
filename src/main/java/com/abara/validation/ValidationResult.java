package com.abara.validation;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;

public class ValidationResult {

    private String entityName;
    private Map<String, String> errors;

    ValidationResult() {
    }

    ValidationResult(String entityName, Map<String, String> errors) {
        this.entityName = entityName;
        this.errors = errors;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public String getEntityName() {
        return entityName;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}

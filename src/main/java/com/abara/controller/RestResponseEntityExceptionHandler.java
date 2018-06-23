package com.abara.controller;

import com.abara.validation.ValidationException;
import com.abara.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ResponseStatus(value = HttpStatus.NO_CONTENT, reason = "Entity not found by ID")
    @ExceptionHandler(EntityNotFoundException.class)
    public void entityNotFoundHandler(final Exception e) {
        LOG.error(e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationResult> validationErrorHandler(final ValidationException e) {
        return ResponseEntity.badRequest().body(e.getValidationResult());
    }
}

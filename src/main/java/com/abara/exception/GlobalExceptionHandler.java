package com.abara.exception;

import com.abara.validation.ValidationException;
import com.abara.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.util.AbstractMap;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFoundException(final EntityNotFoundException e) {
        LOG.error(e.getMessage());
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationResult> handleValidationException(final ValidationException e) {
        return ResponseEntity.badRequest().body(e.getValidationResult());
    }

    @ExceptionHandler
    public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handleException(final Exception e) {
        LOG.error("Exception: Unable to process this request. ", e);
        AbstractMap.SimpleEntry<String, String> response =
                new AbstractMap.SimpleEntry<>("message", "Unable to process this request.");
        return ResponseEntity.badRequest().body(response);
    }
}

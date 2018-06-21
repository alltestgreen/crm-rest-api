package com.abara.validation;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class EntityValidatorTest {

    @Mock
    private Validator validator;

    @InjectMocks
    private EntityValidator entityValidator;

    @Mock
    private ConstraintViolation<Object> constraintViolation;

    @Test
    public void validate() {
        Object object = new Object();
        String errorMessage = "message";
        String property = "property";

        given(constraintViolation.getMessage()).willReturn(errorMessage);
        given(constraintViolation.getPropertyPath()).willReturn(PathImpl.createPathFromString(property));
        given(validator.validate(object)).willReturn(Collections.singleton(constraintViolation));

        Optional<ValidationResult> validationResultOptional = entityValidator.validate(object);

        assertTrue(validationResultOptional.isPresent());
        ValidationResult validationResult = validationResultOptional.get();
        assertEquals(1, validationResult.getErrors().size());
        assertEquals(errorMessage, validationResult.getErrors().get(property));
    }
}
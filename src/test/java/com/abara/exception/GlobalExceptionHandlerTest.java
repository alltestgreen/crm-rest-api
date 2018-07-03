package com.abara.exception;

import com.abara.validation.ValidationException;
import com.abara.validation.ValidationResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.persistence.EntityNotFoundException;
import java.util.AbstractMap;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GlobalExceptionHandlerTest {

    @Autowired
    private GlobalExceptionHandler exceptionHandler;

    @Test
    public void handleEntityNotFoundException() {
        String message = "message";
        EntityNotFoundException ex = new EntityNotFoundException(message);

        ResponseEntity<Void> responseEntity = exceptionHandler.handleEntityNotFoundException(ex);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void handleValidationException() {
        ValidationResult validationResult = new ValidationResult("entityName", null);
        ValidationException ex = new ValidationException(validationResult);

        ResponseEntity responseEntity = exceptionHandler.handleValidationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(validationResult, responseEntity.getBody());
    }

    @Test
    public void handleException() {
        Exception ex = new HttpRequestMethodNotSupportedException("GET", Arrays.asList("POST", "DELETE"));

        ResponseEntity responseEntity = exceptionHandler.handleException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(new AbstractMap.SimpleEntry<>("message", "Unable to process this request."), responseEntity.getBody());
    }
}
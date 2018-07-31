package com.abara.controller;

import com.abara.common.AbstractIntegrationTest;
import com.abara.entity.Customer;
import com.abara.model.CustomerDetails;
import com.abara.validation.ValidationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static com.abara.controller.CustomerController.API_CUSTOMER_PATH;
import static org.junit.Assert.*;

public class CustomerControllerIntegrationTest extends AbstractIntegrationTest {

    @Value("${api.username}")
    private String apiUser;

    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        restTemplate = buildRestTemplate();
    }

    @Test
    public void testRetrieveAll() {
        ResponseEntity<List<CustomerDetails>> response = restTemplate.exchange(
                createURLWithPort(API_CUSTOMER_PATH), HttpMethod.GET, new HttpEntity<>(null), new ParameterizedTypeReference<List<CustomerDetails>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<CustomerDetails> customerMap = response.getBody();

        Assert.assertEquals("John", customerMap.get(0).getName());
        Assert.assertEquals("Smith", customerMap.get(0).getSurname());
        Assert.assertEquals("Grace", customerMap.get(1).getName());
        Assert.assertEquals("Clayson", customerMap.get(1).getSurname());
    }

    @Test
    public void testRetrieveById() {
        Long testID = 2L;
        ResponseEntity<CustomerDetails> response = restTemplate.getForEntity(
                createURLWithPortAndId(API_CUSTOMER_PATH, testID), CustomerDetails.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        CustomerDetails customerDetails = response.getBody();
        Assert.assertNotNull(customerDetails);

        assertEquals("Grace", customerDetails.getName());
        assertEquals("Clayson", customerDetails.getSurname());
        assertEquals("gclay", customerDetails.getUsername());
        assertEquals("grace.clayson@company.com", customerDetails.getEmail());
    }

    @Test
    public void testCreate() {
        String testName = "Cassie";
        String testSurName = "Mckenzie";
        Customer newCustomer = new Customer("mcassie", testName, testSurName, "cassie.mckenzie@company.com", "imageUuid");

        ResponseEntity<ValidationResult> response = restTemplate.postForEntity(
                createURLWithPort(API_CUSTOMER_PATH), new HttpEntity<>(newCustomer), ValidationResult.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        URI resourceURL = response.getHeaders().getLocation();
        assertTrue(resourceURL.toString().contains(API_CUSTOMER_PATH));

        ResponseEntity<CustomerDetails> getResponse = restTemplate.getForEntity(resourceURL, CustomerDetails.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        CustomerDetails customerDetails = getResponse.getBody();
        assertEquals(newCustomer.getUsername(), customerDetails.getUsername());
        assertEquals(newCustomer.getName(), customerDetails.getName());
        assertEquals(newCustomer.getSurname(), customerDetails.getSurname());
        assertEquals(newCustomer.getEmail(), customerDetails.getEmail());
        assertNotNull(customerDetails.getImageURI().toString());
        assertNull(customerDetails.getModifiedBy());
        assertEquals(apiUser, customerDetails.getCreatedBy());
    }

    @Test(expected = HttpClientErrorException.class)
    public void testCreateValidationFail() throws IOException {
        String invalidUserName = StringUtils.repeat("y", 270);
        String invalidName = StringUtils.repeat("y", 270);
        String invalidSurName = StringUtils.repeat("y", 270);
        Customer customer = new Customer(invalidUserName, invalidName, invalidSurName, "asd", null);

        try {
            restTemplate.postForEntity(createURLWithPort(API_CUSTOMER_PATH), new HttpEntity<>(customer), ValidationResult.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());

            ValidationResult validationResult = mapper.readValue(e.getResponseBodyAsString(), ValidationResult.class);
            assertTrue(validationResult.hasErrors());
            assertEquals(customer.getClass().getSimpleName(), validationResult.getEntityName());
            assertEquals(4, validationResult.getErrors().size());
            assertEquals("size must be between 0 and 256", validationResult.getErrors().get("username"));
            assertEquals("size must be between 0 and 256", validationResult.getErrors().get("name"));
            assertEquals("size must be between 0 and 256", validationResult.getErrors().get("surname"));
            assertEquals("must be a well-formed email address", validationResult.getErrors().get("email"));
            throw e;
        }
    }

    @Test
    public void testUpdate() {
        Long testID = 1L;
        String testUserName = "mcass";
        String testName = "Cassie";
        String testSurName = "Mckenzie";
        String testEmail = "m.cass@company.com";

        ResponseEntity<CustomerDetails> customerResponse = restTemplate.getForEntity(
                createURLWithPortAndId(API_CUSTOMER_PATH, testID), CustomerDetails.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        CustomerDetails customerDetails = customerResponse.getBody();

        Assert.assertNotNull(customerDetails);

        Customer customer = new Customer();
        customer.setId(testID);
        customer.setUsername(testUserName);
        customer.setName(testName);
        customer.setSurname(testSurName);
        customer.setEmail(testEmail);

        ResponseEntity<ValidationResult> response = restTemplate.exchange(
                createURLWithPort(API_CUSTOMER_PATH),
                HttpMethod.PUT, new HttpEntity<>(customer), ValidationResult.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        URI resourceURL = response.getHeaders().getLocation();
        assertTrue(resourceURL.toString().contains(API_CUSTOMER_PATH));

        ResponseEntity<CustomerDetails> updatedResponse = restTemplate.getForEntity(resourceURL, CustomerDetails.class);
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
        CustomerDetails updatedCustomerDetails = updatedResponse.getBody();

        assertEquals(testUserName, updatedCustomerDetails.getUsername());
        assertEquals(testName, updatedCustomerDetails.getName());
        assertEquals(testSurName, updatedCustomerDetails.getSurname());
        assertEquals(testEmail, updatedCustomerDetails.getEmail());
        assertNotNull(updatedCustomerDetails.getCreatedBy());
        assertEquals(apiUser, updatedCustomerDetails.getModifiedBy());
    }

    @Test(expected = HttpClientErrorException.class)
    public void testUpdateValidationFail() throws IOException {
        Long testID = 1L;
        String invalidName = StringUtils.repeat("y", 270);
        Customer customer = new Customer("a", invalidName, "surname", "asd", null);
        customer.setId(testID);

        try {
            restTemplate.put(createURLWithPort(API_CUSTOMER_PATH), new HttpEntity<>(customer));
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());

            ValidationResult validationResult = mapper.readValue(e.getResponseBodyAsString(), ValidationResult.class);
            assertTrue(validationResult.hasErrors());
            assertEquals(customer.getClass().getSimpleName(), validationResult.getEntityName());
            assertEquals(2, validationResult.getErrors().size());
            assertEquals("size must be between 0 and 256", validationResult.getErrors().get("name"));
            assertEquals("must be a well-formed email address", validationResult.getErrors().get("email"));
            throw e;
        }
    }

    @Test
    public void testDelete() {
        Long testID = 3L;

        ResponseEntity<CustomerDetails> customerResponse = restTemplate.getForEntity(
                createURLWithPortAndId(API_CUSTOMER_PATH, testID), CustomerDetails.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        CustomerDetails customerDetails = customerResponse.getBody();
        Assert.assertNotNull(customerDetails);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                createURLWithPortAndId(API_CUSTOMER_PATH, testID),
                HttpMethod.DELETE, new HttpEntity<>(null), Void.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        ResponseEntity<CustomerDetails> getResponse = restTemplate.getForEntity(
                createURLWithPortAndId(API_CUSTOMER_PATH, testID), CustomerDetails.class);
        assertEquals(HttpStatus.NO_CONTENT, getResponse.getStatusCode());
    }

}
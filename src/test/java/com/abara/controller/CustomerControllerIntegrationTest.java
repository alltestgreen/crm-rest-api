package com.abara.controller;

import com.abara.common.AbstractIntegrationTest;
import com.abara.model.Customer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2RestOperations;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;

public class CustomerControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String API_CUSTOMER_CREATE = "/api/customer/create";
    private static final String API_CUSTOMER_DETAILS = "/api/customer/details/";
    private static final String API_CUSTOMER_LIST = "/api/customer/list";
    private static final String API_CUSTOMER_UPDATE = "/api/customer/update";
    private static final String API_CUSTOMER_DELETE = "/api/customer/delete/";

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpHeaders headers = new HttpHeaders();

    @Value("${oauth.user.username}")
    private String apiUser;

    @Autowired
    private OAuth2RestOperations restTemplate;

    @Test
    public void testRetrieveAllName() throws IOException {
        ResponseEntity<String> response = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_LIST), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<String> customers = mapper.readValue(response.getBody(), new TypeReference<List<String>>() {
        });

        Assert.assertTrue(customers.contains("John Smith"));
        Assert.assertTrue(customers.contains("Grace Clarkson"));
        Assert.assertTrue(customers.contains("Timothy Thompson"));
    }

    @Test
    public void testRetrieveById() {
        Long testID = 2L;
        ResponseEntity<Customer> response = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_DETAILS + testID), Customer.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Customer customer = response.getBody();
        Assert.assertNotNull(customer);

        assertNotNull(customer.getName());
        assertNotNull(customer.getSurname());
        assertNull(customer.getPhoto());
        assertNotNull(customer.getCreatedBy());
    }

    @Test
    public void testCreate() {
        String testName = "Cassie";
        String testSurName = "Mckenzie";
        String testPhoto = "photo";
        Customer newCustomer = new Customer(testName, testSurName, testPhoto);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                createURLWithPort(API_CUSTOMER_CREATE),
                new HttpEntity<>(newCustomer, headers), Void.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        URI resourceURL = response.getHeaders().getLocation();
        assertTrue(resourceURL.toString().contains(API_CUSTOMER_DETAILS));

        ResponseEntity<Customer> getResponse = restTemplate.getForEntity(resourceURL, Customer.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Customer createdCustomer = getResponse.getBody();
        assertNotNull(createdCustomer.getId());
        assertEquals(testName, createdCustomer.getName());
        assertEquals(testSurName, createdCustomer.getSurname());
        assertEquals(testPhoto, createdCustomer.getPhoto());
        assertNull(createdCustomer.getMofifiedBy());
        assertEquals(apiUser, createdCustomer.getCreatedBy());
    }

    @Test
    public void testUpdate() {
        Long testID = 1L;
        String testName = "Cassie";
        String testSurName = "Mckenzie";
        String testPhoto = "photo";

        ResponseEntity<Customer> customerResponse = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_DETAILS + testID), Customer.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        Customer customer = customerResponse.getBody();

        Assert.assertNotNull(customer);

        customer.setName(testName);
        customer.setSurname(testSurName);
        customer.setPhoto(testPhoto);

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort(API_CUSTOMER_UPDATE),
                HttpMethod.PUT, new HttpEntity<>(customer, headers), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<Customer> updatedResponse = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_DETAILS + testID), Customer.class);
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
        Customer updatedCustomer = updatedResponse.getBody();

        assertEquals(testName, updatedCustomer.getName());
        assertEquals(testSurName, updatedCustomer.getSurname());
        assertEquals(testPhoto, updatedCustomer.getPhoto());
        assertEquals(apiUser, updatedCustomer.getMofifiedBy());
    }

    @Test
    public void testDelete() {
        Long testID = 3L;

        ResponseEntity<Customer> customerResponse = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_DETAILS + testID), Customer.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        Customer customer = customerResponse.getBody();
        Assert.assertNotNull(customer);

        ResponseEntity<Void> deleteResponse = restTemplate.postForEntity(
                createURLWithPort(API_CUSTOMER_DELETE + customer.getId()),
                new HttpEntity<>(headers), Void.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        ResponseEntity<Customer> getResponse = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_DETAILS + customer.getId()), Customer.class);
        System.out.println(getResponse.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, getResponse.getStatusCode());
    }

}
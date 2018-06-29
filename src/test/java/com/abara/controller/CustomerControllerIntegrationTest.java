package com.abara.controller;

import com.abara.common.AbstractIntegrationTest;
import com.abara.entity.Customer;
import com.abara.entity.CustomerImage;
import com.abara.model.CustomerDetails;
import com.abara.validation.ValidationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static com.abara.controller.CustomerController.API_CUSTOMER_IMAGE_PATH;
import static com.abara.controller.CustomerController.API_CUSTOMER_PATH;
import static org.junit.Assert.*;

public class CustomerControllerIntegrationTest extends AbstractIntegrationTest {

    @Value("${oauth.username}")
    private String apiUser;

    @Autowired
    private OAuth2RestOperations restTemplate;

    @Autowired
    private ObjectMapper mapper;

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
        assertEquals(createURLWithPortAndId(API_CUSTOMER_IMAGE_PATH, testID), customerDetails.getImageURI().toString());
    }

    @Test
    public void testCreate() {
        String testName = "Cassie";
        String testSurName = "Mckenzie";
        CustomerImage testImage = new CustomerImage("name", "type", new byte[]{123});
        Customer newCustomer = new Customer(testName, testSurName, testImage);

        ResponseEntity<ValidationResult> response = restTemplate.postForEntity(
                createURLWithPort(API_CUSTOMER_PATH), new HttpEntity<>(newCustomer), ValidationResult.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        URI resourceURL = response.getHeaders().getLocation();
        assertTrue(resourceURL.toString().contains(API_CUSTOMER_PATH));

        ResponseEntity<CustomerDetails> getResponse = restTemplate.getForEntity(resourceURL, CustomerDetails.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        CustomerDetails customerDetails = getResponse.getBody();
        assertEquals(newCustomer.getName(), customerDetails.getName());
        assertEquals(newCustomer.getSurname(), customerDetails.getSurname());
        assertNotNull(customerDetails.getImageURI().toString());
        assertNull(customerDetails.getModifiedBy());
        assertEquals(apiUser, customerDetails.getCreatedBy());
    }

    @Test(expected = HttpClientErrorException.class)
    public void testCreateValidationFail() throws IOException {
        String invalidName = StringUtils.repeat("y", 270);
        String invalidSurName = StringUtils.repeat("y", 270);
        Customer customer = new Customer(invalidName, invalidSurName, null);

        try {
            restTemplate.postForEntity(createURLWithPort(API_CUSTOMER_PATH), new HttpEntity<>(customer), ValidationResult.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());

            ValidationResult validationResult = mapper.readValue(e.getResponseBodyAsString(), ValidationResult.class);
            assertTrue(validationResult.hasErrors());
            assertEquals(customer.getClass().getSimpleName(), validationResult.getEntityName());
            assertEquals(2, validationResult.getErrors().size());
            assertEquals("size must be between 0 and 256", validationResult.getErrors().get("name"));
            assertEquals("size must be between 0 and 256", validationResult.getErrors().get("surname"));
            throw e;
        }
    }

    @Test
    public void testUpdate() {
        Long testID = 1L;
        String testName = "Cassie";
        String testSurName = "Mckenzie";
        CustomerImage testImage = new CustomerImage("name", "type", new byte[]{123});

        ResponseEntity<CustomerDetails> customerResponse = restTemplate.getForEntity(
                createURLWithPortAndId(API_CUSTOMER_PATH, testID), CustomerDetails.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        CustomerDetails customerDetails = customerResponse.getBody();

        Assert.assertNotNull(customerDetails);

        Customer customer = new Customer();
        customer.setId(testID);
        customer.setName(testName);
        customer.setSurname(testSurName);
        customer.setImage(testImage);

        ResponseEntity<ValidationResult> response = restTemplate.exchange(
                createURLWithPort(API_CUSTOMER_PATH),
                HttpMethod.PUT, new HttpEntity<>(customer), ValidationResult.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        URI resourceURL = response.getHeaders().getLocation();
        assertTrue(resourceURL.toString().contains(API_CUSTOMER_PATH));

        ResponseEntity<CustomerDetails> updatedResponse = restTemplate.getForEntity(resourceURL, CustomerDetails.class);
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
        CustomerDetails updatedCustomerDetails = updatedResponse.getBody();

        assertEquals(testName, updatedCustomerDetails.getName());
        assertEquals(testSurName, updatedCustomerDetails.getSurname());
        assertNotNull(updatedCustomerDetails.getImageURI().toString());
        assertNotNull(updatedCustomerDetails.getCreatedBy());
        assertEquals(apiUser, updatedCustomerDetails.getModifiedBy());
    }

    @Test(expected = HttpClientErrorException.class)
    public void testUpdateValidationFail() throws IOException {
        Long testID = 1L;
        String invalidName = StringUtils.repeat("y", 270);
        Customer customer = new Customer(invalidName, "surname", null);
        customer.setId(testID);

        try {
            restTemplate.put(createURLWithPort(API_CUSTOMER_PATH), new HttpEntity<>(customer));
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());

            ValidationResult validationResult = mapper.readValue(e.getResponseBodyAsString(), ValidationResult.class);
            assertTrue(validationResult.hasErrors());
            assertEquals(customer.getClass().getSimpleName(), validationResult.getEntityName());
            assertEquals(1, validationResult.getErrors().size());
            assertEquals("size must be between 0 and 256", validationResult.getErrors().get("name"));
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

    @Test
    public void testImageUpload() {
        Long testID = 1L;
        String fileParamName = "file";
        FileSystemResource fileSystemResource = new FileSystemResource("src/test/resources/images/red-dot.png");

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add(fileParamName, fileSystemResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<ValidationResult> uploadResponse = restTemplate.postForEntity(
                createURLWithPortAndId(API_CUSTOMER_IMAGE_PATH, testID), new HttpEntity<>(map, headers), ValidationResult.class);
        assertEquals(HttpStatus.CREATED, uploadResponse.getStatusCode());

        ResponseEntity<CustomerDetails> customerResponse = restTemplate.getForEntity(
                createURLWithPortAndId(API_CUSTOMER_PATH, testID), CustomerDetails.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        CustomerDetails customerDetails = customerResponse.getBody();
        Assert.assertNotNull(customerDetails);
        Assert.assertTrue(customerDetails.getImageURI().toString().contains(API_CUSTOMER_IMAGE_PATH));
    }

    @Test
    public void testImageUploadRetrieval() throws IOException {
        Long testID = 1L;
        FileSystemResource fileSystemResource = new FileSystemResource("src/test/resources/images/red-dot.png");

        ResponseEntity<byte[]> response = restTemplate.getForEntity(
                createURLWithPortAndId(API_CUSTOMER_IMAGE_PATH, testID), byte[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        byte[] imageBytes = response.getBody();
        Assert.assertEquals(fileSystemResource.contentLength(), imageBytes.length);
    }

    @Test
    public void testImageUploadDeletion() {
        Long testID = 1L;

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPortAndId(API_CUSTOMER_IMAGE_PATH, testID),
                HttpMethod.DELETE, new HttpEntity<>(null), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<CustomerDetails> customerResponse = restTemplate.getForEntity(
                createURLWithPortAndId(API_CUSTOMER_PATH, testID), CustomerDetails.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        CustomerDetails customerDetails = customerResponse.getBody();
        Assert.assertNotNull(customerDetails);
        Assert.assertNull(customerDetails.getImageURI());
    }

}
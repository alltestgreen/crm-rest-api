package com.abara.controller;

import com.abara.common.AbstractIntegrationTest;
import com.abara.entity.Customer;
import com.abara.entity.CustomerImage;
import com.abara.model.CustomerDetails;
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

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static org.junit.Assert.*;

public class CustomerControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String API_CUSTOMER_CREATE = "/api/customer/create";
    private static final String API_CUSTOMER_DETAILS = "/api/customer/details/";
    private static final String API_CUSTOMER_LIST = "/api/customer/list";
    private static final String API_CUSTOMER_UPDATE = "/api/customer/update";
    private static final String API_CUSTOMER_DELETE = "/api/customer/delete/";
    private static final String API_CUSTOMER_IMAGE = "/api/customer/image/";
    private static final String API_CUSTOMER_IMAGE_UPLOAD = "api/customer/image/upload/";
    private static final String API_CUSTOMER_IMAGE_DELETE = "api/customer/image/delete/";

    @Value("${oauth.username}")
    private String apiUser;

    @Autowired
    private OAuth2RestOperations restTemplate;

    @Test
    public void testRetrieveAll() {
        ParameterizedTypeReference<Map<Long, String>> responseType = new ParameterizedTypeReference<Map<Long, String>>() {
        };
        ResponseEntity<Map<Long, String>> response = restTemplate.exchange(
                createURLWithPort(API_CUSTOMER_LIST), HttpMethod.GET, new HttpEntity<>(null), responseType);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<Long, String> customerMap = response.getBody();

        Assert.assertEquals("John Smith", customerMap.get(1L));
        Assert.assertEquals("Grace Clayson", customerMap.get(2L));
    }

    @Test
    public void testRetrieveById() {
        Long testID = 2L;
        ResponseEntity<CustomerDetails> response = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_DETAILS + testID), CustomerDetails.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        CustomerDetails customerDetails = response.getBody();
        Assert.assertNotNull(customerDetails);

        assertEquals("Grace", customerDetails.getName());
        assertEquals("Clayson", customerDetails.getSurname());
        assertEquals("http://localhost:" + port + API_CUSTOMER_IMAGE + testID, customerDetails.getImageURL());
    }

    @Test
    public void testCreate() {
        String testName = "Cassie";
        String testSurName = "Mckenzie";
        CustomerImage testImage = new CustomerImage("name", "type", new byte[]{123});
        Customer newCustomer = new Customer(testName, testSurName, testImage);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                createURLWithPort(API_CUSTOMER_CREATE),
                new HttpEntity<>(newCustomer), Void.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        URI resourceURL = response.getHeaders().getLocation();
        assertTrue(resourceURL.toString().contains(API_CUSTOMER_DETAILS));

        ResponseEntity<CustomerDetails> getResponse = restTemplate.getForEntity(resourceURL, CustomerDetails.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        CustomerDetails customerDetails = getResponse.getBody();
        assertEquals(newCustomer.getName(), customerDetails.getName());
        assertEquals(newCustomer.getSurname(), customerDetails.getSurname());
        assertNotNull(customerDetails.getImageURL());
        assertNull(customerDetails.getModifiedBy());
        assertEquals(apiUser, customerDetails.getCreatedBy());
    }

    @Test
    public void testUpdate() {
        Long testID = 1L;
        String testName = "Cassie";
        String testSurName = "Mckenzie";
        CustomerImage testImage = new CustomerImage("name", "type", new byte[]{123});

        ResponseEntity<CustomerDetails> customerResponse = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_DETAILS + testID), CustomerDetails.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        CustomerDetails customerDetails = customerResponse.getBody();

        Assert.assertNotNull(customerDetails);

        Customer customer = new Customer();
        customer.setId(testID);
        customer.setName(testName);
        customer.setSurname(testSurName);
        customer.setImage(testImage);

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort(API_CUSTOMER_UPDATE),
                HttpMethod.PUT, new HttpEntity<>(customer), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        URI resourceURL = response.getHeaders().getLocation();
        assertTrue(resourceURL.toString().contains(API_CUSTOMER_DETAILS));

        ResponseEntity<CustomerDetails> updatedResponse = restTemplate.getForEntity(resourceURL, CustomerDetails.class);
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
        CustomerDetails updatedCustomerDetails = updatedResponse.getBody();

        assertEquals(testName, updatedCustomerDetails.getName());
        assertEquals(testSurName, updatedCustomerDetails.getSurname());
        assertNotNull(updatedCustomerDetails.getImageURL());
        assertNotNull(updatedCustomerDetails.getCreatedBy());
        assertEquals(apiUser, updatedCustomerDetails.getModifiedBy());
    }

    @Test
    public void testDelete() {
        Long testID = 3L;

        ResponseEntity<CustomerDetails> customerResponse = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_DETAILS + testID), CustomerDetails.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        CustomerDetails customerDetails = customerResponse.getBody();
        Assert.assertNotNull(customerDetails);

        ResponseEntity<Void> deleteResponse = restTemplate.postForEntity(
                createURLWithPort(API_CUSTOMER_DELETE + testID),
                new HttpEntity<>(null), Void.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        ResponseEntity<CustomerDetails> getResponse = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_DETAILS + testID), CustomerDetails.class);
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

        ResponseEntity<Void> uploadResponse = restTemplate.postForEntity(
                createURLWithPort(API_CUSTOMER_IMAGE_UPLOAD + testID), new HttpEntity<>(map, headers), Void.class);
        assertEquals(HttpStatus.CREATED, uploadResponse.getStatusCode());

        ResponseEntity<CustomerDetails> customerResponse = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_DETAILS + testID), CustomerDetails.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        CustomerDetails customerDetails = customerResponse.getBody();
        Assert.assertNotNull(customerDetails);
        Assert.assertTrue(customerDetails.getImageURL().contains(API_CUSTOMER_IMAGE));
    }

    @Test
    public void testImageUploadRetrieval() throws IOException {
        Long testID = 1L;
        FileSystemResource fileSystemResource = new FileSystemResource("src/test/resources/images/red-dot.png");

        ResponseEntity<byte[]> response = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_IMAGE + testID), byte[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        byte[] imageBytes = response.getBody();
        Assert.assertEquals(fileSystemResource.contentLength(), imageBytes.length);
    }

    @Test
    public void testImageUploadDeletion() {
        Long testID = 1L;

        ResponseEntity<Void> response = restTemplate.postForEntity(
                createURLWithPort(API_CUSTOMER_IMAGE_DELETE + testID), new HttpEntity<>(null), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<CustomerDetails> customerResponse = restTemplate.getForEntity(
                createURLWithPort(API_CUSTOMER_DETAILS + testID), CustomerDetails.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        CustomerDetails customerDetails = customerResponse.getBody();
        Assert.assertNotNull(customerDetails);
        Assert.assertNull(customerDetails.getImageURL());
    }

}
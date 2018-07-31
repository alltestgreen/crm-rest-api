package com.abara.controller;

import com.abara.common.AbstractIntegrationTest;
import com.abara.model.CustomerDetails;
import com.abara.validation.ValidationResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.abara.controller.CustomerController.API_CUSTOMER_IMAGE_PATH;
import static com.abara.controller.CustomerController.API_CUSTOMER_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CustomerImageIntegrationTest extends AbstractIntegrationTest {

    @Before
    public void setUp() {
        restTemplate = buildRestTemplate();
    }

    @Test
    public void testImageUploadThenRetrieval() {
        Long testID = 1L;

        // Upload
        FileSystemResource fileSystemResource = new FileSystemResource("src/test/resources/images/red-dot.png");
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", fileSystemResource);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        ResponseEntity<ValidationResult> uploadResponse = restTemplate.postForEntity(
                createURLWithPortAndId(API_CUSTOMER_IMAGE_PATH, testID), new HttpEntity<>(map, headers), ValidationResult.class);
        assertEquals(HttpStatus.CREATED, uploadResponse.getStatusCode());

        // Get
        ResponseEntity<byte[]> response = restTemplate.getForEntity(
                createURLWithPortAndId(API_CUSTOMER_IMAGE_PATH, testID), byte[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        byte[] customerImage = response.getBody();
        assertNotNull(customerImage);
        assertEquals(85, customerImage.length);
    }

    @Test
    public void testImageUploadThenDeletion() {
        Long testID = 1L;

        // Upload
        FileSystemResource fileSystemResource = new FileSystemResource("src/test/resources/images/red-dot.png");
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", fileSystemResource);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        ResponseEntity<ValidationResult> uploadResponse = restTemplate.postForEntity(
                createURLWithPortAndId(API_CUSTOMER_IMAGE_PATH, testID), new HttpEntity<>(map, headers), ValidationResult.class);
        assertEquals(HttpStatus.CREATED, uploadResponse.getStatusCode());

        // Delete
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

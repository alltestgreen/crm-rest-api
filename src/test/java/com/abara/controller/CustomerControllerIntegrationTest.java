package com.abara.controller;

import com.abara.DemoApplication;
import com.abara.model.Customer;
import com.abara.service.CustomerService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private HttpHeaders headers = new HttpHeaders();
    private TestRestTemplate restTemplate = new TestRestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCreateCustomer() {
        Customer customer = new Customer("Test", "Customer", null);

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort("/api/customer/create"),
                HttpMethod.POST, new HttpEntity<>(customer, headers), Void.class);

        assertTrue(response.getHeaders().get(HttpHeaders.LOCATION).get(0).contains("/api/customer/details/"));
    }

    @Test
    public void testRetrieveAllCustomer() throws IOException {
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/api/customer/list"),
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        List<String> customers = mapper.readValue(response.getBody(), new TypeReference<List<String>>() {
        });

        Assert.assertTrue(customers.contains("John Smith"));
        Assert.assertTrue(customers.contains("Grace Clarkson"));
        Assert.assertTrue(customers.contains("Timothy Thompson"));
    }

    @Test
    public void testRetrieveCustomerById() {
        Long testID = 2L;
        ResponseEntity<Customer> response = restTemplate.exchange(
                createURLWithPort("/api/customer/details/" + testID),
                HttpMethod.GET, new HttpEntity<>(headers), Customer.class);

        Customer customer = response.getBody();
        assertEquals("Grace", customer.getName());
        assertEquals("Clarkson", customer.getSurname());
        assertEquals(null, customer.getPhoto());
    }

    @Test
    public void testUpdate() {
        Long testID = 1L;
        ResponseEntity<Customer> customerResponse = restTemplate.exchange(
                createURLWithPort("/api/customer/details/" + testID),
                HttpMethod.GET, new HttpEntity<>(headers), Customer.class);
        Customer customer = customerResponse.getBody();

        Assert.assertNotNull(customer);

        customer.setName("Cassie");
        customer.setSurname("Mckenzie");

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort("/api/customer/update/"),
                HttpMethod.PUT, new HttpEntity<>(customer, headers), Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<Customer> updatedResponse = restTemplate.exchange(
                createURLWithPort("/api/customer/details/" + testID),
                HttpMethod.GET, new HttpEntity<>(headers), Customer.class);

        assertEquals(customer, updatedResponse.getBody());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}

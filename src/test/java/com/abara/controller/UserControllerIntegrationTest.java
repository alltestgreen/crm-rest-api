package com.abara.controller;

import com.abara.common.AbstractIntegrationTest;
import com.abara.model.Role;
import com.abara.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2RestOperations;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class UserControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String API_USER_CREATE = "/api/user/create";
    private static final String API_USER_DETAILS = "/api/user/details/";
    private static final String API_USER_LIST = "/api/user/list";
    private static final String API_USER_UPDATE = "/api/user/update";
    private static final String API_USER_DELETE = "/api/user/delete/";

    private final HttpHeaders headers = new HttpHeaders();

    @Autowired
    private OAuth2RestOperations restTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRetrieveAll() {
        ParameterizedTypeReference<List<User>> responseType = new ParameterizedTypeReference<List<User>>() {
        };
        ResponseEntity<List<User>> response = restTemplate.exchange(
                createURLWithPort(API_USER_LIST), HttpMethod.GET, new HttpEntity<>(headers), responseType);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<User> users = response.getBody();

        Assert.assertEquals(2, users.size());
        Assert.assertEquals("admin", users.get(0).getUsername());
    }

    @Test
    public void testRetrieveUserById() {
        Long testID = 1L;
        ResponseEntity<User> response = restTemplate.getForEntity(
                createURLWithPort(API_USER_DETAILS + testID), User.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        User user = response.getBody();
        Assert.assertNotNull(user);

        assertNotNull(user.getUsername());
        assertNotNull(user.getPassword());
        assertFalse(user.getRoles().isEmpty());
    }

    @Test
    public void testCreate() {
        String testUserName = "Cassie";
        String testPassword = "P@ssw0rd";
        Role testRole = new Role("USER");
        User newUser = new User(testUserName, testPassword, Collections.singleton(testRole));

        ResponseEntity<Void> response = restTemplate.postForEntity(
                createURLWithPort(API_USER_CREATE),
                new HttpEntity<>(newUser, headers), Void.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        URI resourceURL = response.getHeaders().getLocation();
        assertTrue(resourceURL.toString().contains(API_USER_DETAILS));

        ResponseEntity<User> getResponse = restTemplate.getForEntity(resourceURL, User.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        User createdUser = getResponse.getBody();
        assertNotNull(createdUser.getId());
        assertEquals(testUserName, createdUser.getUsername());
        assertTrue(passwordEncoder.matches(testPassword, createdUser.getPassword()));
        assertEquals(Collections.singleton(testRole), createdUser.getRoles());
    }

    @Test
    public void testUpdate() {
        Long testID = 3L;
        String testUsername = "James";
        Role adminRole = new Role("ADMIN");

        ResponseEntity<User> customerResponse = restTemplate.getForEntity(
                createURLWithPort(API_USER_DETAILS + testID), User.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        User user = customerResponse.getBody();

        Assert.assertNotNull(user);

        user.setUsername(testUsername);
        Set<Role> currentRoles = user.getRoles();
        currentRoles.add(adminRole);
        user.setRoles(currentRoles);

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort(API_USER_UPDATE),
                HttpMethod.PUT, new HttpEntity<>(user, headers), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<User> updatedResponse = restTemplate.getForEntity(
                createURLWithPort(API_USER_DETAILS + testID), User.class);
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
        User updatedUser = updatedResponse.getBody();

        assertEquals(testUsername, updatedUser.getUsername());
        assertEquals(currentRoles, updatedUser.getRoles());
    }

    @Test
    public void testDelete() {
        Long testID = 2L;

        ResponseEntity<User> customerResponse = restTemplate.getForEntity(
                createURLWithPort(API_USER_DETAILS + testID), User.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        User user = customerResponse.getBody();
        Assert.assertNotNull(user);

        ResponseEntity<Void> deleteResponse = restTemplate.postForEntity(
                createURLWithPort(API_USER_DELETE + user.getId()),
                new HttpEntity<>(headers), Void.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        ResponseEntity<User> getResponse = restTemplate.getForEntity(
                createURLWithPort(API_USER_DETAILS + user.getId()), User.class);
        System.out.println(getResponse.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, getResponse.getStatusCode());
    }

    @Test
    public void imageUpload() {
        Long testID = 1L;
        String image = "iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==";

        ResponseEntity<Void> uploadResponse = restTemplate.postForEntity(
                createURLWithPort("api/user/uploadImage/" + testID),
                new HttpEntity<>(image, headers), Void.class);
        assertEquals(HttpStatus.OK, uploadResponse.getStatusCode());
    }
}
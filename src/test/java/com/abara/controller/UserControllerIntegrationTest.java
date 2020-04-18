package com.abara.controller;

import com.abara.common.AbstractIntegrationTest;
import com.abara.entity.Role;
import com.abara.entity.User;
import com.abara.model.UserDetails;
import com.abara.validation.ValidationResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.abara.controller.UserController.API_USER_PATH;
import static org.junit.Assert.*;

public class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        restTemplate = buildRestTemplate();
    }

    @Test
    public void testRetrieveAll() {
        ResponseEntity<List<UserDetails>> response = restTemplate.exchange(
                createURLWithPort(API_USER_PATH), HttpMethod.GET, new HttpEntity<>(null),
                new ParameterizedTypeReference<List<UserDetails>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<UserDetails> users = response.getBody();

        Assert.assertEquals(2, users.size());
        Assert.assertEquals("admin", users.get(0).getUsername());
    }

    @Test
    public void testRetrieveUserById() {
        Long testID = 1L;
        ResponseEntity<UserDetails> response = restTemplate.getForEntity(
                createURLWithPortAndId(API_USER_PATH, testID), UserDetails.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserDetails userDetails = response.getBody();
        Assert.assertNotNull(userDetails);

        assertEquals(testID, userDetails.getId());
        assertEquals("admin", userDetails.getUsername());
        assertTrue(userDetails.getRoles().contains(new Role("USER")));
        assertTrue(userDetails.getRoles().contains(new Role("ADMIN")));
    }

    @Test
    public void testRetrieveNonExistingUserById() {
        Long testID = 99L;
        ResponseEntity<UserDetails> response = restTemplate.getForEntity(
                createURLWithPortAndId(API_USER_PATH, testID), UserDetails.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testCreate() {
        String testUserName = "Cassie";
        String testPassword = "P@ssw0rd";
        Role testRole1 = new Role("USER");
        Role testRole2 = new Role("ADMIN");
        Set<Role> roles = Stream.of(testRole1, testRole2).collect(Collectors.toSet());
        User newUser = new User(testUserName, testPassword, roles);

        ResponseEntity<ValidationResult> response = restTemplate.postForEntity(
                createURLWithPort(API_USER_PATH),
                new HttpEntity<>(newUser), ValidationResult.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        URI resourceURL = response.getHeaders().getLocation();
        assertTrue(resourceURL.toString().contains(API_USER_PATH));

        ResponseEntity<UserDetails> getResponse = restTemplate.getForEntity(resourceURL, UserDetails.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        UserDetails createdUser = getResponse.getBody();
        assertNotNull(createdUser.getId());
        assertEquals(testUserName, createdUser.getUsername());
        assertEquals(roles, createdUser.getRoles());
    }

    @Test(expected = HttpClientErrorException.class)
    public void testCreateValidationFail() throws IOException {
        String invalidName = StringUtils.repeat("y", 270);
        User user = new User(invalidName, "testPassword", Collections.emptySet());

        try {
            restTemplate.postForEntity(createURLWithPort(API_USER_PATH),
                    new HttpEntity<>(user), ValidationResult.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());

            ValidationResult validationResult = mapper.readValue(e.getResponseBodyAsString(), ValidationResult.class);
            assertTrue(validationResult.hasErrors());
            assertEquals(user.getClass().getSimpleName(), validationResult.getEntityName());
            assertEquals(1, validationResult.getErrors().size());
            assertEquals("size must be between 0 and 256", validationResult.getErrors().get("username"));
            throw e;
        }
    }

    @Test
    public void testUpdate() {
        Long testID = 3L;
        String testUsername = "James";
        String testPassword = "pass";
        Role adminRole = new Role("ADMIN");

        ResponseEntity<UserDetails> customerResponse = restTemplate.getForEntity(
                createURLWithPortAndId(API_USER_PATH, testID), UserDetails.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        UserDetails userDetails = customerResponse.getBody();

        Assert.assertNotNull(userDetails);

        User user = new User();
        user.setId(userDetails.getId());
        user.setUsername(testUsername);
        user.setPassword(testPassword);
        Set<Role> userRoles = userDetails.getRoles();
        userRoles.add(adminRole);
        user.setRoles(userRoles);

        ResponseEntity<ValidationResult> response = restTemplate.exchange(
                createURLWithPort(API_USER_PATH),
                HttpMethod.PUT, new HttpEntity<>(user), ValidationResult.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        URI resourceURL = response.getHeaders().getLocation();
        assertTrue(resourceURL.toString().contains(API_USER_PATH));

        ResponseEntity<UserDetails> updatedResponse = restTemplate.getForEntity(resourceURL, UserDetails.class);
        assertEquals(HttpStatus.OK, updatedResponse.getStatusCode());
        UserDetails updatedUserDetails = updatedResponse.getBody();

        assertEquals(testUsername, updatedUserDetails.getUsername());
        assertEquals(userRoles, updatedUserDetails.getRoles());
    }

    @Test(expected = HttpClientErrorException.class)
    public void testUpdateValidationFail() throws IOException {
        Long testID = 2L;
        String invalidName = StringUtils.repeat("y", 270);
        User user = new User(invalidName, "testPassword", Collections.emptySet());
        user.setId(testID);

        try {
            restTemplate.put(createURLWithPort(API_USER_PATH), new HttpEntity<>(user));
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());

            ValidationResult validationResult = mapper.readValue(e.getResponseBodyAsString(), ValidationResult.class);
            assertTrue(validationResult.hasErrors());
            assertEquals(user.getClass().getSimpleName(), validationResult.getEntityName());
            assertEquals(1, validationResult.getErrors().size());
            assertEquals("size must be between 0 and 256", validationResult.getErrors().get("username"));
            throw e;
        }
    }

    @Test
    public void testDelete() {
        Long testID = 2L;

        ResponseEntity<UserDetails> customerResponse = restTemplate.getForEntity(
                createURLWithPortAndId(API_USER_PATH, testID), UserDetails.class);
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        UserDetails userDetails = customerResponse.getBody();
        Assert.assertNotNull(userDetails);

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                createURLWithPortAndId(API_USER_PATH, testID),
                HttpMethod.DELETE, new HttpEntity<>(null), Void.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        ResponseEntity<User> getResponse = restTemplate.getForEntity(
                createURLWithPortAndId(API_USER_PATH, userDetails.getId()), User.class);
        assertEquals(HttpStatus.NO_CONTENT, getResponse.getStatusCode());
    }

}
package com.abara.config;

import com.abara.model.Customer;
import com.abara.model.Role;
import com.abara.model.User;
import com.abara.service.CustomerService;
import com.abara.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@Configuration
public class TestContextConfiguration {

    @Value("${oauth.trusted-client-id}")
    private String clientId;

    @Value("${oauth.trusted-client-secret}")
    private String secret;

    @Value("${oauth.grant-type}")
    private String grantType;

    @Value("${oauth.user.username}")
    private String apiUser;

    @Value("${oauth.user.password}")
    private String apiUserPassword;

    @Value("${oauth.admin.username}")
    private String adminUsername;

    @Value("${server.port}")
    private int port;

    @Bean
    protected OAuth2RestOperations restTemplate() {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();

        resource.setAccessTokenUri("http://localhost:" + port + "/oauth/token");
        resource.setClientId(clientId);
        resource.setClientSecret(secret);
        resource.setGrantType(grantType);
        resource.setScope(asList("read", "write"));

        resource.setUsername(apiUser);
        resource.setPassword(apiUserPassword);

        return new OAuth2RestTemplate(resource);
    }

    @Bean
    CommandLineRunner runner(CustomerService customerService, UserService userService) {
        return args -> {
            Customer customer1 = new Customer("John", "Smith", null);
            Customer customer2 = new Customer("Grace", "Clarkson", null);
            Customer customer3 = new Customer("Timothy", "Thompson", null);
            customer1.setCreatedBy(adminUsername);
            customer2.setCreatedBy(adminUsername);
            customer3.setCreatedBy(adminUsername);

            customerService.save(customer1);
            customerService.save(customer2);
            customerService.save(customer3);

            userService.save(new User("admin", "admin", Stream.of(new Role("USER"), new Role("ADMIN")).collect(Collectors.toSet())));
            userService.save(new User("user", "user", Collections.singleton(new Role("USER"))));
        };
    }

}
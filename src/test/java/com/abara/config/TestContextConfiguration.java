package com.abara.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;

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

}
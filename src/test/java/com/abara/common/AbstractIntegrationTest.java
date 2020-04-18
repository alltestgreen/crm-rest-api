package com.abara.common;

import com.abara.DemoApplication;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.Arrays.asList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestPropertySource("/application-test.properties")
public abstract class AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Value("${security.oauth.client.id}")
    private String clientId;

    @Value("${security.oauth.client.secret}")
    private String secret;

    @Value("${api.username}")
    private String apiUser;

    @Value("${api.password}")
    private String apiUserPassword;

    protected OAuth2RestOperations restTemplate;

    protected OAuth2RestOperations buildRestTemplate() {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();

        resource.setAccessTokenUri("http://localhost:" + port + "/oauth/token");
        resource.setClientId(clientId);
        resource.setClientSecret(secret);
        resource.setGrantType("password");
        resource.setScope(asList("read", "write"));

        resource.setUsername(apiUser);
        resource.setPassword(apiUserPassword);

        return new OAuth2RestTemplate(resource);
    }

    protected String createURLWithPort(String uri) {
        return String.format("http://localhost:%d%s", port, uri);
    }

    protected String createURLWithPortAndId(String uri, Long id) {
        return String.format("http://localhost:%d%s/%d", port, uri, id);
    }

}

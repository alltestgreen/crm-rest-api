package com.abara.common;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import static org.junit.Assert.*;

public class OAuth2AccessTokenTest extends AbstractIntegrationTest {

    private OAuth2RestOperations restTemplate;

    @Before
    public void setUp() {
        restTemplate = buildRestTemplate();
    }

    @Test
    public void getAccessTokenByGrantedPassword() {
        OAuth2AccessToken accessToken = restTemplate.getAccessToken();

        assertEquals("bearer", accessToken.getTokenType());
        assertTrue(accessToken.getScope().contains("read"));
        assertTrue(accessToken.getScope().contains("write"));
        assertNotNull(accessToken.getValue());
        assertNotNull(accessToken.getExpiration());
    }
}

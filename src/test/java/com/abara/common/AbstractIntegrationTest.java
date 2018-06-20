package com.abara.common;

import com.abara.DemoApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public abstract class AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    protected String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}

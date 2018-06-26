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
    protected int port;

    protected String createURLWithPort(String uri) {
        return String.format("http://localhost:%d%s", port, uri);
    }

    protected String createURLWithPortAndId(String uri, Long id) {
        return String.format("http://localhost:%d%s/%d", port, uri, id);
    }

}

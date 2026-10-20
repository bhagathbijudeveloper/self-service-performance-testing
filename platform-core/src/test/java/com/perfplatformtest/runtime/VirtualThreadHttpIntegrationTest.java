package com.perfplatformtest.runtime;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/** Uses real embedded HTTP, since MockMvc executes on the test thread rather than Tomcat's executor. */
@SpringBootTest(classes = VirtualThreadHttpIntegrationTest.ProbeApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VirtualThreadHttpIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Test
    void packagedDefaultsDispatchHttpRequestsOnVirtualThreads() throws Exception {
        ThreadProbe probe = probe(port);
        assertThat(probe).isNotNull();
        assertThat(probe.javaFeature()).isEqualTo(25);
        assertThat(probe.virtual()).isTrue();
    }

    @Test
    void openApiGenerationRemainsCompatibleWithTheUpgradedSpringRuntime() throws Exception {
        var response = get(port, "/api-docs");
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"openapi\"", "\"/thread-probe\"");
    }

    static ThreadProbe probe(int port) throws Exception {
        var response = get(port, "/thread-probe");
        assertThat(response.statusCode()).isEqualTo(200);
        return new ObjectMapper().readValue(response.body(), ThreadProbe.class);
    }

    private static HttpResponse<String> get(int port, String path) throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            return client.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                    .timeout(Duration.ofSeconds(10)).GET().build(), HttpResponse.BodyHandlers.ofString());
        }
    }

    // Outside com.perfplatform so the real application's component scan cannot pick up this fixture.
    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class,
            SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class,
            ServletWebSecurityAutoConfiguration.class,
            ManagementWebSecurityAutoConfiguration.class})
    @Import(ProbeController.class)
    static class ProbeApplication { }

    @RestController
    static class ProbeController {
        @GetMapping("/thread-probe")
        ThreadProbe probe() {
            return new ThreadProbe(Thread.currentThread().isVirtual(), Runtime.version().feature());
        }
    }

    record ThreadProbe(boolean virtual, int javaFeature) { }
}

/** Operators can disable virtual threads without changing the image or the application code. */
@SpringBootTest(classes = VirtualThreadHttpIntegrationTest.ProbeApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.threads.virtual.enabled=false")
class PlatformThreadHttpIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Test
    void explicitOptOutDispatchesHttpRequestsOnPlatformThreads() throws Exception {
        var probe = VirtualThreadHttpIntegrationTest.probe(port);
        assertThat(probe).isNotNull();
        assertThat(probe.virtual()).isFalse();
    }
}

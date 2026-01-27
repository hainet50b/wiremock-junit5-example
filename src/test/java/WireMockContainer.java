import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;

public class WireMockContainer extends GenericContainer<WireMockContainer> {

    private static final int PORT = 8080;

    @SuppressWarnings("resource")
    public WireMockContainer(String classpathResourcePath) {
        super("wiremock/wiremock:latest");
        withExposedPorts(PORT);
        withClasspathResourceMapping(
                classpathResourcePath,
                "/home/wiremock",
                BindMode.READ_ONLY
        );
        waitingFor(Wait
                .forHttp("/__admin/health")
                .withMethod("GET")
                .forStatusCode(200)
                .forPort(PORT)
        );
        withStartupTimeout(Duration.ofSeconds(30));
    }

    public String getBaseUrl() {
        return String.format("http://%s:%d", getHost(), getMappedPort(PORT));
    }
}

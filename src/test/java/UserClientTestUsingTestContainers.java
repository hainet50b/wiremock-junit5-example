import com.github.tomakehurst.wiremock.client.HttpAdminClient;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
@Testcontainers
class UserClientTestUsingTestContainers {

    @Container
    static WireMockContainer container = new WireMockContainer("wiremock/users");

    @BeforeEach
    void resetStubs() {
        HttpAdminClient client = new HttpAdminClient(container.getHost(), container.getMappedPort(8080));
        client.resetToDefaultMappings();
    }

    @Test
    void findAllTest() {
        UserClient sut = new UserClientImpl(container.getBaseUrl());

        Map<Integer, User> users = sut.findAll().stream().collect(
                Collectors.toMap(User::id, Function.identity())
        );

        assertAll(
                () -> assertEquals(2, users.size()),
                () -> assertEquals(new User(1, "hainet50b"), users.get(1)),
                () -> assertEquals(new User(2, "programacho.com"), users.get(2))
        );
    }

    @Test
    void findAllTest_clientError() {
        HttpAdminClient client = new HttpAdminClient(container.getHost(), container.getMappedPort(8080));

        StubMapping stubMapping = client.getStubMapping(UUID.fromString("6f33f78c-da9d-4599-8621-3887e02ec1df")).getItem();
        stubMapping.setResponse(aResponse()
                .withStatus(400)
                .build()
        );
        client.editStubMapping(stubMapping);

        UserClient sut = new UserClientImpl(container.getBaseUrl());

        assertThrows(
                HttpClientErrorException.class,
                sut::findAll
        );
    }

    @Test
    void findAllTest_serverError() {
        HttpAdminClient client = new HttpAdminClient(container.getHost(), container.getMappedPort(8080));

        StubMapping stubMapping = client.getStubMapping(UUID.fromString("6f33f78c-da9d-4599-8621-3887e02ec1df")).getItem();
        stubMapping.setResponse(aResponse()
                .withStatus(500)
                .build()
        );
        client.editStubMapping(stubMapping);

        UserClient sut = new UserClientImpl(container.getBaseUrl());

        assertThrows(
                HttpServerErrorException.class,
                sut::findAll
        );
    }
}

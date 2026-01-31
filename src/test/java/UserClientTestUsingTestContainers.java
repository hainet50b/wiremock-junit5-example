import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class UserClientTestUsingTestContainers {

    @Container
    static WireMockContainer container = new WireMockContainer("wiremock/users");

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
}

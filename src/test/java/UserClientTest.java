import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest
class UserClientTest {

    @Test
    void findAllTest(WireMockRuntimeInfo wmRuntimeInfo) {
        WireMock wireMock = wmRuntimeInfo.getWireMock();
        // @formatter:off
        wireMock.register(get("/users").willReturn(okJson("""
                [
                  {
                    "id": 1,
                    "name": "hainet50b"
                  },
                  {
                    "id": 2,
                    "name": "programacho.com"
                  }
                ]
                """
        )));
        // @formatter:on

        UserClient sut = new UserClientImpl(wmRuntimeInfo.getHttpBaseUrl());

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

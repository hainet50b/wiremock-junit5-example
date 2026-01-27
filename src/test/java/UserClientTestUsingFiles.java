import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserClientTestUsingFiles {

    @RegisterExtension
    static WireMockExtension wmExtension = WireMockExtension.newInstance()
            .options(wireMockConfig()
                    .dynamicPort()
            )
            .build();

    @Test
    void findAllTest() {
        WireMockRuntimeInfo wmRuntimeInfo = wmExtension.getRuntimeInfo();

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

    @RegisterExtension
    static WireMockExtension wmExtensionUsingSpecificFiles = WireMockExtension.newInstance()
            .options(wireMockConfig()
                    .dynamicPort()
                    .usingFilesUnderClasspath("wiremock/users")
            )
            .build();

    @Test
    void findAllTestUsingSpecificFiles() {
        WireMockRuntimeInfo wmRuntimeInfo = wmExtensionUsingSpecificFiles.getRuntimeInfo();

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

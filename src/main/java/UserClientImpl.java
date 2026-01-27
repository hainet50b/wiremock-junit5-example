import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class UserClientImpl implements UserClient {

    private String baseUrl;

    public UserClientImpl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public List<User> findAll() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(baseUrl + "/users"))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(
                    response.body(),
                    new TypeReference<>() {
                    }
            );
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}

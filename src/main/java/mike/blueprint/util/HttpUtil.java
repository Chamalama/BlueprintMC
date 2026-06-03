package mike.blueprint.util;

import mike.blueprint.config.Json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpUtil {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static <T> void sendData(String url, T data) {
        final HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(Json.getJSON().toJson(data)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.thoughtworks.gauge.Step;

import org.json.JSONArray;

public class StepImplementation {

    @Step("There is a pet named <pet> available in the pet store")
    public void verifyPetIsAvailable(String petName) {
        JSONArray availablePets = requestAvailablePets();
        Assertions.assertThat(availablePets).containsPetNamed(petName);
    }

    private static JSONArray requestAvailablePets() {
        return getJSONArrayResponse(getAvailablePetsRequest());
    }

    private static HttpRequest getAvailablePetsRequest() {
        String url = String.format("%s/pet/findByStatus?status=available", getOpenAPIHost());
        return HttpRequest.newBuilder().header("Accept", "application/json").
            header("Authorization", "Bearer YOUR_ACCESS_TOKEN").uri(URI.create(url)).build();
    }

    private static JSONArray getJSONArrayResponse(HttpRequest request) {
        HttpResponse<String> rawResponse = send(request);
        return new JSONArray(rawResponse.body());
    }

    private static HttpResponse<String> send(HttpRequest request) {
        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            verifySuccessfulResponse(response);
            return response;
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Exception when sending request", e);
        }
    }

    private static void verifySuccessfulResponse(HttpResponse<String> response) {
        if (response.statusCode() > 299) {
            String message = String.format("Expected 2xx response but got %1$s. Response body: %2$s",
                    response.statusCode(), response.body());
            throw new IllegalStateException(message);
        }
    }

    private static String getOpenAPIHost() {
        return System.getenv("OPENAPI_HOST");
    }

}

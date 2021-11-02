import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import com.thoughtworks.gauge.Step;

import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.api.PetApi;
import org.openapitools.client.model.Pet;

public class StepImplementation {

    @Step("There is a pet named <pet> available in the pet store")
    public void verifyPetIsAvailable(String petName) throws ApiException {
        assertThat(availablePets()).extracting("name").contains(petName);
    }

    public List<Pet> availablePets() throws ApiException {
        String available = Pet.StatusEnum.AVAILABLE.getValue();
        return petApi().findPetsByStatus(Arrays.asList(available));
    }

    public PetApi petApi() {
        return new PetApi(apiClient());
    }

    public ApiClient apiClient() {
        ApiClient client = Configuration.getDefaultApiClient();
        String openApiHost = System.getenv("OPENAPI_HOST");
        if (openApiHost != null) {
            System.out.println("Setting server URL to: " + openApiHost);
            client.setBasePath(openApiHost);
        }
        client.setAccessToken("YOUR_ACCESS_TOKEN");
        return client;
    }

}

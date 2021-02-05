from getgauge.python import step
import openapi_client
from openapi_client.api import pet_api
import os


@step("There is a pet named <pet_name> available in the pet store")
def there_is_an_available_pet_named(pet_name):
    with openapi_client.ApiClient(configuration()) as api_client:
        api_instance = pet_api.PetApi(api_client)
        available_pets = api_instance.find_pets_by_status(["available"])
        print(available_pets)
        assert any(pet.name == pet_name for pet in available_pets)


def configuration():
    openapi_host = os.environ.get("OPENAPI_HOST")
    if openapi_host is None:
        configuration = openapi_client.Configuration()
    else:
        configuration = openapi_client.Configuration(host=openapi_host)

    configuration.access_token = "YOUR_ACCESS_TOKEN"
    return configuration

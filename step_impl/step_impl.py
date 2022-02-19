"""Gauge step implementations."""
import os
import requests

from getgauge.python import step


@step("There is a pet named <pet_name> available in the pet store")
def there_is_an_available_pet_named(pet_name):
    """Checks if there is a pet with given name in the pet store"""
    url = f"{os.environ.get('OPENAPI_HOST')}/pet/findByStatus?status=available"
    available_pets = get_json(url)
    assert any(pet["name"] == pet_name for pet in available_pets)


def get_json(url):
    """Makes a HTTP JSON request and returns the JSON response"""
    headers = {
        "Accept": "application/json",
        "Authorization": "Bearer YOUR_ACCESS_TOKEN",
    }
    return requests.get(url, headers=headers).json()

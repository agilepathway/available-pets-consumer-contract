# java-gauge-openapi-example

[![Gauge Badge](https://gauge.org/Gauge_Badge.svg)](https://gauge.org) 
[![Gauge](https://github.com/agilepathway/java-gauge-openapi-example/workflows/Gauge%20specs/badge.svg)](https://github.com/agilepathway/java-gauge-openapi-example/actions?query=workflow%3A%22Gauge+specs%22+branch%3Amaster)
[![reviewdog](https://github.com/agilepathway/java-gauge-openapi-example/workflows/reviewdog/badge.svg)](https://github.com/agilepathway/java-gauge-openapi-example/actions?query=workflow%3Areviewdog+event%3Apush+branch%3Amaster)
[![License](https://img.shields.io/github/license/agilepathway/java-gauge-openapi-example?color=blue)](LICENSE)
[<img src="https://github.com/agilepathway/gauge-openapi-example/wiki/images/openapi.png" width="87">](./openapi.yaml)[![OpenAPI Validator](https://validator.swagger.io/validator?url=https://raw.githubusercontent.com/agilepathway/java-gauge-openapi-example/master/openapi.yaml)](./openapi.yaml)

Example in Java of how [Gauge](https://gauge.org/) and [OpenAPI](https://www.openapis.org/about) play nicely
together to produce [living documentation](https://www.infoq.com/articles/book-review-living-documentation/) for APIs.

NB There is also a separate [Python example repository](https://github.com/agilepathway/gauge-openapi-example),
demonstrating the same workflow but using Python as the test implementation language instead of Java.

___
* [Example workflow](#example-workflow)
* [Benefits of this approach](#benefits-of-this-approach)
* [Running the spec](#running-the-spec)
  * [Prerequisites](#prerequisites)
  * [Run the spec against the mock server](#run-the-spec-against-the-mock-server)
  * [Run the spec against the real server](#run-the-spec-against-the-real-server)
  * [Run the spec using Prism as a validation proxy against the real server](#run-the-spec-using-prism-as-a-validation-proxy-against-the-real-server)
* [Notes](#notes)
___

## Example workflow

1. Have a collaborative story refinement session to come up with 
   [specification examples](https://gojko.net/2008/11/04/specifying-with-examples/), using 
   [example mapping](https://cucumber.io/blog/bdd/example-mapping-introduction/) for instance

2. [Write up the specification examples in Gauge](https://docs.gauge.org/writing-specifications.html).

   Using the example from this repo, we'd have the following Gauge spec written at this point:

   ```
    # Pet store availability

    ## Customers can see which pets are available in the pet store

    * There is a pet named "doggie" available in the pet store
   ```

   We don't write the underlying implementation for this Gauge spec yet, that will come below.

3. Now, let's say that implementing this feature requires a new [REST API microservice](https://microservices.io/patterns/microservices.html).

   Create an OpenAPI specification to describe our new API, e.g. the [`openapi.yaml`](./openapi.yaml) in this repo.

   (The OpenAPI specification file can be YAML or JSON)
   
4. Even though we don't have an implementation for our OpenAPI spec yet, we already have all we need to go ahead and implement the Gauge spec.
   
   1. Write the implementation code for the Gauge spec in Java by treating the OpenAPI spec as a black-box. Here is an example (with just some of the code):
   
   ```java
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
        String url = "https://petstore.swagger.io/v2/pet/findByStatus?status=available";
        return HttpRequest.newBuilder().header("Accept", "application/json").uri(URI.create(url)).build();
    }
   ```
    
5. If we ran the Gauge spec now it would fail, because there is no implementation of the OpenAPI spec for the Java Client SDK to communicate with. Enter Prism.

   [Prism](https://stoplight.io/prism) is a mock server that effortlessly serves example
   responses based on an OpenAPI spec.
   1. [Install Prism](https://meta.stoplight.io/docs/prism/docs/getting-started/01-installation.md)
   3. Setup a Gauge [environment variable](https://docs.gauge.org/configuration.html#using-environments-in-a-gauge-project) to point our Gauge spec implementation at the Prism
   mock server that we just started:

      Create an `env/mock/openapi.properties` file and also an
      `env/validation-proxy/openapi.properties` file, both with this content: 
      
      `OPENAPI_HOST = http://127.0.0.1:4010`

6. Now we can run our Gauge spec against our mock environment, and it will pass :-)
   - `prism mock openapi.yaml`
   - `gauge run --env mock specs`

7. We can now go ahead and implement the API, based on our OpenAPI spec of course.

   When we have done so, we can run our Gauge spec against it too, without any modification:

   - `gauge run specs`

   Even better, we can [use Prism as a validation proxy against the real server](https://meta.stoplight.io/docs/prism/docs/guides/03-validation-proxy.md), which verifies
   that the implementation is fully compliant with the OpenAPI spec:

   - `prism proxy openapi.yaml https://petstore.swagger.io/v2`
   - `gauge run --env validation-proxy specs`

## Benefits of this approach

1. Collaborative - consumers, solution architects, developers, testers, analysts, Product Owner all have a natural interest in being involved.  This is a great silo breaker.
2. [Shift Left](https://devops.com/devops-shift-left-avoid-failure/) - enables testing of APIs before implementation has started
3. Speeds up and improves service consumer and provider integration: provides API consumers with a working mocked example of the API that they can integrate straightaway.
4. [Design-first APIs](https://tyk.io/moving-api-design-first-agile-world/)
   - [Development teams can work in parallel](https://swagger.io/resources/articles/adopting-an-api-first-approach/#development-teams-can-work-in-parallel--3)
   - [Reduces the cost of developing apps](https://swagger.io/resources/articles/adopting-an-api-first-approach/#reduces-the-cost-of-developing-apps-4)
   - [Increases the speed to market](https://swagger.io/resources/articles/adopting-an-api-first-approach/#increases-the-speed-to-market-5)
   - [Ensures good developer experiences](https://swagger.io/resources/articles/adopting-an-api-first-approach/#ensures-good-developer-experiences-6)
   - [Reduces the risk of failure](https://swagger.io/resources/articles/adopting-an-api-first-approach/#reduces-the-risk-of-failure-7)
5. [Specification by Example](https://gojko.net/2008/11/04/specifying-with-examples/)
   - Shared understanding between all parties
   - [Living documentation](https://www.infoq.com/articles/book-review-living-documentation/), providing a single source of truth. This API documentation stays up to date because it is executable, and is only written in one place (rather than analysts, developers and testers all writing their own separate documentation.)
6. API [black box testing](https://resources.whitesourcesoftware.com/blog-whitesource/black-box-testing)
   - provides great test coverage
   - decoupled from implementation, so does not get in the way of implementation
7. [Consumer-Driven-Contract-Testing](https://meta.stoplight.io/docs/prism/docs/guides/03-validation-proxy.md#end-to-end-contract-testing)
   - allows service design to be driven by the verified needs of consumers
   - [ensures that consumer test stubs stay in sync with the implementation](https://meta.stoplight.io/docs/prism/docs/guides/03-validation-proxy.md#assisting-api-consumer-integration)
8. Enables different languages to be used easily - can choose Python for the client SDK and Java for the server implementation, for instance

## Running the spec
### Prerequisites
- [Install OpenAPI Generator](https://openapi-generator.tech/docs/installation)
- Generate the Java client SDK code:

  `openapi-generator-cli generate -i openapi.yaml -g java -o ./java-client-generated`

- Install the generated Java client SDK code:

  `cd java-client-generated/ && mvn clean package && cd ../`

   In `env/default/java.properties`, set `gauge_additional_libs` as follows:

   `gauge_additional_libs = libs/*,java-client-generated/target/*,java-client-generated/target/lib/*`

- [Install Prism](https://meta.stoplight.io/docs/prism/docs/getting-started/01-installation.md)
  
### Run the spec against the mock server
- `prism mock openapi.yaml`
- `gauge run --env mock specs`

### Run the spec against the real server
- `gauge run specs`

### Run the spec using Prism as a validation proxy against the real server
- `prism proxy openapi.yaml https://petstore.swagger.io/v2`
- `gauge run --env validation-proxy specs`


## Notes

- This example uses [Gauge](https://gauge.org/), but other natural-language specification tools 
(e.g. [Cucumber](https://cucumber.io/), [SpecFlow](https://specflow.org/) etc) would be fine too.

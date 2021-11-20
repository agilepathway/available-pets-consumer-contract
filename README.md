# java-openapi-provider

[![Gauge Badge](https://gauge.org/Gauge_Badge.svg)](https://gauge.org) 
[![Gauge](https://github.com/agilepathway/java-gauge-openapi-example/workflows/Gauge%20specs/badge.svg)](https://github.com/agilepathway/java-gauge-openapi-example/actions?query=workflow%3A%22Gauge+specs%22+branch%3Amaster)
[![reviewdog](https://github.com/agilepathway/java-gauge-openapi-example/workflows/reviewdog/badge.svg)](https://github.com/agilepathway/java-gauge-openapi-example/actions?query=workflow%3Areviewdog+event%3Apush+branch%3Amaster)
[![License](https://img.shields.io/github/license/agilepathway/java-gauge-openapi-example?color=blue)](LICENSE)
[<img src="https://github.com/agilepathway/gauge-openapi-example/wiki/images/openapi.png" width="87">](./openapi.yaml)[![OpenAPI Validator](https://validator.swagger.io/validator?url=https://raw.githubusercontent.com/agilepathway/java-gauge-openapi-example/master/openapi.yaml)](./openapi.yaml)

An example provider application in Java to demonstrate 
[consumer-driven contract testing](https://www.martinfowler.com/articles/consumerDrivenContracts.html) in action, using
[Specification by Example](https://gojko.net/2008/11/04/specifying-with-examples/) with [Gauge](https://gauge.org/),
along with [OpenAPI](https://www.openapis.org/about) and [Prism](https://stoplight.io/prism).

See also the companion [consumer web app](https://github.com/agilepathway/available-pets-consumer) to this provider.

NB There is also a separate [Python example repository](https://github.com/agilepathway/gauge-openapi-example),
demonstrating the same workflow but using Python as the test implementation language instead of Java.

___
* [Example workflow](#example-workflow)
* [Benefits of this approach](#benefits-of-this-approach)
* [Running the specs](#running-the-specs)
  * [Prerequisites](#prerequisites)
  * [Run the spec against the mock server](#run-the-spec-against-the-mock-server)
  * [Run the spec against the real server](#run-the-spec-against-the-real-server)
  * [Run the spec using Prism as a validation proxy against the real server](#run-the-spec-using-prism-as-a-validation-proxy-against-the-real-server)
* [Notes](#notes)
___

## Example workflow

Read the
[README in the consumer app](https://github.com/agilepathway/available-pets-consumer#workflow-for-consumer-driven-changes-to-the-provider-api)
first, as this gives the all-important consumer-driven perspective.  Then come back here and read the description below
of the workflow from the provider perspective.

1. After using Specification by Example to drive their specification of a new feature (the ability to show which pets
   are newly in the petstore, in our notional example), the consumer creates a feature branch in our provider repo and
   modifies the [OpenAPI spec](./openapi.yaml) with their proposed change, i.e. adding `new` to the list of defined 
   statuses:

   ```
   enum:
      - available
      - pending
      - sold
      - new
   ```

2. The consumer also adds a specification on our provider repo, e.g.

   ```markdown
   ## Customers can see which pets are new in the pet store

   * There is a pet named "doggie" new in the pet store
   ```

   Note that this spec is identical to the spec which the consumer also created in their consumer repo.  This is a good 
   thing as it describes the same consistent API contract in both the consumer and provider (it's not a disaster if the
   specs have slightly different wording due to step implementation differences, but it's a good goal to keep them the
   same or as close to the same as possible).

   If the consumer team have the capability to add the step implementation for this spec in our provider repo, they 
   should go ahead and do so on the same feature branch.  The step implementation on the provider side is a 
   black-box API test using Prism, so implementing it does not require any knowledge of the internals of the provider
   application.  This is a nice instance of using
   [innersource](https://resources.github.com/whitepapers/introduction-to-innersource/) principles.  If not, then it's
   also fine for the implementation to be left to the provider.

   Have a look at [the `new-pets` branch](https://github.com/agilepathway/java-openapi-provider/tree/new-pets) 
   and you can see these changes added by the consumer in the most recent commits there.

   Let's look more closely at how Prism is helping us here.
   [Prism](https://stoplight.io/prism) is a mock server that effortlessly serves example responses based just on our
   OpenAPI spec. All it needs is the OpenAPI spec, nothing more, nothing less.  That's very powerful.

   Our CI/CD pipeline uses Prism to run our Gauge specs on every push to every branch.  The tests run a couple of
   times in different modes:
   - mock mode: Prism is used as a mock server, i.e. our Gauge specs run against Prism with no interaction with the
     real service
   - [validation proxy mode](https://meta.stoplight.io/docs/prism/docs/guides/03-validation-proxy.md): Prism proxies
     through to the real server and errors if there are any mismatches between the OpenAPI spec and what the real
     server accepts and responds with.  This is incredibly powerful as it ensures that there is zero discrepancy
     between the OpenAPI spec and the real service, in terms of the API contract expressed through our consumer-driven
     Gauge specs.
   
   If we 
   [look at the CI/CD pipeline for our `new-pets` branch](https://github.com/agilepathway/java-openapi-provider/runs/4272344093?check_suite_focus=true)
   then we will see that the tests pass in mock mode and fail in validation proxy mode.  This is entirely expected and a good thing, as it indicates that we the provider need to go ahead and amend the microservice to make the failing test pass.  This is real consumer-driven development in action.

3. So the provider can now go ahead and make the necessary changes to their microservice. 


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

## Running the specs
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

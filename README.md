# Products store
Sample ReST microservices back-end for a grocery e-commerce

## Tech stack
- Spring Boot web for ReST endpoints exposure
- Spring Boot security for authentication
- Spring Boot data-jpa and [H2 in-memory SQL database](#access-the-data-storage) for data access and storage
- Spring Boot test for unit testing
- [Gradle multi-project build system](#gradle-multi-project-build-system)
- Docker-compose deployment

## Requirements
- Docker(tested with Docker `20.10.12` and Docker-compose `1.29.2`)

### Optional
- JRE(tested with JRE `11`) if you want to test, build and run services directly on your system with the Gradle wrapper `gradlew[.bat]`
- Python `>=3.7`(with bundled `pip`) to use `servicegen` template folder to [generate a new service source project](#generate-a-new-service-source-project)


## Operations
In the following subsections `<SERVICE>` is a placeholder for one of `review` or `product`.
For all commands the working directory is the one where this repository has been checkout.

### Test a service
#### Gradle
##### Unit tests
```sh
./gradlew :<SERVICE>service:test
```

Unit tests are behaviour tests implemented as Spock `Specification`s

#### e2e tests
```sh
# Build Docker image to run e2e tests
docker image build -t e2etests -f e2eTests/Dockerfile .

# Run Docker container from built image to execute e2e tests
docker run --rm -t --name e2etester --privileged e2etests
```
e2e tests are implemented in the following way:
- [`e2eTests/Dockerfile`][e2eTestsDockerfile] defines a Docker image with
  - JDK `11`
  - Docker and Docker-compose 
  - This repository
  - `e2eTests/e2e-tests.sh` as entrypoint
- [`e2eTests/e2e-tests.sh`][e2eTestsScript]:
  - deploys product and review microservices with Docker-compose as described 
in section [Deploy all services](#deploy-all-services)
  - Runs `./gradlew e2eTest`
  - tears down the microservices with Docker-compose as as described
    in section [Deploy all services](#deploy-all-services)
- `e2eTest` Gradle task is a `Test` task running test classes in [`e2eTests/src/main/groovy`][e2eTestsClasses]
- One test class is defined in `e2eTests/src/main/groovy`: `MicroServicesSpec`. It is a Spock `Specification` with a 
feature calling product service aggregation endpoint through Spring web `RestTemplate`

#### Swagger(*OpenAPI*)
Swagger HTML documentation of `<SERVICE>` is exposed at `/`. You can browse it to manually try out the service ReST endpoints.
In the case of `review` service, it exposes write endpoints(`POST`, `DELETE`) protected by authentication through Spring [`HttpSecurity`][HttpSecurity].
Thus you need to first login at `/login` with username `lee` and password `rock`(*This credentials are an intentional reference to a pretty famous manga...*)
in order to try them.
> **Note**
> Of course putting username and password to access data writing endpoints in a README.md of a public Git repository is an awful security practice.
> This is done since these are **purely demo purposes endpoints**, accessing an in-memory database with sample data. 
> However, to demo good security practices, the default password generated dynamically and logged at start up by Spring Boot security is replaced by a static encrypted one,
> as suggested in `withDefaultPasswordEncoder()` method deprecation note of class `org.springframework.security.core.userdetails.User`.


### Build a service
#### Gradle
```sh
./gradlew :<SERVICE>service:bootJar
```

#### Docker
```sh
docker image build --build-arg service=<SERVICE> -t <SERVICE>service .
```

### Run a service
#### Gradle
You can run a previously built `jar`(`<VERSION>` is the value of `version` property in [`<SERVICE>service/build.gradle`][serviceBuildGradle]):

```sh
java -jar <SERVICE>service-<VERSION>.jar
```

or build and run a service in one shot:

```sh
./gradlew :<SERVICE>service:bootRun
```

#### Docker
```sh
docker container run --rm -t --name <SERVICE>service -p <PORT>:<PORT> <SERVICE>service
```

`<PORT>` to listen to is configured for `<SERVICE>` in its [application properties][applicationProperties].

### Deploy all services
#### Docker-compose
```sh
# Build services Docker images and create containers from them
docker-compose up --build --no-start

# Start services containers
docker-compose start

# See logs of a service
docker-compose logs <SERVICE>

# Build and start services in one command
docker-compose up --build

# Stop and remove services containers and resources
docker-compose down
```

### Generate a new service source project
This operation uses the awesome [cookiecutter][cookiecutter-repo] Python 3 open source command-line utility for template based directory tree generation:  
```sh
# Install cookiecutter pip package
pip install --user cookiecutter
# Generate new service project structure
cookiecutter servicegen/
```

cookiecutter will prompt you for [some informations][cookiecutter-data] about the new service. You can directly press enter at any prompt to accept the defaults.
The newly generated service is a minimal Spring Boot Web application listening on port `8080` and exposing a `/hello` `GET` ReST endpoint with a unit test for it.
The Swagger(*Open API*) HTML documentation of the service is exposed at `/`.
You can use the same Gradle and Docker [operations described above](#test-a-service) to test, build and run the service.

## Access the data storage
The data storage is an in-memory H2 SQL database. It is deployed and used by Spring Boot data-jpa by default if `com.h2database:h2` dependency is in the classpath. 
Moreover it comes with H2 management web console enabled and available at `/h2-console` to navigate the database schema and run SQL scripts. 
Integration with H2 is a very handy way Spring Boot data-jpa provides for test and demo purposes. Since it is an in-memory database it is not a persistent storage:
it is recreated every time the Spring Boot application is started and initial data can be set with a [SQL script][seedSQLScript].
The database coordinates are defined in Spring Boot [application properties][applicationProperties].
> **Note**
> H2 management web console is blocked by Spring security by default. In order to be browsable it requires *cross site request forgery* to be disabled.
> 
> The H2 web console has its own login page which sends a `POST` request to `/h2-console/login.do`. 
> In the case of an H2 instance deployed within a Spring Boot application, the `/h2-console/login.do` login processing endpoint is protected by Spring security 
> `SecurityFilterChain` and the `POST` request to it is rejected (`403` http status is retrieved) since it doesn't come from a Spring Boot page but 
> from one provided by the H2 jar(Spring Boot security `X-` headers are not sent), even though it is hosted by the same embedded servlet container of the Spring Boot application.
> 
> More over `X-Frame-Options` Spring Boot response header must be disabled to allow H2 web console to be rendered within an HTML `<frameset>`.
> [This Spring Boot security tweaks][securityTweaks] are of course intended for a demo setup and not for a production one.

## Gradle multi-project build system
The microservices defined in this repository are built through a single Gradle multi-project build system. 

The **goal I wanted to achieve** with such build system **was to not have to repeat for every microservice the dependencies and build tasks configurations that are common** 
to all microservices(for example `org.springdoc:springdoc-openapi-groovy` for Swagger(*OpenAPI*) documentation generation or `useJUnitPlatform()` test task configuration for test classes discovery).

Here is how it is implemented:
- [`settings.gradle`][settingsGradle] at repository root includes every subdirectory with a name ending with `service` as a buildable project
- Each `*service` Gradle project is defined in [`*service/build.gradle`][serviceBuildGradle] and applies the [`spring-boot-service`][gradlePlugin] 
'in-line' Gradle plugin which defines common dependencies and tasks configuration and in turns applies the `org.springframework.boot` Gradle plugin.
- This way every `*service` Gradle project has the `bootJar` task defined to build a self-contained Spring Boot `jar` including an embedded servlet container for web exposure.

**This build system allows also to build a Docker image for each microservice using one `Dockerfile`**.

The [`Dockerfile`][Dockerfile] just needs to expose a build `ARG` to know which is the name of the microservice to build and delegate the build to `:<NAME>service:bootJar` Gradle task.
A second build stage defined in the `Dockerfile` copies the `jar` into a new image layer to not include source code in the final image built and set the entrypoint to run a container to `java -jar *.jar`.
This way the Docker image build is agnostic of the version of the microservice(the `jar`) to be included, which is instead defined only by the microservice `build.gradle`
> **Note**
> The term 'in-line' Gradle plugin is a term of mine. 
> 
> In Gradle terminology it is called *precompiled script plugin* because it is a Gradle Plugin
> (a piece of software using Gradle API to bundle build configuration to be shared) defined through a script and built 
> just before the projects of the Gradle build defining it are built(so that the plugin can be used to build the projects).
> It differs from a standalone Gradle Plugin in the sense that it is defined within another Gradle build instead of its own, but once built it is
> the same as a standalone Gradle plugin. 
> 
> Its typical usage is to dry build configuration within a build comprising multiple projects(a *multi-project build* in Gradle terminology).
> This is why I like to think about it as an 'in-line' or 'embedded' Gradle plugin. 
> It is the perfect fit to dry in a self-contained manner common build configuration across microservices defined in the same repository.


## Further notes
The product service aggregation `GET` endpoint consumes a live ReST API which is not the suggested `https://www.adidas.co.uk/api/products/{productId}`.
This because it answers `403` http status code to `org.springframework.web.client.RestTemplate` instance used to consume it.
I tried it out with `curl https://www.adidas.co.uk/api/products/M20324 -i` and I received back `301` or `403` http status codes as well.
With the browser instead the API retrieves the expected product data in `application/json` MIME type.
The request from the browser includes a `cookie` header so Adidas live API requires session management with cookies.
I tried to implement session management with cookies within product service by writing an implementation of `org.springframework.http.client.ClientHttpRequestInterceptor`
that records first response headers and add them to next requests but it didn't work, `403` was still returned. The session management implementation attempt is in branch `feature/product/aggregate-adidas-live-api`.

The live ReST API consumed by product service in `master` is instead `https://tienda.mercadona.es/api/categories/{id}` which does not require session management. 
It uses numeric IDs instead of alphanumeric ones. The response `Content-Type` is `application/json`, it is localised in Spanish and describes a category of products
like `Agua y refrescos` along with the related products. For the purposes of demoing the product service aggregation endpoint only the `name` top field of this Mercadona API is aggregated.
Examples of products categories IDs are `12`, `13`, `15`, `18`.

Thus by sending a `GET` request to `/product/13` the following response body is retrieved:
````json
{
  "id": 18,
  "name": "Agua y refrescos",
  "averageReviewScore": 10.0,
  "numberOfReviews": 5000
}
````

[e2eTestsDockerfile]: https://github.com/rikZerac/product-store-micro/blob/master/e2eTests/Dockerfile
[e2eTestsScript]: https://github.com/rikZerac/product-store-micro/blob/master/e2eTests/e2e-tests.sh
[e2eTestsClasses]: https://github.com/rikZerac/product-store-micro/blob/master/e2eTests//src/main/groovy/dev/riccardo/productsstore/MicroServicesSpec.groovy
[HttpSecurity]: https://github.com/rikZerac/product-store-micro/blob/master/reviewservice/src/main/groovy/dev/riccardo/productsstore/HttpSecurityConfiguration.groovy#L23
[passwordEncrypt]: https://github.com/rikZerac/product-store-micro/blob/master/reviewservice/src/main/groovy//dev/riccardo/productsstore/HttpSecurityConfiguration.groovy#L40
[applicationProperties]: https://github.com/rikZerac/product-store-micro/blob/master/reviewservice/src/main/resources/application.yaml
[cookiecutter-repo]: https://github.com/cookiecutter/cookiecutter
[cookiecutter-data]: https://github.com/rikZerac/product-store-micro/blob/master/servicegen/cookiecutter.json
[seedSQLScript]: https://github.com/rikZerac/product-store-micro/blob/master/reviewservice/src/main/resources/data.sql
[securityTweaks]: https://github.com/rikZerac/product-store-micro/blob/master/reviewservice/src/main/groovy//dev/riccardo/productsstore/HttpSecurityConfiguration.groovy#L28
[settingsGradle]: https://github.com/rikZerac/product-store-micro/blob/master/settings.gradle
[serviceBuildGradle]:  https://github.com/rikZerac/product-store-micro/blob/master/reviewservice/build.gradle
[gradlePlugin]:  https://github.com/rikZerac/product-store-micro/blob/master/buildSrc/src/main/groovy/spring-boot-service.gradle
[Dockerfile]: https://github.com/rikZerac/product-store-micro/blob/master/Dockerfile

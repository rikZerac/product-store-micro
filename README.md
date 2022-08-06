# Products store
Sample ReST microservices back-end for a grocery e-commerce

## Tech stack
- Spring Boot web for ReST endpoints exposure
- Spring Boot security for authentication
- Spring Boot data-jpa and H2 in-memory SQL database for data access and storage
- Spring Boot test for unit testing
- Gradle multi project build system
- Docker-compose deployment

## Requirements
- Docker(tested with Docker `20.10.12` and Docker-compose `1.29.2`)

### Optional
- JRE(tested with JRE `11`) if you want to test, build and run services directly on your system with the Gradle wrapper `gradlew[.bat]`
- Python `>=3.7`(with bundled `pip`) to use `servicegen` template folder to [generate a new service source project](#Generate a new service source project)


## Operations
In the following subsections `<SERVICE>` is a placeholder for one of `review` or `product`.
For all commands the working directory is the one where this repository has been checkout.

### Test a service
#### Gradle
##### Unit test
```sh
./gradlew :<SERVICE>service:test
```

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
You can run a previously built `jar`(`<VERSION>` is the value of `version` property in [`buildSrc/src/main/groovy/spring-boot-service.gradle`][version]):

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

`<PORT>` to listen to is configured for `<SERVICE>` in its [application properties][application properties].

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
The newly generated service is a minimal Spring Boot Web application listening on port `8080` and exposing a '/hello' `GET` ReST endpoint with a unit test for it.
The Swagger(*Open API*) HTML documentation of the service is exposed at `/`.
You can use the same Gradle and Docker [operations described above](#Test a service) to test, build and run the service.
 





[HttpSecurity]: https://github.com/rikZerac/product-store-micro/blob/master/reviewservice/src/main/groovy/HttpSecurityConfiguration.groovy#L24
[passwordEncrypt]: https://github.com/rikZerac/product-store-micro/blob/master/reviewservice/src/main/groovy/HttpSecurityConfiguration.groovy#L41
[version]: https://github.com/rikZerac/product-store-micro/blob/master/buildSrc/src/main/groovy/spring-boot-service.gradle#L8
[application properties]: https://github.com/rikZerac/product-store-micro/blob/master/reviewservice/src/main/groovy/resources/application.yaml#L2
[cookiecutter-repo]: https://github.com/cookiecutter/cookiecutter
[cookiecutter-data]: https://github.com/rikZerac/product-store-micro/blob/master/servicegen/cookiecutter.json

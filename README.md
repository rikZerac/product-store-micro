# Products store
Sample microservices back-end for a web store

## Tech stack
- Spring Boot web for ReST endpoints exposure
- Spring Boot data-jpa and H2 in-memory SQL datatbase for data access and storage
- Gradle multi project build system
- Docker-compose deployment

## Requirements
- Docker(tested with Docker `20.10.12` and Docker-compose `1.29.2`)

### Optional
- JRE(tested with JRE `11`) if you want to build and run services locally with the Gradle wrapper `gradlew[.bat]`


## Operations
In the following subsections `<SERVICE>` is a placeholder for one of `review` or `product`.
For all commands the working directory is the one where this repository has been checkout.

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
docker container run --rm -t --name <SERVICE>service -p 80:80 -p 8080:8080 <SERVICE>service
```

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


[version]: https://github.com/rikZerac/product-store-micro/blob/master/buildSrc/src/main/groovy/spring-boot-service.gradle#L8

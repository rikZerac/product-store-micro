## In a nutshell
Sample microservices BE with Spring Boot, Gradle multi project build system and Docker distribution.

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
You can run a `jar` previously built:

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

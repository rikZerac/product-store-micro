FROM amazoncorretto:11-alpine3.15 AS builder
WORKDIR build
COPY ./ ./
ARG service
RUN ./gradlew :${service}service:clean :${service}service:bootJar

FROM amazoncorretto:11-alpine3.15
WORKDIR services
ARG service
COPY --from=builder ./build/${service}service/build/libs/${service}service-*.jar ./
EXPOSE 80
EXPOSE 8080
ENTRYPOINT ["/bin/sh", "-c", "java -jar *.jar"]

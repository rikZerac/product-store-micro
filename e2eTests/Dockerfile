FROM amazoncorretto:11-alpine3.15
WORKDIR e2e

# Install Docker
RUN apk update &&\
 apk add docker docker-compose

#Run e2e tests
COPY ./ ./
ENTRYPOINT ["/bin/sh", "-c", "dockerd 1>dockerd.out 2>dockerd.err & ./e2eTests/e2e-tests.sh"]

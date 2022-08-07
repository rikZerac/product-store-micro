set -x
docker-compose up --build --no-start
docker-compose start
./gradlew e2eTest
docker-compose down

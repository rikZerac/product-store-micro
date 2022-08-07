set -x
docker-compose up --build --no-start
docker-compose start
./gradlew clean e2eTest
cp -r e2eTests/build/reports/tests reports
docker-compose down

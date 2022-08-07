# tested only on MacOS 11
echo "=== START e2e TESTS ==="
echo ""
for service in $(ls -A1 | grep service | grep -v servicegen); do
  if test -d "${service}"; then
    echo "Start service ${service}"
    ./gradlew :${service}:bootRun 1>${service}.out 2>&1 & echo $! > ${service}.pid
  fi
done
echo ""

echo "Wait for services to be up"
sleep 15

echo "Running tests"
echo ""
./gradlew e2eTest
open -a Google\ Chrome e2eTests/build/reports/tests/e2eTest/index.html
echo ""

echo "Are you done with testing?"
read
echo ""

echo "Killing services"
pkill -f gradle
for service in $(ls -A1 | grep service | grep -v servicegen); do
  if test -d "${service}"; then
    rm ${service}.pid
    rm ${service}.out
  fi
done
echo ""
echo "=== END e2e TESTS ==="


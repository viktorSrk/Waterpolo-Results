#!/usr/bin/env bash

./gradlew build

JAR_FILE=$(ls -t server/build/libs/server-*.jar | grep -v -e '-plain.jar' | head -n 1)

java -jar $JAR_FILE
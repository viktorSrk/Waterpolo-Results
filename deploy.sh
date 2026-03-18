#!/bin/bash

git pull origin prod

./gradlew :commons:build :server:build -x test
java -jar "server/build/libs/server-0.0.1-SNAPSHOT.jar"

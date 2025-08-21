#!/bin/bash

# Load and export env variables from .env file
set -o allexport
source ./.env
set +o allexport

# Run Spring Boot
./mvnw -f pom.xml spring-boot:run -Dspring-boot.run.profiles=local

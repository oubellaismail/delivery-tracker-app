#!/bin/bash

set -e

echo "Stopping any running containers..."
docker compose -f docker/docker-compose.yml --env-file .env down

echo "Building and starting containers..."
docker compose -f docker/docker-compose.yml --env-file .env up --build

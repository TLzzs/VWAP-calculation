#!/bin/bash

echo "Creating Kafka topics from Avro schema files..."

for file in ../src/main/resources/avro/*.avsc; do
  [ -e "$file" ] || continue

  echo "Processing file: $file"
  base=$(basename "$file")
  topic=${base%%.*}  # removes file extension
  echo "Creating topic: ${topic}.test"

  docker exec kafka kafka-topics \
    --create \
    --if-not-exists \
    --topic "${topic}.test" \
    --bootstrap-server localhost:9092 \
    --partitions 1 \
    --replication-factor 1

  echo "Topic ${topic}.test created (or already exists)"
done
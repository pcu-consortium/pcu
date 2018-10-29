#!/bin/bash

if [ -x "$(command -v docker)" ]; then
    echo "Docker is installed"
else
    echo "You must install docker and/or add your current user to the docker group"
    exit 1
fi

echo "Check Elasticsearch 6.4.2 image in local repository"
docker images docker.elastic.co/elasticsearch/elasticsearch:6.4.2

echo "Start Elasticsearch 6.4.2 container : elasticsearch-pcu"
docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" --name elasticsearch-pcu -d docker.elastic.co/elasticsearch/elasticsearch:6.4.2

echo "Start zookeeper"
docker run -d \
    --net=host \
    --name=zookeeper \
    -e ZOOKEEPER_CLIENT_PORT=2181 \
    -e ZOOKEEPER_TICK_TIME=2000 \
    -e ZOOKEEPER_SYNC_LIMIT=2 \
    confluentinc/cp-zookeeper:5.0.0

echo "Start kafka"
docker run -d \
    --net=host \
    --name=kafka \
    -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \
    -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
    -e KAFKA_BROKER_ID=2 \
    -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
    confluentinc/cp-kafka:5.0.0

echo "Start kafka : waiting..."
sleep 30


echo "Create Ingest topic in Kafka"
docker run \
  --net=host \
  --rm confluentinc/cp-kafka:5.0.0 \
  kafka-topics --create --topic Ingest --partitions 1 --replication-factor 1 \
  --if-not-exists --zookeeper localhost:2181


echo "Check ingest topic in Kafka"
docker run \
  --net=host \
  --rm \
  confluentinc/cp-kafka:5.0.0 \
  kafka-topics --describe --topic Ingest --zookeeper localhost:2181



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

#echo "simple site for agent crawling"
#docker run -p 80:80 kennethreitz/httpbin

echo "Prepare network confluent"
docker network create confluent

echo "Start zookeeper"
docker run -d \
    --net=confluent \
    --name=zookeeper \
    -e ZOOKEEPER_CLIENT_PORT=2181 \
    confluentinc/cp-zookeeper:5.0.0

echo "Start kafka"
docker run -d \
    --net=confluent \
    --name=kafka \
    -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
    -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 \
    -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
    confluentinc/cp-kafka:5.0.0


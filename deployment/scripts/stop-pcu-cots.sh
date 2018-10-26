#!/bin/bash

echo "Stop Elasticsearch 6.4.2 container : elasticsearch-pcu"
docker stop elasticsearch-pcu
echo "Remove Elasticsearch 6.4.2 container : elasticsearch-pcu"
docker rm elasticsearch-pcu
echo "Stop Kafka container: kafka"
docker stop kafka
echo "Remove Kafka container: kafka"
docker rm kafka
echo "Stop zookeeper container: zookeeper"
docker stop zookeeper
echo "Remove zookeeper container: zookeeper"
docker rm zookeeper
echo "Remove network confluent"
docker network rm confluent


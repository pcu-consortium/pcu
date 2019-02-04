#!/bin/bash

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

PCU_BIN_FOLDER="$(pwd)"
cd ${PCU_BIN_FOLDER}/../dist
PCU_DIST_FOLDER="$(pwd)"
cd ${PCU_BIN_FOLDER}
echo "Current folder : ${PCU_BIN_FOLDER}"
echo "Dist files folder : ${PCU_DIST_FOLDER}"

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
    confluentinc/cp-zookeeper:5.0.1

echo "Start kafka"
docker run -d \
    --net=host \
    --name=kafka \
    -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \
    -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
    -e KAFKA_BROKER_ID=2 \
    -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
    confluentinc/cp-kafka:5.0.1

echo "Start kafka : waiting 30s..."
sleep 30

echo "Create topics in Kafka"
docker run \
  --net=host \
  --rm confluentinc/cp-kafka:5.0.1 \
  kafka-topics --create --topic Ingest-File --partitions 1 --replication-factor 1 \
  --if-not-exists --zookeeper localhost:2181

docker run \
  --net=host \
  --rm confluentinc/cp-kafka:5.0.1 \
  kafka-topics --create --topic Ingest-Metadata --partitions 1 --replication-factor 1 \
  --if-not-exists --zookeeper localhost:2181

echo "Check topics in Kafka"
docker run \
  --net=host \
  --rm \
  confluentinc/cp-kafka:5.0.1 \
  kafka-topics --describe --topic Ingest-File --zookeeper localhost:2181

docker run \
  --net=host \
  --rm \
  confluentinc/cp-kafka:5.0.1 \
  kafka-topics --describe --topic Ingest-Metadata --zookeeper localhost:2181

echo "Start mysql 5.6 container"
echo ${PCU_DIST_FOLDER}
docker run --name mysql-pcu \
  --net=host \
  -e MYSQL_ROOT_PASSWORD=password \
  -v ${PCU_DIST_FOLDER}:/opt \
  -d mysql:5.6

echo "Start mysql : waiting 30s..."
sleep 30

echo "Insert data in mysql"
docker exec mysql-pcu /bin/bash -c 'mysql -u root -ppassword </opt/jdbc/data-test.sql'
docker exec mysql-pcu /bin/bash -c 'mysql testdatabase -u root -ppassword </opt/jdbc/data-test-content.sql'

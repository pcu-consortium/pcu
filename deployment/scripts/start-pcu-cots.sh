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


echo "start mysql 5.6 container"


docker run -d --name mysql-server-pcu \
-v /storage/mysql-server-pcu/datadir:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=123 \
severalnines/mysql-pxb:5.6


echo "create database pour test  "
docker exec -it mysql-server-pcu  mysql -uroot -p123 -e "create database testdatabase ;

use testdatabase ;

create table tutorial(id INT NOT NULL AUTO_INCREMENT,    name VARCHAR(100) NOT NULL,      PRIMARY KEY (id ) );

insert into tutorial  (id,name)values(1,'sql');

insert into tutorial  (id,name)values(2,'java');"


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
    -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \²
    -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
    -e KAFKA_BROKER_ID=2 \
    -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
    confluentinc/cp-kafka:5.0.1

echo "Start kafka : waiting..."
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

echo "Start kafka control center"
docker run -d \
  --net=host \
  --name=control-center \
  --ulimit nofile=16384:16384 \
  -p 9021:9021 \
  -v /tmp/control-center/data:/var/lib/confluent-control-center \
  -e CONTROL_CENTER_ZOOKEEPER_CONNECT=localhost:2181 \
  -e CONTROL_CENTER_BOOTSTRAP_SERVERS=localhost:9092 \
  -e CONTROL_CENTER_REPLICATION_FACTOR=1 \
  -e CONTROL_CENTER_MONITORING_INTERCEPTOR_TOPIC_PARTITIONS=1 \
  -e CONTROL_CENTER_INTERNAL_TOPICS_PARTITIONS=1 \
  -e CONTROL_CENTER_STREAMS_NUM_STREAM_THREADS=2 \
  confluentinc/cp-enterprise-control-center:5.0.1



#!/bin/bash

#echo "Clean jupyter"
#docker stop jupyter
#docker rm jupyter

echo "Clean spark worker 1"
docker stop spark-worker-1
docker rm spark-worker-1

echo "Clean spark worker 2"
docker stop spark-worker-2
docker rm spark-worker-2

echo "Clean spark master"
docker stop spark-master
docker rm spark-master

echo "Launch spark master"
docker run \
    --name spark-master \
    -h spark-master \
    -p 8083:8083 \
    -p 8888:8888 \
    -p 7077:7077 \
   	-e SPARK_MASTER_PORT=7077 \
   	-e SPARK_MASTER_WEBUI_PORT=8083 \
   	-e ENABLE_INIT_DAEMON=false \
    -d pcu/spark-master:2.4.0-hadoop2.7

echo "Launch spark worker 1"
docker run \
    --name spark-worker-1 \
    --link spark-master:spark-master \
    -p 8084:8084 \
    -p 7078:7078 \
    -e ENABLE_INIT_DAEMON=false \
	-e SPARK_MASTER_WEBUI_PORT=8083 \
    -e SPARK_WORKER_PORT=7078 \
	-e SPARK_WORKER_WEBUI_PORT=8084 \
    -e SPARK_MASTER=spark://spark-master:7077 \
    -d pcu/spark-worker:2.4.0-hadoop2.7

echo "Launch spark worker 2"
docker run \
    --name spark-worker-2 \
    --link spark-master:spark-master \
    -p 8085:8085 \
    -p 7079:7079 \
    -e ENABLE_INIT_DAEMON=false \
	-e SPARK_MASTER_WEBUI_PORT=8083 \
    -e SPARK_WORKER_PORT=7079 \
	-e SPARK_WORKER_WEBUI_PORT=8085 \
    -e SPARK_MASTER=spark://spark-master:7077 \
    -d pcu/spark-worker:2.4.0-hadoop2.7


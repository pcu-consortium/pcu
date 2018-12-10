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

echo "Stop Elasticsearch 6.4.2 container : elasticsearch-pcu"
docker stop elasticsearch-pcu
echo "Remove Elasticsearch 6.4.2 container : elasticsearch-pcu"
docker rm elasticsearch-pcu
echo "Stop Kafka control center: control-center"
docker stop control-center
echo "Remove Kafka control center: control-center"
docker rm control-center
echo "Stop Kafka container: kafka"
docker stop kafka
echo "Remove Kafka container: kafka"
docker rm kafka
echo "Stop zookeeper container: zookeeper"
docker stop zookeeper
echo "Remove zookeeper container: zookeeper"
docker rm zookeeper
echo "stop  mysql 5.6 container: mysql-pcu"
docker stop mysql-pcu
echo "Remove  mysql 5.6 container: mysql-pcu"
docker rm mysql-pcu

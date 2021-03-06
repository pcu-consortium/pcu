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

INFO='[\033[0;32mPCU\033[0m][\033[0;32mINFO\033[0m]'
WARN='[\033[0;32mPCU\033[0m][\033[0;33mWARN\033[0m]'
ERROR='[\033[0;32mPCU\033[0m][\033[0;31mERROR\033[0m]'

echo -e "${INFO} PCU entreprise search release : START"
DEPLOYMENT_FOLDER=$(pwd) 
RELEASE_FOLDER=${DEPLOYMENT_FOLDER}/pcu-entreprise-search-release
SOURCES_FOLDER=${DEPLOYMENT_FOLDER}/../sources
ES_FOLDER=${DEPLOYMENT_FOLDER}/../../elasticsearch-5.5.1
echo -e "${INFO} Deployment folder:${DEPLOYMENT_FOLDER}"
echo -e "${INFO} Sources folder:${SOURCES_FOLDER}"
echo -e "${INFO} Release folder:${RELEASE_FOLDER}"

echo -e "${INFO} Build sources"
echo -e "${INFO} Build platform sources"
cd ${SOURCES_FOLDER}
mvn clean install -o -DskipTests
echo -e "${INFO} Build filesystem agent"
cd ${SOURCES_FOLDER}/provided/agent-filesystem-norconex
mvn clean install -o -DskipTests
echo -e "${INFO} Build http agent"
cd ${SOURCES_FOLDER}/provided/agent-http-norconex
mvn clean install -o -DskipTests
echo -e "${INFO} Build database agent"
cd ${SOURCES_FOLDER}/provided/agent-database-jdbc
mvn clean install -o -DskipTests
cd ${DEPLOYMENT_FOLDER}

echo -e "${INFO} Generate release folder"
rm -rf ${RELEASE_FOLDER}
mkdir ${RELEASE_FOLDER}
cp -rf ${DEPLOYMENT_FOLDER}/bin ${RELEASE_FOLDER}/bin
cp -rf ${SOURCES_FOLDER}/provided/dist ${RELEASE_FOLDER}
cp -rf ${DEPLOYMENT_FOLDER}/config ${RELEASE_FOLDER}/config
mkdir ${RELEASE_FOLDER}/data
mkdir ${RELEASE_FOLDER}/data/storage
mkdir ${RELEASE_FOLDER}/lib
cp -rf ${SOURCES_FOLDER}/provided/agent-filesystem-norconex/target/pcu-collectors-agent-jar-with-dependencies.jar ${RELEASE_FOLDER}/lib/pcu-collectors-agent-filesystem.jar
cp -rf ${SOURCES_FOLDER}/provided/agent-http-norconex/target/pcu-collectors-agent-jar-with-dependencies.jar ${RELEASE_FOLDER}/lib/pcu-collectors-agent-http.jar
cp -rf ${SOURCES_FOLDER}/provided/agent-database-jdbc/target/pcu-collectors-agent-jar-with-dependencies.jar ${RELEASE_FOLDER}/lib/pcu-collectors-agent-database.jar
cp -rf ${SOURCES_FOLDER}/platform/server/target/pcu-platform-server-exec.jar ${RELEASE_FOLDER}/lib/pcu-platform-server.jar


echo -e "${INFO} PCU entreprise search release : END"


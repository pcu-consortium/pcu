#!/bin/bash

INFO='[\033[0;32mINFO\033[0m]'
WARN='[\033[0;33mWARN\033[0m]'
ERROR='[\033[0;31mERROR\033[0m]'

echo -e "${INFO} PCU entreprise search release : START"
DEPLOYMENT_FOLDER=$(pwd) 
RELEASE_FOLDER=${DEPLOYMENT_FOLDER}/pcu-entreprise-search-release
SOURCES_FOLDER=${DEPLOYMENT_FOLDER}/../sources
ES_FOLDER=${DEPLOYMENT_FOLDER}/../../elasticsearch-5.5.1
echo -e "${INFO} Deployment folder:${DEPLOYMENT_FOLDER}"
echo -e "${INFO} Sources folder:${SOURCES_FOLDER}"
echo -e "${INFO} Release folder:${RELEASE_FOLDER}"

echo -e "${INFO} Build sources"
cd ${SOURCES_FOLDER}
mvn clean install -o -DskipTests
cd ${DEPLOYMENT_FOLDER}

echo -e "${INFO} Generate release folder"
rm -rf ${RELEASE_FOLDER}
mkdir ${RELEASE_FOLDER}
cp -rf ${DEPLOYMENT_FOLDER}/scripts ${RELEASE_FOLDER}/scripts
cp -rf ${DEPLOYMENT_FOLDER}/config ${RELEASE_FOLDER}/config
cp -rf ${SOURCES_FOLDER}/provided/dist ${RELEASE_FOLDER}
cp -rf ${SOURCES_FOLDER}/provided/agent-filesystem-norconex/target/pcu-collectors-agent-jar-with-dependencies.jar ${RELEASE_FOLDER}
cp -rf ${SOURCES_FOLDER}/platform/server/target/pcu-platform-server-*.jar ${RELEASE_FOLDER}/pcu-platform-server.jar

echo -e "${INFO} PCU entreprise search release : END"

#pushd pcu-entreprise-search-beta
#cp -rf ../features/connector/target/pcu-features-connector.jar .
#cp -rf ../features/connector/src/main/resources/*conf .
#cp -rf ../features/connector/src/main/resources/*groovy .
#cp -rf ../features/connector/src/main/resources/*yml .
#cp -rf ../features/connector/src/main/resources/bootstrap .
#cp -rf ../applications/search/target/pcu-application-search.jar .
#cp -rf ../applications/search/src/main/resources/*conf .
#cp -rf ../applications/search/src/main/resources/*groovy .
#cp -rf ../applications/search/src/main/resources/*yml .
#cp -rf ../applications/search/src/main/resources/*properties .
#cp -rf ../../../elasticsearch-5.5.1 .
#rm -rf elasticsearch-5.5.1/data*
#rm -rf pcu_store/
#find . -name "*.log" -exec rm {} \;
#tar cfz ../pcu-entreprise-search-beta_20171206.tar.gz ../pcu-entreprise-search-beta
#popd
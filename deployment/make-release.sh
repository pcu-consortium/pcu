#!/bin/bash

pushd ../sources
mvn clean install -o -DskipTests
mvn package -DskipTests
popd

rm -rf pcu-entreprise-search-beta

cp -rf ../applications/search/dist pcu-entreprise-search-beta
pushd pcu-entreprise-search-beta
# Collector
mkdir collector
pushd collector
cp -rf ../../../sources/connectors/collectors/core/target/*.jar .
cp -rf ../../../sources/connectors/collectors/core/src/main/resources/*.conf .
cp -rf ../../../sources/connectors/collectors/core/src/main/resources/*.yml .
mkdir -p BOOT-INF/lib/
pwd
cp -rf ../../../sources/connectors/collectors/filesystem/target/*.jar BOOT-INF/lib
cp -rf ../../../sources/connectors/indexer/elasticsearch/target/*.jar BOOT-INF/lib
popd

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
popd
tar cfz pcu-entreprise-search-beta_20171206.tar.gz pcu-entreprise-search-beta


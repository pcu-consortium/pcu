#!/bin/bash

mvn clean install -o -DskipTests
pushd applications/search
mvn install -DskipTests -Pjar
popd
# only then (else can't find type ex. PcuModelConfiguration.class in connector jar)
pushd features/connector
mvn install -DskipTests -Pjar
popd

rm -rf pcu-entreprise-search-beta
cp -rf applications/search/dist pcu-entreprise-search-beta
pushd pcu-entreprise-search-beta
cp -rf ../features/connector/target/pcu-features-connector.jar .
cp -rf ../features/connector/src/main/resources/*conf .
cp -rf ../features/connector/src/main/resources/*groovy .
cp -rf ../features/connector/src/main/resources/*yml .
cp -rf ../features/connector/src/main/resources/bootstrap .
cp -rf ../applications/search/target/pcu-application-search.jar .
cp -rf ../applications/search/src/main/resources/*conf .
cp -rf ../applications/search/src/main/resources/*groovy .
cp -rf ../applications/search/src/main/resources/*yml .
cp -rf ../applications/search/src/main/resources/*properties .
cp -rf ../../../elasticsearch-5.5.1 .
rm -rf elasticsearch-5.5.1/data*
rm -rf pcu_store/
#find . -name "*.log" -exec rm {} \;
tar cfz ../pcu-entreprise-search-beta_20171206.tar.gz ../pcu-entreprise-search-beta
popd

=====================================================

PCU Consortium - PCU Platform

https://pcu-consortium.github.io

https://github.com/pcu-consortium/pcu

Copyright (c) 2017 The PCU Consortium

[![Build Status](https://travis-ci.org/pcu-consortium/pcu.svg?branch=master)](https://travis-ci.org/pcu-consortium/pcu) [![Coverage Status](https://coveralls.io/repos/github/pcu-consortium/pcu/badge.svg?branch=master)](https://coveralls.io/github/pcu-consortium/pcu?branch=master) [![Download](https://api.bintray.com/packages/pcu-consortium/pcu/pcu-entreprise-search/images/download.svg?version=beta_20171206) ](https://bintray.com/pcu-consortium/pcu/pcu-entreprise-search/beta_20171206/view/files) [![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)](https://opensource.org/licenses/Apache-2.0) [![Project Stats](https://www.openhub.net/p/pcu-consortium-pcu/widgets/project_thin_badge.gif)](https://www.openhub.net/p/pcu-consortium-pcu)

=====================================================


About PCU Platform
----------

The PCU Consortium aims at building an Open Source, unified Machine Learning (ML) platform targeted at business applications such as ecommerce,
that makes search a first-class citizen of Big Data.
It is bundled with a default application : the PCU Entreprise Search engine.


Features
   * Big Data components : integrated in a pluggable, decoupled manner by Service Provider Interfaces (SPI) for **search, compute, messaging, fs**
      * search : ElasticSearch client API (& search SPI impl). It is built on built on Apache CXF, Jackson, Spring Boot. See
https://github.com/pcu-consortium/pcu/blob/master/providers/search/elasticsearch & its README.
      * file storage : local file storage REST server. Uses implicit hash or explicit name as id, direct-to-disk HTTP streaming upload.
Upcoming : auto cleanup, quotas.
   * search API (PcuSearchApiServerImpl) : on top of search SPI (PcuSearchApiSimpleImpl), with
      * async indexing through Kafka + Spark Streaming YAML-configured pipeline (PcuSearchApiPipelineImpl)
      * ML-powered and value-added search query engine (PcuSearchApiEngineImpl)
   * upcoming : other providers (Spark compute, Kafka messaging), text mining / NLP, core Manager, search and ecommerce ML algorithms...

Tools
   * upcoming : Swagger online API developer documentation and further playground

Team
   * Specifications : the PCU Consortium
   * foundations (Spring, REST), ElasticSearch API, file indexer & Avro data model prototype, File content API & impl, server : Marc Dutoo, Smile
   * File content Metadata Extractor : Emmanuel Keller, Wallix / Qwazr
   * Entreprise Search UI : Jennifer Aouizerat (design), Romain Gilles (integration), Marc Dutoo (v0), Smile

License : Apache License 2.0

Requirements : [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), [Maven 3](http://maven.apache.org/download.cgi)

Roadmap : TODO


# Latest release
The latest release is [PCU Entreprise Search beta Technology Preview](https://owncloud.smile.eu/s/6toBC6N2VJv4EHC).


# Quickstart

## Clone sources
````bash
git clone git@github.com:pcu-consortium/pcu.git
````

## Build
````bash
mvn clean install -DskipTests
````

NB. in order to get newer versions of the big data components that are integrated through their SNAPSHOT versions, such as [Qwazr Extractor](providers/metadata/extractor) :
````bash
mvn -U clean install
````

## Try it out
The easiest way to test the PCU platform is to use it as an Entreprise Search server :
````bash
cd application/search
mvn spring-boot:run
````

then configure a file connector to crawl for instance your home, and start it :
````bash
cd features/connector/
vi src/main/resources/bootstrap/poller.yaml
roots:
  - /home/yourusername
mvn spring-boot:run
````

and browse to its web search UI at [http://localhost:8080](http://localhost:8080).

You can also only start its backend instead :
````bash
cd features/search/server
mvn -Pheadless spring-boot:run
````
then go to the [Swagger UI playground](http://localhost:8080/pcu/api-docs?url=http://localhost:8080/pcu/swagger.json) and try its indexing and search request samples.

## Run unit tests
Install [ElasticSearch 5.5.1](https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.5.1.tar.gz) and start it :
````bash
cd elasticsearch-5.5.1/bin
./elasticsearch
````

Install [Kafka 1.0](http://www.us.apache.org/dist/kafka/1.0.0/kafka_2.11-1.0.0.tgz), start it and create the "file" topic :
````bash
cd kafka
bin/zookeeper-server-start.sh config/zookeeper.properties &
bin/kafka-server-start.sh config/server.properties &
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic file --zookeeper localhost:2181
````

Run unit tests :
````bash
mvn clean install
````


# Developers

## Release

In order to assemble a binary release, do as scripted in [make-release.sh](make-release.sh).

In order to be able to do it with a non-SNAPSHOT version, do as follow :

Remove -SNAPSHOT from integrated Big Data component provider modules, such as [Qwazr Extractor](providers/metadata/extractor). This implies that both release should be somewhat coordinated.

Remove -SNAPSHOT from all modules, tag (-DtagNameFormat=@{project.version}), upgrade project version number :
````bash
mvn release:prepare -DautoVersionSubmodules=true (-Dresume=false) (-DdryRun=true)
````
To test it, add -DdryRun=true. To retry, add -Dresume=false. To publish to Maven Central repository, follow steps in [Sonatype guide](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide) with help [from these hints](http://danhaywood.com/2013/07/11/deploying-artifacts-to-maven-central-repo/).

Then (checkout said tag and) do as scripted in make-release.sh .

## Faster build

To achieve a faster build, you can skip tests, stay offline (provided all dependencies have already been installed in a previous build) and skip installing node & npm & npm packages (provided they've already been in a previous build hand have not changed since) :
````bash
mvn clean install -o -DskipTests -DskipNpm
````

## Frontend

Hot deploy (of js changes) should work out of the box. Otherwise, rather use the proxy :
````bash
cd src/main/frontend
../../../target/node/npm start
````
and browse to the proxied web UI at [http://localhost:9090](http://localhost:9090).

If compilation fails overall, recheck compilation without restarting with :
````bash
cd src/main/frontend
./node_modules/webpack/bin/webpack.js
````

## Continuous Integration

PCU is continuously built and tested in [Travis]( https://travis-ci.org/pcu-consortium/pcu) (see [.travis.yml](.travis.yml) configuration), with provisioning of ElasticSearch and Kafka.

## REST  & JAXRS best practices

- no 2 operations with same path, rather provide suppl helper operations in ElasticSearchClientApi,
else JAXRS can't differentiate them :
nov. 15, 2017 1:33:22 PM org.apache.cxf.jaxrs.model.OperationResourceInfoComparator compare
AVERTISSEMENT: Both org.pcu.providers.search.elasticsearch.spi.ESSearchProviderEsApiServerImpl#searchInType
and org.pcu.providers.search.elasticsearch.spi.ESSearchProviderEsApiServerImpl#searchInType are equal candidates
for handling the current request which can lead to unpredictable results

### React.js best practices
- built using babel (provides jsx compilation, latest ES2015, polyfill)

- no redux, overblown for a simple application like Entreprise Search (at least the front), makes it harder to understand how it works (dispatches over more files). And if the need appears, it can still be easily refactored in.

- Axios for REST, the best 

- extends React.Component rather than createClass(), which will at somepoint be removed, see https://toddmotto.com/react-create-class-versus-component/

- bind using anonymous function : ````handleClick = () => { ...```` It is terser than in the constructor, faster than in render() which new fct each time. See https://stackoverflow.com/documentation/reactjs/6371/react-createclass-vs-extends-react-component#t=20171002141935868464

- setState() good practice : ````this.setState(state => ({...state, results: [] }));````. See https://engineering.musefind.com/our-best-practices-for-writing-react-components-dec3eb5c3fc8 with spread operator and https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Spread_operator


## PCU project file tree draft :

pcu/

 (big_data_components/)

 (storage/ (stor ?))
 search/ (cpt-search ? cpt-st-search ?)
  spi (jaxrs, jackson, swagger), client (cxf, spring-boot) with test mock server
  elasticsearch/
   api (jaxrs, jackson, swagger), client (cxf, spring-boot), spi-impl with integration test
  solr/...
 file/
 (columnarfile/, columnardb/, documentdb/...)

 compute/ (comp ? cpt-comp ?)
  spi, client
  spark/
   api, client, spi-impl

 messaging/
  kafka/

 (platform/)

 core/ (or separately : metadata & configuration, its api developer & configuration backoffice, deployment or in other submodules...)

 pipeline/

 connector/ (& extractor...)

 (features/)
 
 search/ (feature-search ?)

 rec/ (recommendation ? feature-recommendation ?)

 (use cases/)

 entreprisesearch/ (app-search ? with simple built-in front-end)

 ecommerce/ (with Magento-specific extensions being in another project)

 B2B/, digitalinstore/ ...



=====================================================

PCU Consortium - PCU Platform

https://pcu-consortium.github.io

https://github.com/pcu-consortium/pcu

Copyright (c) 2017 The PCU Consortium

[![Build Status](https://travis-ci.org/pcu-consortium/pcu.svg?branch=master)](https://travis-ci.org/pcu-consortium/pcu) [![Coverage Status](https://coveralls.io/repos/github/pcu-consortium/pcu/badge.svg?branch=master)](https://coveralls.io/github/pcu-consortium/pcu?branch=master) [![Download](https://api.bintray.com/packages/pcu-consortium/pcu/pcu-entreprise-search/images/download.svg?version=beta_20171206) ](https://bintray.com/pcu-consortium/pcu/pcu-entreprise-search/beta_20171206/view/files) [![License](https://img.shields.io/badge/License-Apache%202.0-red.svg)](https://opensource.org/licenses/Apache-2.0)

=====================================================


About PCU Platform
----------

The PCU Consortium aims at building an Open Source, unified Machine Learning (ML) platform targeted at business applications such as ecommerce,
that makes search a first-class citizen of Big Data.

License : [Apache License 2.0](LICENCE)

Requirements : [Java Open JDK 10](https://openjdk.java.net/projects/jdk/10/), [Maven 3](http://maven.apache.org/download.cgi)

Authors : [Authors](AUTHORS.md)


# PCU Package

*(WIP) deploy packaged version in bintray*

PCU can build a packaged version (available only on Linux).

The requirements are :
* openjdk 10 (build & execute)
* apache maven 3+ (build)
* docker as sudo user (execute)

The packaged version contains :
* PCU platform
* PCU provided files collector agent 
* PCU provided web collector agent
* PCU provided database collector agent

For each of these modules are provided scripts, configurations and sample datas.
The package uses docker images of cots (elasticsearch, kafka, zookeeper, mysql) for its uses.

````
-pcu-entreprise-search-release/
---config/
---data/
---dist/
---lib/
---bin/
````
The scripts available in *bin/* are :
* **start-pcu-cots.sh** : launch and initialize docker images of elasticsearch, zookeeper,  kafka and mysql
* **stop-pcu-cots.sh** : stop and remove the docker images
* **start-pcu-platform-server.sh** : start PCU platform
* **start-agent-filesystem.sh** : execute PCU provided files collector agent on sample data
* **start-agent-http.sh** : execute PCU provided web collector agent on *http://www.open-source-guide.com/Solutions* website
* **start-agent-database.sh** : execute PCU provided database collector agent on sample mysql database

# Quickstart

## Clone sources

````bash
git clone git@github.com:pcu-consortium/pcu.git
````

## Demo Package

### Build

[Build requirements](#PCU-Package)

Build the package:
````bash
cd deployment
./make-release.sh
````
This creates the package folder  : deployment/pcu-entreprise-search-release

### Execute

[Execution requirements](#PCU-Package)

1. Launch the COTS :
````bash
cd deployment/pcu-entreprise-search-release/bin
./start-pcu-cots.sh
````
This create and start all the docker container used by the Platform and its provided agents :
* elasticsearch-pcu (docker.elastic.co/elasticsearch/elasticsearch:6.4.2)
* zookeeper (confluentinc/cp-zookeeper:5.0.1)
* kafka (confluentinc/cp-kafka:5.0.1)
* mysql-pcu (mysql:5.6)

2. Execute PCU platform server :
````bash
cd deployment/pcu-entreprise-search-release/bin
./start-pcu-platform-server.sh
````
This start the PCU platform server on port 8080 : http://localhost:8080

3. Execute PCU provided files collector agent :
````bash
cd deployment/pcu-entreprise-search-release/bin
./start-agent-filesystem.sh
````
To check if data has been collected search : *PCU*

4. Execute PCU provided web collector agent :
````bash
cd deployment/pcu-entreprise-search-release/bin
./start-agent-http.sh
````
To check if data has been collected search : *Open*

5. Execute PCU provided database collector agent :
````bash
cd deployment/pcu-entreprise-search-release/bin
./start-agent-database.sh
````
To check if data has been collected search : *ACAJOU*


# Build PCU

* To build PCU platform :
````bash
cd sources
mvn clean install -DskipTests
````

* To build provided files agent :
````bash
cd provided/agent-filesystem-norconex
mvn clean install -DskipTests
````

* To build provided web agent :
````bash
cd provided/agent-http-norconex
mvn clean install -DskipTests
````

* To build provided database agent :
````bash
cd provided/agent-database-jdbc
mvn clean install -DskipTests
````

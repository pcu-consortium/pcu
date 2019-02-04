=====================================================

PCU Consortium - PCU Entreprise Search Beta

https://pcu-consortium.github.io

https://github.com/pcu-consortium/pcu

Copyright (c) 2017 The PCU Consortium

=====================================================


About PCU Platform
----------

The PCU Consortium aims at building an Open Source, unified Machine Learning (ML) platform targeted at business applications such as ecommerce,
that makes search a first-class citizen of Big Data.
It is bundled with a default application : the PCU Entreprise Search engine.

Team
   * Specifications : the PCU Consortium (Smile, LIPN, ESILV, Proxem, Armadillo, Wallix)
   * foundations (Spring, REST), ElasticSearch API, File content API & impl, server, Kafka indexing pipeline & Avro data model check, connector & crawler : Marc Dutoo, Smile
   * File content Metadata Extractor : Emmanuel Keller, Wallix / Qwazr
   * Entreprise Search UI : Jennifer Aouizerat (design), Romain Gilles (integration), Marc Dutoo (v0), Smile

License : Apache License 2.0

Requirements : [Java JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), [Maven 3](http://maven.apache.org/download.cgi)


PCU Entreprise Search Beta Quickstart
----------


Install Java (JDK 8) :
======================

Follow steps described at http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html


Start ElasticSearch :
=====================

cd elasticsearch-5.5.1/bin
./elasticsearch


Start PCU Entreprise Search server :
====================================

java -Dloader.path=.\,BOOT-INF/lib/ -jar pcu-application-search.jar

or "fully executable" jar (Unix-only) :

./pcu-application-search.jar


Start PCU Entreprise Search connector :
=======================================

java -Dloader.path=.\,BOOT-INF/lib/ -jar pcu-features-connector.jar

or "fully executable" jar (Unix-only) :

./pcu-features-connector.jar


Demo :
======

browse to the UI at http://localhost:8080

search "Entreprise Search"

it should display the 3 files crawled in the file/ directory.

the API documentation is available at http://localhost:8080/pcu/api-docs?url=http://localhost:8080/pcu/swagger.json

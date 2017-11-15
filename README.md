=====================================================

PCU Consortium - PCU Platform

https://pcu-consortium.github.io

https://github.com/pcu-consortium/pcu

Copyright (c) 2017 The PCU Consortium

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


# Quickstart

## Clone sources
git clone git@github.com:pcu-consortium/pcu.git

## Build
mvn clean install

## Test
The easiest way to test the PCU platform is to use it as an Entreprise Search server :
````bash
cd application/search
mvn spring-boot:run
````
then start a file connector (upcoming) and browse to its web search UI at [http://localhost:8080](http://localhost:8080).

You can also only start its backend :
````bash
cd features/search/server
mvn -Pheadless spring-boot:run
````
then go to the [Swagger UI playground](http://localhost:8080/pcu/api-docs?url=http://localhost:8080/pcu/swagger.json) and try its indexing and search request samples.


# Developers

## REST  & JAXRS best practices

- no 2 operations with same path, rather provide suppl helper operations in ElasticSearchClientApi,
else JAXRS can't differentiate them :
nov. 15, 2017 1:33:22 PM org.apache.cxf.jaxrs.model.OperationResourceInfoComparator compare
AVERTISSEMENT: Both org.pcu.providers.search.elasticsearch.spi.ESSearchProviderEsApiServerImpl#searchInType
and org.pcu.providers.search.elasticsearch.spi.ESSearchProviderEsApiServerImpl#searchInType are equal candidates
for handling the current request which can lead to unpredictable results

## frontend

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



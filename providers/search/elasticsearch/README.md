About :
- ElasticSearch client API (& its PCU SPI impl) is built on built on Apache CXF, Jackson, Spring Boot. For now, it supports index mapping configuration (most, though maybe not typed fine enough), document indexing and management, search (bool, multi_match, terms, script).
- API operations are also tested on an embedded mock server
- supported API features are tested on ElasticSearch 5.5 (to be started separately) in https://github.com/pcu-consortium/pcu/blob/master/providers/search/elasticsearch/client/src/test/java/org/pcu/search/elasticsearch/client/PcuElasticSearchApiClientTest.java
- features that are yet to be tested (and often require enriching the API) are : auth, phonetic, function_score, sort, synonym & acronym, facettes, templates, native query, snippet/highlight, suggest, auto completion, bulk

Setup :
- sudo update-alternatives --config java
- ElasticSearch 5.5 https://www.elastic.co/fr/downloads/elasticsearch

Running it : see tests in client/

Debug :
see es-rest-mock.log

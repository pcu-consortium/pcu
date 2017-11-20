package org.pcu.search.elasticsearch.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.search.elasticsearch.PcuElasticSearchClientApplication;
import org.pcu.search.elasticsearch.api.Document;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.ElasticSearchClientApi;
import org.pcu.search.elasticsearch.api.GetResult;
import org.pcu.search.elasticsearch.api.QueryDocument;
import org.pcu.search.elasticsearch.api.mapping.Analysis;
import org.pcu.search.elasticsearch.api.mapping.Analyzer;
import org.pcu.search.elasticsearch.api.mapping.IndexMapping;
import org.pcu.search.elasticsearch.api.mapping.IndexResult;
import org.pcu.search.elasticsearch.api.mapping.IndexSettings;
import org.pcu.search.elasticsearch.api.mapping.PropertyMapping;
import org.pcu.search.elasticsearch.api.mapping.PutMappingResult;
import org.pcu.search.elasticsearch.api.mapping.TokenFilter;
import org.pcu.search.elasticsearch.api.mapping.TypeMapping;
import org.pcu.search.elasticsearch.api.query.ESQueryMessage;
import org.pcu.search.elasticsearch.api.query.ESRescore;
import org.pcu.search.elasticsearch.api.query.Highlight;
import org.pcu.search.elasticsearch.api.query.HighlightParameters;
import org.pcu.search.elasticsearch.api.query.Hit;
import org.pcu.search.elasticsearch.api.query.SearchResult;
import org.pcu.search.elasticsearch.api.query.clause.DecayFunctionScoreFieldParameters;
import org.pcu.search.elasticsearch.api.query.clause.ESScript;
import org.pcu.search.elasticsearch.api.query.clause.ESScriptScript;
import org.pcu.search.elasticsearch.api.query.clause.FieldValueFactorFunctionScore;
import org.pcu.search.elasticsearch.api.query.clause.FunctionScoreFilter;
import org.pcu.search.elasticsearch.api.query.clause.bool;
import org.pcu.search.elasticsearch.api.query.clause.function_score;
import org.pcu.search.elasticsearch.api.query.clause.more_like_this;
import org.pcu.search.elasticsearch.api.query.clause.multi_match;
import org.pcu.search.elasticsearch.api.query.clause.query_string;
import org.pcu.search.elasticsearch.api.query.clause.script;
import org.pcu.search.elasticsearch.api.query.clause.terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.eaio.uuid.UUID;


/**
 * Test of ES search API and features
 * WARNING requires ElasticSearch 5.5 to have been started independently.
 * 
 * TODO :
 * - missing features
 * - auto provision ES
 * - move index mapping building to generic buildXXMapping() methods, then to service,
 * and while refactoring to unified api, think about PCU metadata manager.
 * 
 * @author mdutoo
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes=PcuElasticSearchClientApplication.class,
   initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("test")
public class PcuElasticSearchApiClientTest {

   protected static final Logger LOGGER = LoggerFactory.getLogger(PcuElasticSearchApiClientTest.class);
   
   private boolean debug = true;

   // should be magical proxy (no impl in -client)
   @Autowired
   protected ElasticSearchClientApi es;

   protected String index = "files" + "_test"; // TODO ? + "_ElasticSearchApi"; // to avoid polluting (production) db
   protected String type = "file";
   protected String docId;
   
   
   protected PutMappingResult cleanAndSetupIndex(String index, IndexMapping indexMapping) throws ESApiException {
      // clean :
      try {
         es.deleteMapping(index);
      } catch(ESApiException esex) {
         assertTrue(esex.getAsJson().contains("index_not_found_exception"));
         LOGGER.info("There was no need to cleanup before testing");
         //String msg = IOUtils.toString((InputStream) waex.getResponse().getEntity());
         //msg.toString();
      }

      // setup :
      return es.putMapping(index, indexMapping);
   }
   
   /**
    * file with attachment (resume) Entreprise Search
    * example inspired by ES mapper-attachments plugin without store from https://qbox.io/blog/index-attachments-files-elasticsearch-mapper
    * @return
    */
   protected IndexMapping buildFilesIndexExample() {
      // define model mapping & indexing conf :
      IndexMapping indexMapping = new IndexMapping();
      IndexSettings settings = new IndexSettings();
      Analysis analysis = new Analysis();
      settings.setAnalysis(analysis);
      indexMapping.setSettings(settings);
      LinkedHashMap<String, TypeMapping> mappings = new LinkedHashMap<String, TypeMapping>();
      indexMapping.setMappings(mappings);
      
      // define indexing features :
      // see MES' at https://github.com/Smile-SA/elasticsuite/blob/master/src/module-elasticsuite-core/etc/elasticsuite_analysis.xml
      analysis.setAnalyzer(new LinkedHashMap<String,Analyzer>());
      
      // fulltext for file names (with word_delimiter) :
      Analyzer fileNameAnalyzer = new Analyzer();
      analysis.getAnalyzer().put("fileNameAnalyzer", fileNameAnalyzer);
      fileNameAnalyzer.setTokenizer("standard"); // else none
      fileNameAnalyzer.setFilter(new ArrayList<String>());
      fileNameAnalyzer.getFilter().add("word_delimiter");
      /*TokenFilter wordDelimiterTF = new TokenFilter();
      wordDelimiterTF.setType("word_delimiter");
      wordDelimiterTF.setCatenate_words(true);
      analysis.setFilter(new LinkedHashMap<>());
      analysis.getFilter().put("wordDelimiter", wordDelimiterTF);*/
      // fulltext conf :
      // ex. test : curl -XGET 'localhost:9200/files/_analyze?analyzer=fileContentAnalyzer&pretty' -d 'Test text to see shingles and stopwords'
      
      // fulltext (with shingle) :
      Analyzer fileContentAnalyzer = new Analyzer();
      analysis.getAnalyzer().put("fileContentAnalyzer", fileContentAnalyzer);
      /*fileContentAnalyzer.setType("standard");
      fileContentAnalyzer.setStopwords("_english_");*/ // setting a (non-custom) type would prevent shingle
      /*TokenFilter shingleTF = new TokenFilter();
      shingleTF.setType("shingle");
      analysis.getFilter().put("shingle", shingleTF);*/
      fileContentAnalyzer.setTokenizer("standard");
      fileContentAnalyzer.setFilter(new ArrayList<String>());
      fileContentAnalyzer.getFilter().add("standard"); // noop
      fileContentAnalyzer.getFilter().add("lowercase");
      fileContentAnalyzer.getFilter().add("asciifolding"); // for ex. french within english
      fileContentAnalyzer.getFilter().add("stop"); // stopwords that work with shingles
      fileContentAnalyzer.getFilter().add("shingle");
      // alt phonetic conf NOT USED YET :
      // requires plugin https://www.elastic.co/guide/en/elasticsearch/plugins/5.5/analysis-phonetic.html
      TokenFilter phonetic = new TokenFilter();
      ///analysis.getFilter().put("phonetic", phonetic);
      phonetic.setType("phonetic");
      phonetic.setEncoder("metaphone");
      Analyzer fileContentAnalyzerPhonetic = new Analyzer();
      analysis.getAnalyzer().put("fileContentAnalyzerPhonetic", fileContentAnalyzerPhonetic);
      fileContentAnalyzerPhonetic.setTokenizer("standard");
      fileContentAnalyzerPhonetic.setFilter(new ArrayList<String>());
      fileContentAnalyzerPhonetic.getFilter().add("standard"); // noop
      fileContentAnalyzerPhonetic.getFilter().add("lowercase");
      fileContentAnalyzerPhonetic.getFilter().add("asciifolding"); // for ex. french within english
      fileContentAnalyzerPhonetic.getFilter().add("stop"); // stopwords that work with shingles
      ///fileContentAnalyzerPhonetic.getFilter().add("phonetic");
      
      // french conf NOT USED YET :
      // rather using default, but could be redefined to add ex. shingle or phonetic
      // taken from example https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-lang-analyzer.html#french-analyzer
      analysis.setFilter(new LinkedHashMap<String,TokenFilter>());
      TokenFilter elision_fr = new TokenFilter();
      analysis.getFilter().put("elision_fr", elision_fr);
      elision_fr.setType("elision");
      elision_fr.setArticles(Arrays.asList("l", "m", "t", "qu", "n", "s", "j"));
      TokenFilter stop_fr = new TokenFilter();
      analysis.getFilter().put("stop_fr", stop_fr);
      stop_fr.setType("stop");
      stop_fr.setStop("_french_");
      TokenFilter stemmer_fr = new TokenFilter();
      analysis.getFilter().put("stemmer_fr", stemmer_fr);
      stemmer_fr.setType("stemmer");
      stemmer_fr.setLanguage("light_french");
      Analyzer fileContentFrAnalyzer = new Analyzer(); // redefine fr analyzer to add shingles
      analysis.getAnalyzer().put("fileContentFrAnalyzer", fileContentFrAnalyzer);
      fileContentFrAnalyzer.setTokenizer("standard");
      fileContentFrAnalyzer.setFilter(new ArrayList<String>());
      fileContentFrAnalyzer.getFilter().add("standard"); // noop
      fileContentFrAnalyzer.getFilter().add("elision_fr");
      fileContentFrAnalyzer.getFilter().add("lowercase");
      fileContentFrAnalyzer.getFilter().add("stop_fr");
      //fileContentFrAnalyzer.getFilter().add("french_keywords"); // stemmer excluder
      fileContentFrAnalyzer.getFilter().add("stemmer_fr");
      fileContentFrAnalyzer.getFilter().add("shingle");//
      ///fileContentFrAnalyzer.getFilter().add("phonetic_fr");
      // alt french phonetic conf :
      // requires plugin https://www.elastic.co/guide/en/elasticsearch/plugins/5.5/analysis-phonetic.html
      TokenFilter phonetic_fr = new TokenFilter();
      ///analysis.getFilter().put("phonetic_fr", phonetic_fr);
      phonetic_fr.setType("phonetic");
      phonetic_fr.setEncoder("beidermorse");
      phonetic_fr.setLanguageset(Arrays.asList("french"));
      
      // define model mapping :
      TypeMapping fileMapping = new TypeMapping();
      mappings.put("file", fileMapping);
      fileMapping.setProperties(new LinkedHashMap<>());
      
      PropertyMapping fileNameMapping = new PropertyMapping();
      fileMapping.getProperties().put("name", fileNameMapping);
      fileNameMapping.setType("text");
      fileNameMapping.setAnalyzer("fileNameAnalyzer");
      // TODO letter tokenizer (& shingle...)
      fileNameMapping.setCopy_to(Arrays.asList("_all", // search in all fields
            "name_default"));
      if (debug) fileNameMapping.setFielddata(true); // DEBUG NO _termvectors enough ; else on script_fields : Fielddata is disabled on text fields by default.
      // orig path :
      // TODO path, host, protocol not fulltext (or path de-boosted, at query time)
      PropertyMapping fileNameDefaultMapping = new PropertyMapping();
      fileMapping.getProperties().put("name_default", fileNameDefaultMapping);
      fileNameDefaultMapping.setType("text");
      fileNameDefaultMapping.setStore(true); // NOOOO KO else can't get content of field https://stackoverflow.com/questions/36618549/is-it-possible-to-get-contents-of-copy-to-field-in-elasticsearch
      fileNameDefaultMapping.setFielddata(true);

      // nested/object type, for binary file :
      PropertyMapping fileContentMapping = new PropertyMapping();
      fileContentMapping.setProperties(new LinkedHashMap<>());
      fileMapping.getProperties().put("content", fileContentMapping);
      // [SPEC] fulltext extracted from binary, analyzed but TODO possibly not stored :
      PropertyMapping fileContentContentMapping = new PropertyMapping();
      fileContentContentMapping.setType("text");
      fileContentContentMapping.setStore(true); // false TODO NOO
      //if (debug) fileContentContentMapping.setCopy_to(Arrays.asList("content_fr")); // DEBUG NOO
      if (debug) fileContentContentMapping.setFielddata(true); // DEBUG NO _termvectors enough TODO NOO ; else on script_fields : Fielddata is disabled on text fields by default.
      fileContentContentMapping.setAnalyzer("fileContentAnalyzer");
      fileContentMapping.getProperties().put("content", fileContentContentMapping);
      PropertyMapping fileContentContentFrMapping = new PropertyMapping();
      fileContentContentFrMapping.setType("text");
      fileContentContentFrMapping.setStore(true); // false TODO NOO
      // language - french analyzer :
      if (debug) fileContentContentFrMapping.setFielddata(true); // DEBUG NO _termvectors enough TODO NOO ; else on script_fields : Fielddata is disabled on text fields by default.
      fileContentContentFrMapping.setAnalyzer("french"); // TODO have to redefine it to add shingles (but stopwords ex. et, stemming ex. developpeu informat, elision ex. l', asciifolding ex. expérience work OK)
      fileContentMapping.getProperties().put("content_fr", fileContentContentFrMapping);
      // multi-field, for alt phonetic analyzer :
      fileContentContentMapping.setFields(new LinkedHashMap<>());
      PropertyMapping fileContentContentFrPhoneticMapping = new PropertyMapping();
      fileContentContentMapping.getFields().put("phonetic", fileContentContentFrPhoneticMapping);
      fileContentContentFrPhoneticMapping.setType("text");
      fileContentContentFrPhoneticMapping.setAnalyzer("fileContentFrAnalyzer");
      fileContentContentFrPhoneticMapping.setStore(true); // false TODO NOO
      PropertyMapping fileContentPathMapping = new PropertyMapping();
      fileContentPathMapping.setType("keyword"); // for find by dfs path ? not analyzed ; NB. auto sets ingore_above=256
      fileContentMapping.getProperties().put("path", fileContentPathMapping); // on dfs
      // binary file - mime type, encoding, length, hash : https://doc.nuxeo.com/nxdoc/file-storage/
      PropertyMapping fileContentHashMapping = new PropertyMapping();
      fileContentHashMapping.setType("keyword"); // [SPEC] NOT analyzed ; NB. auto sets ingore_above=256
      fileContentMapping.getProperties().put("hash", fileContentHashMapping);
      PropertyMapping fileContentLengthMapping = new PropertyMapping();
      fileContentLengthMapping.setType("long"); // [SPEC] numeric
      fileContentMapping.getProperties().put("length", fileContentLengthMapping); // binary size
      PropertyMapping fileContentModifiedMapping = new PropertyMapping();
      fileContentModifiedMapping.setType("date"); // [SPEC] date
      fileContentMapping.getProperties().put("modified", fileContentModifiedMapping); // NB. "created" etc. if file through java.nio http://docs.oracle.com/javase/tutorial/essential/io/fileAttr.html https://stackoverflow.com/questions/2723838/determine-file-creation-date-in-java
      
      return indexMapping;
   }

   /** TODO without @Pre 
    * @return */
   @Test
   public void testApi() throws IOException, ESApiException {
      //String index = "files";
      String type = "file";
      
      // (clean and) setup :
      IndexMapping indexMapping = buildFilesIndexExample();
      PutMappingResult mappingRes = cleanAndSetupIndex(this.index, indexMapping);
      assertNotNull(mappingRes);
      assertTrue(mappingRes.isShards_acknowledged());
      LinkedHashMap<String, IndexMapping> foundMappingRes = es.getMapping(index);
      assertEquals(indexMapping.getMappings().size(), foundMappingRes.size());
      
      // index doc :
      //String id = "file://server1/a/b/file.doc"; // NOO ES doesn't support slash, and not a good Lucene id anyway
      String id = new UUID().toString(); // best lucene id http://blog.mikemccandless.com/2014/05/choosing-fast-unique-identifier-uuid.html
      Long version = null;
      Document doc = new Document();
      doc.getProperties().put("name", "cv_johndoe.doc");
      LinkedHashMap<String,Object> docContentProps = new LinkedHashMap<String,Object>(5);
      doc.getProperties().put("content", docContentProps);
      docContentProps.put("path", "node1/mycompany/employees/cv_johndoe.doc"); // on dfs
      docContentProps.put("hash", "AEBGD");
      docContentProps.put("length", 1234123);
      docContentProps.put("modified", Instant.now());
      // sample content : en & fr, stop
      docContentProps.put("content", "John Doe - CV\nExperiences:\n"
            + "2016-2017 ElasticSearch and Spark developer\n"
            + "2015-2016 I Phone developer\n");
      docContentProps.put("content_fr", "John Doe - CV\nExpériences :\n"
            + "2016-2017 développeur ElasticSearch et Spark\n"
            + "2016-2017 développeur I Phone\n"
            + "\nHobby :\n"
            + "les chevaux, l'informatique"); // fr stemming, elision
      doc.getProperties().put("path", "/mycompany/employees"); // orig path
      doc.getProperties().put("host", "server1");
      doc.getProperties().put("protocol", "file");
      
      IndexResult indexRes = es.indexDocument(index, type, id, doc, null, null, version, null, null, null, null);
      assertNotNull(indexRes);
      assertEquals(index, indexRes.get_index());
      
      GetResult getRes = es.getDocument(index, type, id, null, null, null, null, null, null, null, null);
      assertNotNull(getRes);
      assertEquals(index, getRes.get_index());
      assertEquals("cv_johndoe.doc", getRes.get_source().getProperties().get("name"));
      // BEWARE dates are returned as string
      // => TODO format them explicitly OR write a custom Jackson deserializer that recognizes formats OR knows mappings
      @SuppressWarnings("unchecked")
      String foundDate = (String) ((LinkedHashMap<String, Object>)
            getRes.get_source().getProperties().get("content")).get("modified");
      assertEquals(docContentProps.get("modified").toString(), foundDate);
      docId = getRes.get_id();
      
      Document foundDoc = es.getDocumentSource(index, type, id, null, null, null, null, null, null, null);
      assertNotNull(foundDoc);
      assertEquals("slash should be supported in id even not at the end of the operation URL",
            "cv_johndoe.doc", foundDoc.getProperties().get("name"));
      
      // bulk :
      // TODO define another client with mapper without indent, or extract in another test disabling the pcu.rest.enableIndenting prop
      /*
      BulkMessage bulkMessage = new BulkMessage();
      bulkMessage.setActions(new ArrayList<>());
      BulkAction bulkAction1 = new BulkAction();
      bulkAction1.setKindToAction(new LinkedHashMap<>());
      IndexAction indexAction1 = new IndexAction();
      indexAction1.set_index("files");
      indexAction1.set_type("file");
      indexAction1.set_id(new UUID().toString());
      bulkAction1.getKindToAction().put("index", indexAction1);
      Document doc1 = new Document();
      doc1.getProperties().put("name", "cv_janedoe.doc");
      bulkAction1.setDoc(doc1);
      bulkMessage.getActions().add(bulkAction1);
      IndexAction indexAction2 = new IndexAction();
      indexAction2.set_index("files");
      indexAction2.set_type("file");
      indexAction2.set_id(new UUID().toString());
      BulkAction bulkAction2 = new BulkAction();
      bulkAction2.setKindToAction(new LinkedHashMap<>());
      bulkAction2.getKindToAction().put("index", indexAction2);
      Document doc2 = new Document();
      doc2.getProperties().put("name", "cv_janedoe.doc");
      bulkAction2.setDoc(doc2);
      bulkMessage.getActions().add(bulkAction2);
      BulkResult bulkRes = es.bulk(bulkMessage, null, null);
      assertEquals(2, bulkRes.getItems().size());
      assertEquals(indexAction1.get_id(), bulkRes.getItems().get(0).get("index").get_id());
      */
   }
   
   @Test
   public void testFeatures() throws IOException, ESApiException {
      // setup conf & data :
      testApi();
      
      // search doc :
      
      // multi_match :
      ESQueryMessage queryMessage = new ESQueryMessage();
      multi_match multiMatch = new multi_match();
      queryMessage.setQuery(multiMatch);
      multiMatch.setQuery("file");
      multiMatch.setFields(Arrays.asList("name", "protocol"));
      SearchResult searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertNotNull(searchRes);
      assertNotNull(searchRes.getHits());
      assertEquals(1, searchRes.getHits().getTotal());
      Hit firstHit = searchRes.getHits().getHits().get(0);
      assertEquals(index, firstHit.get_index());// found in file field
      assertEquals(docId, firstHit.get_id());
      assertEquals("cv_johndoe.doc", firstHit.get_source().getProperties().get("name"));

      // file name letter tokenizing thanks to word_delimiter :
      multiMatch.setQuery("cv_johndoe.doc");
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      multiMatch.setQuery("cv_johndoe");
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      multiMatch.setQuery("johndoe"); // thanks to word_delimiter
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      multiMatch.setFields(Arrays.asList("name_default"));
      multiMatch.setQuery("cv_johndoe.doc");
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      multiMatch.setQuery("cv_johndoe");
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(0, searchRes.getHits().getTotal()); // no word_delimiter
      
      // no asciifolding (yet) :
      multiMatch.setQuery("johndôe"); // thanks to word_delimiter
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(0, searchRes.getHits().getTotal());

      // stopwords (TODO rm, rather through TF/IDF) :
      multiMatch.setFields(Arrays.asList("content.content"));
      multiMatch.setQuery("and");
      //multiMatch.setQuery("et"); // TODO fr
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(0, searchRes.getHits().getTotal()); // and et stopword

      // no stemming (?) :
      multiMatch.setQuery("experiences");
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      multiMatch.setQuery("experience");
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(0, searchRes.getHits().getTotal());
      
      // terms :
      terms terms = new terms();
      terms.setFieldToTermListOrLookupMap(new LinkedHashMap<>());
      terms.getFieldToTermListOrLookupMap().put("content.content", Arrays.asList("phone"));
      queryMessage.setQuery(terms);
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      
      // shingles :
      terms.getFieldToTermListOrLookupMap().put("content.content", Arrays.asList("i phone"));
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      terms.getFieldToTermListOrLookupMap().put("content.content", Arrays.asList("i developer"));
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(0, searchRes.getHits().getTotal());

      // number :
      terms.getFieldToTermListOrLookupMap().clear();
      terms.getFieldToTermListOrLookupMap().put("content.length", Arrays.asList(1234123));
      queryMessage.setQuery(terms);
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      
      // french :
      multiMatch.setFields(Arrays.asList("content.content_fr"));
      multiMatch.setQuery("expériences");
      queryMessage.setQuery(multiMatch);
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      // french stemming :
      multiMatch.setQuery("expérience");
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      // french elision :
      multiMatch.setQuery("l");
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(0, searchRes.getHits().getTotal());
      // french stopwords :
      multiMatch.setQuery("et");
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(0, searchRes.getHits().getTotal());

      // bool - must / AND :
      bool bool = new bool();
      queryMessage.setQuery(bool);
      multiMatch.setQuery("phone");
      multi_match multiMatch2 = new multi_match();
      multiMatch2.setQuery("smartphone");
      multiMatch2.setFields(Arrays.asList("content.content"));
      bool.setMust(Arrays.asList(multiMatch, multiMatch2));
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(0, searchRes.getHits().getTotal());
      
      // query-time synonyms (manual, not ES', still requires dict, & for acronyms), bool should / OR, boost :
      bool.setMust(null);
      bool.setShould(Arrays.asList(multiMatch, multiMatch2));
      //bool.setBoost(boost);
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      
      // TODO on path (prefix & keyword), see PcuSearchApiServerImplTest
      
      // native Lucene query :
      query_string nativeQuery = new query_string();
      queryMessage.setQuery(nativeQuery);
      nativeQuery.setQuery("+content.content:\"i phone\"^2");
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      
      // explain :
      queryMessage.setExplain(true);
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      assertTrue(searchRes.getHits().getHits().get(0).get_explanation().toString().contains("description"));
      queryMessage.setExplain(false);
      
      // filter_path :
      searchRes = es.searchInType(index, type, queryMessage, null, null, "took, hits.hits._id");
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      assertNull(searchRes.getHits().getHits().get(0).get_index());
      
      // sort :
      queryMessage.setSort(new LinkedHashMap<>());
      queryMessage.getSort().put("name", "asc");
      queryMessage.getSort().put("_score", "desc");
      Hit hit = searchRes.getHits().getHits().get(0);
      assertEquals(docId, hit.get_id());
      queryMessage.setSort(null);
      
      // function score - script :
      ESScript esScript = new ESScript();
      ESScriptScript esScriptScript = esScript.getScript();
      function_score function_score = new function_score();
      queryMessage.setQuery(function_score);
      function_score.setScript_score(esScript);
      esScriptScript.setInline("Math.log(2 + doc['content.length'].value)");
      float previousScore = searchRes.getHits().getHits().get(0).get_score();
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      assertNotEquals(previousScore, searchRes.getHits().getHits().get(0).get_score());
      
      // function score - field value factor AND gaussian decay :
      //function_score = new function_score(); // reset (null not enough)
      //queryMessage.setQuery(function_score);
      function_score.setScript_score(null);
      function_score.setFunctions(new ArrayList<>());
      FunctionScoreFilter field_value_factor_filter = new FunctionScoreFilter();
      function_score.getFunctions().add(field_value_factor_filter);
      FieldValueFactorFunctionScore field_value_factor = new FieldValueFactorFunctionScore();
      field_value_factor_filter.setField_value_factor(field_value_factor);
      field_value_factor.setField("content.length");
      FunctionScoreFilter gaussian_decay_filter = new FunctionScoreFilter();
      function_score.getFunctions().add(gaussian_decay_filter);
      gaussian_decay_filter.setGauss(new LinkedHashMap<>());
      DecayFunctionScoreFieldParameters decayFunctionScoreField = new DecayFunctionScoreFieldParameters();
      gaussian_decay_filter.getGauss().put("content.length", decayFunctionScoreField);
      decayFunctionScoreField.setOrigin("0");
      decayFunctionScoreField.setScale("20");
      previousScore = searchRes.getHits().getHits().get(0).get_score();
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      assertNotEquals(previousScore, searchRes.getHits().getHits().get(0).get_score());
      
      // script_field :
      esScriptScript.setInline("doc['content.length'].value * params.multiplier");
      esScriptScript.setParams(new LinkedHashMap<>());
      esScriptScript.getParams().put("multiplier", 2);
      queryMessage.setScript_fields(new LinkedHashMap<>());
      queryMessage.getScript_fields().put("content.length_doubled", esScript);
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      assertNull(searchRes.getHits().getHits().get(0).get_source());
      assertEquals(Arrays.asList(2468246), // TODO Q why list ?
            searchRes.getHits().getHits().get(0).getFields().get("content.length_doubled"));
      queryMessage.setScript_fields(null);
      
      // script :
      script script = new script();
      queryMessage.setQuery(script);
      script.setScript(esScriptScript);
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      hit = searchRes.getHits().getHits().get(0);
      assertEquals(docId, hit.get_id());
      
      // error (script) :
      esScriptScript.setInline("doc['nofield'].value * params.multiplier");
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertTrue(searchRes.get_shards().getFailures().get(0).getReason().getCaused_by().getReason().contains("No field found"));
      
      // highlight :
      queryMessage.setQuery(bool);
      Highlight highlight = new Highlight();
      highlight.setFields(new LinkedHashMap<>());
      HighlightParameters contentContentHighlightParameters = new HighlightParameters();
      highlight.getFields().put("content.content", contentContentHighlightParameters);
      queryMessage.setHighlight(highlight);
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      //assertTrue(searchRes.getHits().getHits().get(0)
      //      .getHighlight().get("content.content").get(0).toLowerCase().contains("phone")); // NOO none TODO Q why ???
      
      // with rescore :
      contentContentHighlightParameters.setHighlight_query(nativeQuery);
      queryMessage.setRescore(new ArrayList<>());
      ESRescore rescore = new ESRescore();
      queryMessage.getRescore().add(rescore);
      rescore.setWindow_size(50);
      rescore.getQuery().setRescore_query(bool);
      rescore.getQuery().setQuery_weight(0.7f);
      rescore.getQuery().setRescore_query_weight(0.7f);
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals(docId, searchRes.getHits().getHits().get(0).get_id());
      assertTrue(searchRes.getHits().getHits().get(0)
            .getHighlight().get("content.content").get(0).toLowerCase().contains("phone"));

      // similar (more like this) :
      String type = "file";
      queryMessage = new ESQueryMessage(); // removes highlighting & rescore
      more_like_this more_like_this = new more_like_this();
      List<QueryDocument> like = new ArrayList<QueryDocument>();
      more_like_this.setMin_doc_freq(1); // else not found in only 1 (another) indexed doc (unless indexing 30 docs)
      more_like_this.setMin_term_freq(1); // else not found in only 1 (another) indexed doc (unless indexing 30 docs)
      more_like_this.setLike(like);
      more_like_this.setFields(new ArrayList<String>(3));
      more_like_this.getFields().add("content.content");
      QueryDocument likeQueryDocument = new QueryDocument();
      like.add(likeQueryDocument);
      likeQueryDocument.set_index(index);
      likeQueryDocument.set_type(type);
      likeQueryDocument.set_id(docId);
      queryMessage.setQuery(more_like_this);
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertEquals("there should be no other document like this one because the index only contains this one", 0, searchRes.getHits().getTotal());
      
      // with another, similar doc having been indexed :
      ///for (int i = 0; i < 5 ; i++) { // only 10 returns none, 30 returns up to 28 !?!
      String id = new UUID().toString(); // best lucene id http://blog.mikemccandless.com/2014/05/choosing-fast-unique-identifier-uuid.html
      Document doc = es.searchInType(index, type, new ESQueryMessage(), null, null).getHits().getHits().get(0).get_source();
      IndexResult indexRes = es.indexDocument(index, type, id, doc, null, null, null, null, null, null, "wait_for"); // refresh=wait_for else not found,
      // because indexing is not synchronous (BUT works elsewhere) https://github.com/elastic/elasticsearch/pull/17986
      assertNotNull(indexRes);
      assertEquals(id, indexRes.get_id());
      ///}
      searchRes = es.searchInType(index, type, queryMessage, null, null);
      assertNotEquals(0, searchRes.getHits().getTotal());
      assertEquals(id, searchRes.getHits().getHits().get(0).get_id());
   }
   
}

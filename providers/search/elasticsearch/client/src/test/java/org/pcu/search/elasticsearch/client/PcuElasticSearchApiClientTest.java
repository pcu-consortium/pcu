package org.pcu.search.elasticsearch.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pcu.search.elasticsearch.PcuElasticSearchClientApplication;
import org.pcu.search.elasticsearch.api.Document;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.ElasticSearchApi;
import org.pcu.search.elasticsearch.api.GetResult;
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
import org.pcu.search.elasticsearch.api.query.Hit;
import org.pcu.search.elasticsearch.api.query.SearchResult;
import org.pcu.search.elasticsearch.api.query.clause.multi_match;
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
 * WARNING requires ElasticSearch 5.5 to have been started independently
 * TODO auto provision ES
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
   protected ElasticSearchApi es;

   protected String index = "files";
   protected String docId;
   
   
   protected PutMappingResult cleanAndSetupIndex(String index, IndexMapping indexMapping) throws ESApiException {
      // clean :
      try {
         es.deleteMapping(index); // TODO deleteIndex
      } catch(ESApiException esex) {
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
      analysis.setFilter(new LinkedHashMap<String,TokenFilter>());
      TokenFilter elision_fr = new TokenFilter();
      analysis.getFilter().put("elision_fr", elision_fr);
      elision_fr.setType("elision");
      ///elision_fr.setArticles(Arrays.asList("l", "m", "t", "qu", "n", "s", "j"));
      analysis.setAnalyzer(new LinkedHashMap<String,Analyzer>());
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
      // french conf :
      // taken from example https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-lang-analyzer.html
      Analyzer fileContentFrAnalyzer = new Analyzer(); // redefine fr analyzer to add shingles
      analysis.getAnalyzer().put("fileContentFrAnalyzer", fileContentFrAnalyzer);
      fileContentFrAnalyzer.setTokenizer("standard");
      fileContentFrAnalyzer.setFilter(new ArrayList<String>());
      fileContentFrAnalyzer.getFilter().add("standard"); // noop
      /*fileContentFrAnalyzer.getFilter().add("french_elision");
      fileContentFrAnalyzer.getFilter().add("lowercase");
      fileContentFrAnalyzer.getFilter().add("french_stop");
      //fileContentFrAnalyzer.getFilter().add("french_keywords"); // stemmer excluder
      fileContentFrAnalyzer.getFilter().add("french_stemmer");*/
      fileContentFrAnalyzer.getFilter().add("shingle");
      
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
      fileContentMapping.setFields(new LinkedHashMap<>());
      PropertyMapping fileContentFrPhoneticMapping = new PropertyMapping();
      fileContentMapping.getFields().put("phonetic", fileContentFrPhoneticMapping );
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
      docId = getRes.get_id();
      
      Document foundDoc = es.getDocumentSource(index, type, id, null, null, null, null, null, null, null);
      assertNotNull(foundDoc);
      assertEquals("slash should be supported in id even not at the end of the operation URL",
            "cv_johndoe.doc", foundDoc.getProperties().get("name"));
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
      SearchResult searchRes = es.search(queryMessage , null, null);
      assertNotNull(searchRes);
      assertNotNull(searchRes.getHits());
      assertEquals(1, searchRes.getHits().getTotal());
      Hit firstHit = searchRes.getHits().getHits().iterator().next();
      assertEquals(index, firstHit.get_index());// found in file field
      assertEquals(docId, firstHit.get_id());
      assertEquals("cv_johndoe.doc", firstHit.get_source().getProperties().get("name"));

      // file name letter tokenizing thanks to word_delimiter :
      multiMatch.setQuery("cv_johndoe.doc");
      searchRes = es.search(queryMessage , null, null);
      assertEquals(docId, searchRes.getHits().getHits().iterator().next().get_id());
      multiMatch.setQuery("cv_johndoe");
      searchRes = es.search(queryMessage , null, null);
      assertEquals(docId, searchRes.getHits().getHits().iterator().next().get_id());
      multiMatch.setQuery("johndoe"); // thanks to word_delimiter
      searchRes = es.search(queryMessage , null, null);
      assertEquals(docId, searchRes.getHits().getHits().iterator().next().get_id());
      multiMatch.setFields(Arrays.asList("name_default"));
      multiMatch.setQuery("cv_johndoe.doc");
      searchRes = es.search(queryMessage , null, null);
      assertEquals(docId, searchRes.getHits().getHits().iterator().next().get_id());
      multiMatch.setQuery("cv_johndoe");
      searchRes = es.search(queryMessage , null, null);
      assertEquals(0, searchRes.getHits().getTotal()); // no word_delimiter
      
      // no asciifolding (yet) :
      multiMatch.setQuery("johndôe"); // thanks to word_delimiter
      searchRes = es.search(queryMessage , null, null);
      assertEquals(0, searchRes.getHits().getTotal());

      // stopwords :
      multiMatch.setFields(Arrays.asList("content.content"));
      multiMatch.setQuery("and");
      //multiMatch.setQuery("et"); // TODO fr
      searchRes = es.search(queryMessage , null, null);
      assertEquals(0, searchRes.getHits().getTotal()); // and et stopword

      // no stemming (?) :
      multiMatch.setQuery("experiences");
      searchRes = es.search(queryMessage , null, null);
      assertEquals(docId, searchRes.getHits().getHits().iterator().next().get_id());
      multiMatch.setQuery("experience");
      searchRes = es.search(queryMessage , null, null);
      assertEquals(0, searchRes.getHits().getTotal());
      
      // terms :
      terms terms = new terms();
      terms.setFieldToTermListOrLookupMap(new LinkedHashMap<>());
      terms.getFieldToTermListOrLookupMap().put("content.content", Arrays.asList("phone"));
      queryMessage.setQuery(terms);
      searchRes = es.search(queryMessage , null, null);
      assertEquals(docId, searchRes.getHits().getHits().iterator().next().get_id());
      
      // shingles :
      terms.getFieldToTermListOrLookupMap().put("content.content", Arrays.asList("i phone"));
      searchRes = es.search(queryMessage , null, null);
      assertEquals(docId, searchRes.getHits().getHits().iterator().next().get_id());
      terms.getFieldToTermListOrLookupMap().put("content.content", Arrays.asList("i developer"));
      searchRes = es.search(queryMessage , null, null);
      assertEquals(0, searchRes.getHits().getTotal());

      // number :
      terms.getFieldToTermListOrLookupMap().clear();
      terms.getFieldToTermListOrLookupMap().put("content.length", Arrays.asList(1234123));
      queryMessage.setQuery(terms);
      searchRes = es.search(queryMessage , null, null);
      assertEquals(docId, searchRes.getHits().getHits().iterator().next().get_id());
      
      // french :
      multiMatch.setFields(Arrays.asList("content.content_fr"));
      multiMatch.setQuery("expériences");
      queryMessage.setQuery(multiMatch);
      searchRes = es.search(queryMessage , null, null);
      assertEquals(docId, searchRes.getHits().getHits().iterator().next().get_id());
      // french stemming :
      multiMatch.setQuery("expérience");
      searchRes = es.search(queryMessage , null, null);
      assertEquals(docId, searchRes.getHits().getHits().iterator().next().get_id());
      // french elision :
      multiMatch.setQuery("l");
      searchRes = es.search(queryMessage , null, null);
      assertEquals(0, searchRes.getHits().getTotal());
      // french stopwords :
      multiMatch.setQuery("et");
      searchRes = es.search(queryMessage , null, null);
      assertEquals(0, searchRes.getHits().getTotal());
   }
   
}

package org.pcu.features.connector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.pcu.features.configuration.api.GetConfigurationResult;
import org.pcu.features.configuration.api.PcuComponent;
import org.pcu.features.configuration.api.PcuConfiguration;
import org.pcu.features.configuration.api.PcuConfigurationApi;
import org.pcu.features.connector.crawler.AvroDrivenJDBCCrawlBatch;
import org.pcu.features.connector.crawler.CrawlBatch;
import org.pcu.features.connector.crawler.CrawlCompletedException;
import org.pcu.features.connector.crawler.CrawlUtils;
import org.pcu.features.connector.crawler.Crawler;
import org.pcu.features.connector.crawler.Crawler2;
import org.pcu.features.connector.crawler.FileCrawlBatch;
import org.pcu.features.connector.crawler.FileCrawler;
import org.pcu.platform.model.ModelServiceImpl;
import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.metadata.api.PcuMetadataApi;
import org.pcu.providers.search.api.PcuSearchApi;
import org.pcu.providers.search.api.PcuSearchEsClientApi;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.mapping.IndexResult;
import org.pcu.search.elasticsearch.api.query.ESQueryMessage;
import org.pcu.search.elasticsearch.api.query.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

@Component
public class PcuConnector implements ApplicationListener<ApplicationReadyEvent> {

   protected static final Logger log = LoggerFactory.getLogger(PcuConnector.class);
   
   //@Autowired @Qualifier("pcuSearchEsApiRestClient") //@Qualifier("pcuSearchEsApiImpl") //pcuSearchEsApiRestClient
   @Resource(name="pcuSearchEsApiRestClient") // NOT @Autowired else NoSuchBean because proxy has not the same class https://stackoverflow.com/questions/15614786/could-not-autowire-jaxrs-client
   private PcuSearchEsClientApi searchEsApi;
   //@Autowired @Qualifier("pcuSearchApiRestClient") //@Qualifier("pcuSearchApiImpl") //pcuSearchApiRestClient
   @Resource(name="pcuSearchApiRestClient") // NOT @Autowired else NoSuchBean because proxy has not the same class https://stackoverflow.com/questions/15614786/could-not-autowire-jaxrs-client
   private PcuSearchApi searchApi;
   //@Autowired @Qualifier("defaultFileProviderApi") //defaultFileProviderApi LocalFileProviderApiImpl pcuFileApiRestClient
   @Resource(name="pcuFileApiRestClient") // NOT @Autowired else NoSuchBean because proxy has not the same class https://stackoverflow.com/questions/15614786/could-not-autowire-jaxrs-client
   private PcuFileApi fileApi;
   ///@Resource(name="pcuConnectorApiRestClient") // NOT @Autowired else NoSuchBean because proxy has not the same class https://stackoverflow.com/questions/15614786/could-not-autowire-jaxrs-client
   ///private PcuConnectorApi pcuConnectorApi;
   @Resource(name="pcuConfigurationApiRestClient") // NOT @Autowired else NoSuchBean because proxy has not the same class https://stackoverflow.com/questions/15614786/could-not-autowire-jaxrs-client
   private PcuConfigurationApi pcuConfigurationApi;
   @Autowired
   private ModelServiceImpl modelService;
   @Autowired @Qualifier("defaultMetadataExtractorApi") // possibly defaultMetadataExtractorRestClient ?
   private PcuMetadataApi metadataExtractorApi;
   
   // static configuration :
   @Value("${pcu.connector.runAtStartup:false}")
   private boolean runAtStartup;
   /** id of connector template or empty if default one */
   @Value("${pcu.connector.templateId:connector-default-template}")
   private String templateId;
   /** id of connector instance (if empty will use MAC address), & for logging purpose */
   @Value("${pcu.connector.confId:}")
   private String confId;

   // Component (dynamic) configuration : (TODO move in PcuPlatformRestClientConfiguration)
   private ObjectMapper yamlConfMapper = new ObjectMapper(new YAMLFactory()).enableDefaultTyping();
   @Autowired @Qualifier("pcuApiTypedMapper")
   private ObjectMapper pcuApiTypedMapper;
   /** constant */
   public final static String confType = "connector";
   /** conf save, gotten from server */
   private PcuConfiguration conf;
   /** constant */
   public final static String hardcodedDefaultConfId = "connector-default";
   /** conf'd */
   private List<Crawler2> crawlers = new ArrayList<Crawler2>();

   // services / helpers useful to all crawlers :
   private String connectorComputerHostName;
   /** if not configured in ((poller.)yaml) conf, uses best MAC address */
   private String connectorComputerId = null;
   protected TimeBasedGenerator uuidGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());

   @Autowired
   private PathMatchingResourcePatternResolver resourceLoader;
   
   /** @obsolete default crawler */
   private Crawler<?> crawler = null;

   @Override
   public void onApplicationEvent(ApplicationReadyEvent event) {
      try {
         // bootstrap conf TODO move server side :
         // here from path, OR TODO Q by being injected in modelService ?
         bootstrapTemplates(); // TODO and all configuration
         
         setup();
         
      } catch (RuntimeException rex) {
         throw rex;
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
      
      // run at startup by default (not in test profile) :
      // NOT in @PostConstruct else embedded connector blocks deployment of REST API server
      // and fails with ConnectException : Connection refused !
      if (runAtStartup) {
         defaultCrawl();
      }
   }
   
   public void setup() throws ESApiException, IOException {
      
      // (instanciate or) get conf from server, TODO LATER reload conf from server
      if (confId == null || confId.isEmpty()) {
         // try auto / "implicit" connector id as computer id out of MAC address :
         confId = CrawlUtils.macAddress(); // TODO check that no other connector on this computer ??
         log.warn("No configured connectorId found, implicitly using computer id instead " + confId);
      }
      //connectorConf = pcuConnectorApi.get(connectorTemplateId);
      if ((this.conf = lookupConf(confId)) == null) {
         // if no conf yet, create it from template :
         //connectorConf = pcuConnectorApi.createFromTemplate(connectorTemplateId);
         if ((this.conf = lookupConf(templateId)) == null) {
            String msg = "Configuration template " + templateId + " does not exist";
            log.error(msg);
            ///throw new RuntimeException(msg);

            this.conf = hardcodedDefaultConf();
            this.confId = hardcodedDefaultConfId;
            
            log.error("Using hardcoded default conf, save it to template file " + "bootstrap/" + this.confId + ".yaml"
                  + " to avoid this message next time:\n" + yamlConfMapper.writeValueAsString(this.conf));
            
         } else {
            IndexResult createRes = pcuConfigurationApi.put(confType, confId, this.conf, null, null, null);
            if (!createRes.isCreated()) {
               log.error("Failed to create connector configuration from template " + templateId
                     + ", using template as temporary configuration anyway");
            }
         }
      }
      
      initFromConf();
      
      // TODO load default crawler from conf
      Crawler<?> crawler1 = loadFileCrawler1FromYamlConf("classpath*:bootstrap/poller.yaml.old"); // TODO remove
      // TODO schedule jobs from conf

      // setup (of services useful to all crawlers) specific to this connector instance :
      try {
         if (this.connectorComputerHostName == null) {
            this.setConnectorComputerHostName(CrawlUtils.hostName()); // or IP by getCanonicalHostName(), or both ?
            log.info("Using autodetected connectorComputerHostName (readable host name): " + this.getConnectorComputerHostName());
         }
         if (this.connectorComputerId == null) {
            this.setConnectorComputerId(CrawlUtils.macAddress());
            log.warn("connectorComputerId (id of crawled filesystem) not configured (should be in production), "
                  + "using autodetected base64'd MAC address: " + this.getConnectorComputerId());
         }
      } catch (IOException ioex) {
         throw new RuntimeException("Error initing connector " + this, ioex);
      }
   }

   public PcuConfiguration lookupConf(String confId) {
      GetConfigurationResult pcuConfRes = null;
      try {
         pcuConfRes = pcuConfigurationApi.get(confType, confId, null);
      } catch (ESApiException esapiex) {
         if (esapiex.getStatus() != 404) {
            throw esapiex;
         }
      }
      if (pcuConfRes != null && pcuConfRes.isFound()) { // checking isFound() not required, 404 ESApiException instead of false
         ///List<Crawler2> crawlers = yamlConfMapper.readValue(jsonArray, new TypeReference<List<Crawler2>>(){});
         return pcuConfRes.get_source();
      }
      return null;
   }
   
   /**
    * 
    * @param confId logging purpose only
    * @throws RuntimeException if error initing component from found conf
    */
   private void initFromConf() throws RuntimeException {
      List<PcuComponent> foundComponents = this.conf.getComponents();
      this.crawlers = foundComponents.stream()
            .filter(cpt -> cpt instanceof Crawler2).map(cpt -> (Crawler2) cpt).collect(Collectors.toList());
      if (this.crawlers.size() != foundComponents.size()) {
         log.warn("Configuration " + confId
               + " contains components of unknown types : " + foundComponents);
      }
      if (this.crawlers.isEmpty()) {
         String msg = "Found configuration " + confId
               + " but no crawler components inside, try deleting it or setting another connectorId";
         log.error(msg);
         throw new RuntimeException(msg); // abort setup ; TODO better
      }
      
      // init access to services :
      // TODO LATER connector.getBean(name) providing access to any service specific to a crawlBatch,
      // ex. UrlService providing urlBloomFilter to UrlCrawlBatch
      // (beanFactory.autowireBean(crawlBatch) would be comparatively costlier)
      for (Crawler2 crawler : this.crawlers) {
         crawler.setConnector(this);
         for (CrawlBatch crawlBatch : crawler.getToBeCrawledQeue()) {
            crawlBatch.setCrawler(crawler);
         }
      }
   }

   /** TODO rather pluggable so new crawlBatch implementations can contribute their own examples ? */
   private PcuConfiguration hardcodedDefaultConf() {
      crawlers.add(buildDefaultSampleFileCrawler(this).getCrawler());
      crawlers.add(buildDefaultPersonAvroDrivenJDBCCrawler(this).getCrawler());
      
      return new PcuConfiguration(crawlers.toArray(new PcuComponent[crawlers.size()]));
   }
   /**
    * public for tests
    * @return FileCrawlBatch with a dedicated crawler
    */
   public static FileCrawlBatch buildDefaultSampleFileCrawler(PcuConnector connector) {
      Crawler2 fileAndUrlCrawler = new Crawler2();
      fileAndUrlCrawler.setConnector(connector);
      fileAndUrlCrawler.setIndex("files");
      fileAndUrlCrawler.setType("file");
      fileAndUrlCrawler.setContentStore("fileCrawlerStore");
      FileCrawlBatch sampleFileCrawlBatch = new FileCrawlBatch(new File("sample"), fileAndUrlCrawler);
      fileAndUrlCrawler.getToBeCrawledQeue().add(sampleFileCrawlBatch);
      return sampleFileCrawlBatch;
   }
   /**
    * public for tests
    * @return AvroDrivenJDBCCrawlBatch with a dedicated crawler
    */
   public static AvroDrivenJDBCCrawlBatch buildDefaultPersonAvroDrivenJDBCCrawler(PcuConnector connector) {
      Crawler2 personCrawler = new Crawler2();
      personCrawler.setConnector(connector);
      personCrawler.setIndex("persons");
      //fileAndUrlCrawler.setType("file"); // will be taken from avro schema name
      personCrawler.setContentStore("personCrawlerStore");
      AvroDrivenJDBCCrawlBatch personJdbcCrawlBatch = new AvroDrivenJDBCCrawlBatch(personCrawler);
      /*HikariDataSource ds = new HikariDataSource();
      ds.setJdbcUrl("jdbc:h2:mem:jdbcConnectorTest;DATABASE_TO_UPPER=false;MODE=PostgreSQL;DB_CLOSE_ON_EXIT=FALSE\";DB_CLOSE_DELAY=-1");
      ds.setUsername("h2"); // TODO factory 
      JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
      personJdbcCrawlBatch.setJdbcTemplate(jdbcTemplate);*/
      LinkedHashMap<String, Object> jdbcProperties = new LinkedHashMap<String,Object>();
      jdbcProperties.put("jdbcUrl", "jdbc:h2:mem:jdbcConnectorTest;DATABASE_TO_UPPER=false;MODE=PostgreSQL;DB_CLOSE_ON_EXIT=FALSE\";DB_CLOSE_DELAY=-1");
      jdbcProperties.put("username", "h2");
      personJdbcCrawlBatch.setJdbcProperties(jdbcProperties);
      /*Resource personSchemaResource = resourceLoader.getResource("classpath:bootstrap/avro/person.avsc");
      Schema personSchema = new Schema.Parser().parse(personSchemaResource.getInputStream()) // TODO also upload to modelService and as mapping to ElasticSearch
            .getTypes().get(0); // TODO or single one ? (but then fails in modelService)
      personJdbcCrawlBatch.setAvroSchema(personSchema);*/
      personJdbcCrawlBatch.setAvroSchemaName("person");
      personJdbcCrawlBatch.setQuerySelect("select * from person");
      personJdbcCrawlBatch.setIdFieldName("id");
      personJdbcCrawlBatch.setBatchSize(2); // to be able to test batching with 3 items
      personCrawler.getToBeCrawledQeue().add(personJdbcCrawlBatch);
      return personJdbcCrawlBatch;
   }
   

   // ConfigurationManager :
   public static String CONF_INDEX = "configurations";
   /** set to true to force bootstrap */
   @Value("${pcu.forceBootstrap:false}")
   private boolean forceBootstrap;
   /**
    * bootstrap conf
    * TODO TODO move to ConfigurationService (or ModelService ??)
    * on server side (or ensure that it's not uploaded by all connectors)
    * @throws ESApiException
    */
   private void bootstrapTemplates() throws ESApiException {
      String confType = this.confType; // = null; // TODO LATER for each component or all at once ?
      
      if (!forceBootstrap) {
         // TODO LATER version management (keep ALL versions)
         ESQueryMessage checkTemplatesExistQM = new ESQueryMessage();
         checkTemplatesExistQM.setSize(1); // enough to check whether some exist
         try {
            SearchResult checkTemplatesExistRes = searchEsApi.searchInType(CONF_INDEX, confType, checkTemplatesExistQM, null, null, null);
            if (checkTemplatesExistRes.getHits().getTotal() > 0) {
               return; // don't bootstrap
            }
         } catch (ESApiException esex) {
            if (esex.getResponse().getStatus() != 404) {
               throw new RuntimeException("Unkown error checking if there are any conf templates yet of type " + confType, esex);
            } // TODO else no index mapping, allowing in order to generate index mapping from sample !!!!!
         }
      }

      String yamlConfPath = "classpath*:bootstrap/*.yaml";
      try {
         org.springframework.core.io.Resource[] confResources = resourceLoader.getResources(yamlConfPath); // resourceLoader.getResources("classpath*:bootstrap/crawl/*.json")
         if (confResources.length == 0) { // happens when executable jar
            confResources = resourceLoader.getResources(yamlConfPath.replace("classpath*:", "classpath:")); // resourceLoader.getResources("classpath*:bootstrap/crawl/*.json")
            if (confResources.length == 0) {
               log.error("Bootstrap required but no YAML conf found at " + yamlConfPath);
               // (not exploding to allow generating YAML from default hardcoded conf)
               return;
            }
         }
         
         /* TODO add PcuConfiguration.bulk()
         BulkMessage bulkMessage = new BulkMessage();
         List<BulkAction> bulkActions = new ArrayList<BulkAction>(confResources.length);
         bulkMessage.setActions(bulkActions);
         */
         
         for (org.springframework.core.io.Resource confResource : confResources) {
            String fileName = confResource.getFile().getName();
            String confId = fileName.substring(0, fileName.lastIndexOf('.')); // file name without .yaml suffix is used as id
            PcuConfiguration pcuConf;
            try {
               pcuConf = yamlConfMapper.readValue(confResource.getInputStream(), PcuConfiguration.class);

               /*
               BulkAction indexBulkAction = new BulkAction();
               indexBulkAction.setDoc(pcuConf);
               IndexAction indexAction = new IndexAction();
               indexAction.set_index(CONF_INDEX);
               indexAction.set_type(confType); // TODO LATER get from conf file name or directory ?
               indexAction.set_id(confId);
               indexBulkAction.getKindToAction().put("index", indexAction);
               bulkActions.add(indexBulkAction);
               */
               pcuConfigurationApi.put(confType, confId, pcuConf, null, null, null);
               
            } catch (JsonMappingException jmex) {
               throw new RuntimeException("Error reading bootstrap conf from java-typed YAML file "
                     + confResource.getFile().getAbsolutePath(), jmex);
            } catch (ESApiException ex) {
               // TODO Q which kind of errors ??
               log.error("unknown error indexing conf doc " + confId + " "
                     + IOUtils.toString((java.io.InputStream) ex.getResponse().getEntity()), ex);
            }
         }
         
         /*
         try {
            BulkResult bulkRes = searchEsApi.bulk(bulkMessage, null, null);
            // TODO filter bulkRes and try to reindex failed ones !? ex. bad avro schema
         } catch (ESApiException ex) {
            // TODO Q which kind of errors ??
            log.debug("error indexing conf docs " + bulkActions.stream()
               .map(a -> a.getKindToAction().values().iterator().next().get_id())
               .collect(Collectors.toList()), ex);
         }
         */
      } catch (Exception e) {
         log.error("Error bootstrapping confs", e);
      }
   }

   /**
    * @obsolete
    * @param yamlPollerConfPath
    * @return
    */
   public Crawler<?> loadFileCrawler1FromYamlConf(String yamlPollerConfPath) {
      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      Crawler<?> crawler = null;
      try {
         org.springframework.core.io.Resource[] pollerConfResources = resourceLoader.getResources(yamlPollerConfPath); // resourceLoader.getResources("classpath*:bootstrap/crawl/*.json")
         if (pollerConfResources.length == 0) { // happens when executable jar
            pollerConfResources = resourceLoader.getResources(yamlPollerConfPath.replace("classpath*:", "classpath:")); // resourceLoader.getResources("classpath*:bootstrap/crawl/*.json")
         }
         //crawler = mapper.readValue(pollerConfResources[0].getInputStream(), Crawler.class);
         crawler = mapper.readValue(pollerConfResources[0].getInputStream(), FileCrawler.class);
         ///crawler = new PcuConnectorImplTest().buildFileCrawler("files");
         /*crawler = new FileCrawler();
         crawler.setIndex("files");
         ((FileCrawler) crawler).setRoots(Arrays.asList(new String[] { System.getProperty("user.home") + "/Documents" }));*/
         
         crawler.setSearchEsApi(searchEsApi);
         crawler.setSearchApi(searchApi);
         crawler.setFileApi(fileApi);
         crawler.setMetadataExtractorApi(metadataExtractorApi);
      } catch (Exception e) {
         log.error("Error setting up crawler TODO");
      }
      return crawler;
   }
   
   
   
   @Scheduled(cron = "${pcu.connector.cron:0 0 3 * * *}")
   public void defaultCrawl() {
      // crawl job :
      crawl(crawlers);
   }
   
   /**
    * Crawling loop.
    * TODO within ThreadPoolExecutor so it can scale up to several threads as configured.
    * @param crawlers
    */
   public void crawl(List<Crawler2> crawlers) {
      log.warn("Starting crawling of " + crawlers);
      
      // TODO register crawl in REST API
      
      for (Crawler2 crawler : crawlers) {
         crawler.init();
      }
      
      try {
         for (;;) {
            try {
               for (Crawler2 crawler : crawlers) {
                  crawler.crawlIteration();
                  if (log.isInfoEnabled() && crawler.getCrawled() % 100 == 0) {
                     log.info(String.format("crawled %d, indexed %d, to be crawled %d, to be indexed %d", crawler.getCrawled(),
                           crawler.getIndexed(), crawler.getToBeCrawledQeue().size(), crawler.getToBeIndexedPcuDocuments().size()));
                  }
               }
            } catch (CrawlCompletedException e) {
               throw e;
            } catch (Exception e) {
               // TODO log
               log.error("crawling iteration error ", e);
            }
         }
      } catch (CrawlCompletedException e) {
         log.warn("Completed crawling of " + crawlers);
      }
   }

   
   //////////////////////////////////
   // getters for access within crawler & crawlBatches :
   
   public PcuSearchEsClientApi getSearchEsApi() {
      return searchEsApi;
   }
   public PcuSearchApi getSearchApi() {
      return searchApi;
   }
   public void setSearchApi(PcuSearchApi searchApi) {
      this.searchApi = searchApi;
   }
   public PcuFileApi getFileApi() {
      return fileApi;
   }
   public PcuConfigurationApi getPcuConfigurationApi() {
      return pcuConfigurationApi;
   }
   public ModelServiceImpl getModelService() {
      return modelService;
   }
   public PcuMetadataApi getMetadataExtractorApi() {
      return metadataExtractorApi;
   }
   
   public String getConnectorComputerHostName() {
      return connectorComputerHostName;
   }
   public void setConnectorComputerHostName(String connectorComputerHostName) {
      this.connectorComputerHostName = connectorComputerHostName;
   }
   public String getConnectorComputerId() {
      return connectorComputerId;
   }
   public void setConnectorComputerId(String connectorComputerId) {
      this.connectorComputerId = connectorComputerId;
   }
   public TimeBasedGenerator getUuidGenerator() {
      return this.uuidGenerator;
   }
   
   
   ////////////////////////////////////////////////
   // other accessors, mainly for tests

   public PcuConfiguration getConf() {
      return conf;
   }
   String getTemplateId() {
      return templateId;
   }
   public void setTemplateId(String templateId) {
      this.templateId = templateId;
   }
   public String getConfId() {
      return confId;
   }
   public void setConfId(String confId) {
      this.confId = confId;
   }
   public boolean isRunAtStartup() {
      return runAtStartup;
   }
   public void setRunAtStartup(boolean runAtStartup) {
      this.runAtStartup = runAtStartup;
   }
   public List<Crawler2> getCrawlers() {
      return crawlers;
   }
   public void setCrawlers(List<Crawler2> crawlers) {
      this.crawlers = crawlers;
   }
   
}

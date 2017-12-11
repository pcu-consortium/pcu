package org.pcu.features.connector;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.pcu.providers.file.api.PcuFileApi;
import org.pcu.providers.metadata.api.PcuMetadataApi;
import org.pcu.providers.search.api.PcuSearchApi;
import org.pcu.providers.search.api.PcuSearchEsClientApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@Component
public class PcuConnector {

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
   @Autowired @Qualifier("defaultMetadataExtractorApi") // possibly defaultMetadataExtractorRestClient ?
   private PcuMetadataApi metadataExtractorApi;
   
   @Value("${pcu.connector.runAtStartup:false}")
   private boolean runAtStartup;

   //@Autowired
   //private ResourceLoader resourceLoader;
   private PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver(); // TODO @Bean
   /** default crawler */
   private Crawler<?> crawler = null;
   
   @PostConstruct
   public void init() {
      // TODO get conf from server
      
      // TODO load default crawler from conf
      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      try {
         org.springframework.core.io.Resource[] pollerConfResources = resourceLoader.getResources("classpath*:bootstrap/poller.yaml"); // resourceLoader.getResources("classpath*:bootstrap/crawl/*.json")
         if (pollerConfResources.length == 0) { // happens when executable jar
            pollerConfResources = resourceLoader.getResources("classpath:bootstrap/poller.yaml"); // resourceLoader.getResources("classpath*:bootstrap/crawl/*.json")
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

      // TODO schedule jobs from conf
      
      // run at startup by default (not in test profile) :
      if (runAtStartup) {
         defaultCrawl();
      }
   }
   
   @Scheduled(cron = "${pcu.connector.cron:0 0 3 * * *}")
   public void defaultCrawl() {
      // crawl job :
      if (crawler != null) {
         crawl(crawler);
      }
   }
   
   public void crawl(Crawler<?> crawler) {
      log.warn("Starting crawling of " + crawler);
      // TODO register crawl in REST API
      crawler.init();
      try {
         for (;;) {
            try {
               crawler.crawlIteration();
               if (log.isInfoEnabled() && crawler.getCrawled() % 100 == 0) {
                  log.info(String.format("crawled %d, indexed %d, to be crawled %d, to be indexed %d", crawler.getCrawled(),
                        crawler.getIndexed(), crawler.getToBeCrawledQeue().size(), crawler.getToBeIndexedPcuDocuments().size()));
               }
            } catch (CrawlCompletedException e) {
               throw e;
            } catch (Exception e) {
               // TODO log
               log.error("crawling iteration error ", e);
            }
         }
      } catch (CrawlCompletedException e) {
         log.warn("Completed crawling of " + crawler);
      }
   }
   
}

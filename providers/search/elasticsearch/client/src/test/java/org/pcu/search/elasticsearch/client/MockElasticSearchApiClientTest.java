package org.pcu.search.elasticsearch.client;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.junit.runner.RunWith;
import org.pcu.search.elasticsearch.PcuElasticSearchClientApplication;
import org.pcu.search.elasticsearch.api.ESApiException;
import org.pcu.search.elasticsearch.api.ElasticSearchApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;


/**
 * Test of API operations and client with mock server
 * 
 * @author mdutoo
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes={PcuElasticSearchClientApplication.class,MockElasticSearchApiClientTest.MockConfiguration.class},
   initializers = ConfigFileApplicationContextInitializer.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.DEFINED_PORT, properties="server_port=45665")
// rather than @SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, properties="server_port=45665")
// which would require listening to an ApplicationEvent and therefore using a Provider pattern
// see https://docs.spring.io/spring-boot/docs/current/reference/html/howto-embedded-servlet-containers.html https://stackoverflow.com/questions/30312058/spring-boot-how-to-get-the-running-port
// or with autoconf redefine cxf.jaxrs.client.address
@ActiveProfiles("test")
public class MockElasticSearchApiClientTest extends PcuElasticSearchApiClientTest {
   
   public static final String PCU_ES_API_MOCK_PATH = "/search/elasticsearch/";
   
   /** NB. reusing client conf (bus, @Import cxf.xml) */
   public static class MockConfiguration {
      @Bean
      public ServletRegistrationBean dispatcherServlet() {
          return new ServletRegistrationBean(new CXFServlet(), "/*");
      }
      // NB. reusing client bus 
      @Bean
      public Server mockRsServer(SpringBus bus, JacksonJsonProvider elasticSearchProvider) {
          JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
          endpoint.setBus(bus);
          endpoint.setAddress(PCU_ES_API_MOCK_PATH);
          endpoint.setServiceBean(new ElasticSearchApiMockImpl());
          endpoint.setProvider(elasticSearchProvider); // else web app ex Response.Status.UNSUPPORTED_MEDIA_TYPE

          String messagesFilePath = new File("es-rest-mock.log").toURI().toString(); // if local in dev, is in /server
          LoggingFeature loggingFeature = new LoggingFeature(messagesFilePath, messagesFilePath, -1); // in, out, limit (else 50kb)
          loggingFeature.setPrettyLogging(true);
          endpoint.getFeatures().add(loggingFeature);
          
          return endpoint.create();
      }
   }

   // should be magical proxy (no impl in -client)
   //@Autowired @Qualifier("mockRsClient") // client created NOT IN JAVA CONF else randomServerPort not inited
   ///private ElasticSearchApi es;

   /** to build URL for client (in case not default 8080 ex. because random port) */
   @LocalServerPort
   protected int randomServerPort;
   /*@Autowired
   protected SpringBus bus;
   @Autowired
   protected JacksonJsonProvider jacksonJsonProvider;
   
   @PostConstruct
   public void init() {
      // creating client : (NOT IN JAVA CONF else randomServerPort not inited)
      JAXRSClientFactoryBean client = new JAXRSClientFactoryBean();
      client.setBus(bus);
      client.setAddress("http://localhost:" + randomServerPort + PCU_ES_API_MOCK_PATH); // not default 8080 client since random port
      client.setServiceClass(ElasticSearchApi.class);
      client.setProvider(jacksonJsonProvider); // else web app ex Response.Status.UNSUPPORTED_MEDIA_TYPE

      String messagesFilePath = new File("es-rest-mock.log").toURI().toString(); // if local in dev, is in /server
      LoggingFeature loggingFeature = new LoggingFeature(messagesFilePath, messagesFilePath, -1); // in, out, limit (else 50kb)
      loggingFeature.setPrettyLogging(true);
      client.getFeatures().add(loggingFeature);
      
      es = client.create(ElasticSearchApi.class);
   }*/

   @Override // disable, not mocked yet
   public void testFeatures() throws IOException, ESApiException {
      
   }
   
}

package org.pcu.features.search.server;

import java.io.File;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.pcu.features.search.api.PcuSearchApi;
import org.pcu.features.search.engine.PcuSearchEngineConfiguration;
import org.pcu.features.search.pipeline.PcuSearchPipelineConfiguration;
import org.pcu.features.search.simple.PcuSearchSimpleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * In package BELOW others else (ex. in .web) doesn't scan them.
 * 
 * Jackson XML provider is configured and set up by client module's configuration.
 * 
 * @author mdutoo
 */
@Configuration
@ComponentScan(basePackageClasses={PcuSearchServerConfiguration.class, PcuSearchSimpleConfiguration.class,
      PcuSearchPipelineConfiguration.class, PcuSearchEngineConfiguration.class})
@EnableAutoConfiguration // else Unable to start EmbeddedWebApplicationContext due to missing EmbeddedServletContainerFactory bean see https://stackoverflow.com/questions/21783391/spring-boot-unable-to-start-embeddedwebapplicationcontext-due-to-missing-embedd
@ConfigurationProperties
@PropertySource("classpath:pcu-server-defaults.properties") // (NOT working in yml) fills env with props :
// http.server.servlet-path (& default port), cxf.jaxrs.client.address (& default path), else with executable jar :
// Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'cxf.jaxrs.client.address' in string value "${cxf.jaxrs.client.address}"
public class PcuSearchServerConfiguration {
   
    private static final Logger LOGGER = LoggerFactory.getLogger(PcuSearchServerConfiguration.class);
   
    /** for prop check purpose */
    @Autowired
    private Environment env;
    
    @PostConstruct
    public void postContruct () {
        /*if (!"/system".equals(env.getProperty("server.servlet-path"))) {
           throw new RuntimeException("missing server.servlet-path=/system "
                 + "in pcu-server(-defaults).properties");
        }*/ // TODO only check in prod
    }

    /** either this or @EnableAutoConfiguration else Unable to start EmbeddedWebApplicationContext due to missing EmbeddedServletContainerFactory bean
     * BUT KILLS RANDOM PORT */
    /*@Bean
    @ConditionalOnMissingBean(name = "servletContainer")
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        return factory;
     }*/

    /**
     * Registers CXF servlet. Otherwise, service is created but not available through HTTP.
     * Hardcoded, so removes need for props : cxf.path, cxf.servlet.init.service-list-path.
     * Inspired by CxfAutoConfiguration.java's code . */
    @Bean
    @ConditionalOnMissingBean(name = "cxfServletRegistration")
    public ServletRegistrationBean cxfServletRegistration() {
        String path = env.getProperty("cxf.path", "/pcu"); //  else default would be /services/pcu (NB. not / else blocks UI servlet)
        String urlMapping = path.endsWith("/") ? path + "*" : path + "/*";
        ServletRegistrationBean registration = new ServletRegistrationBean(
                new CXFServlet(), urlMapping); // cxf.path
        /*CxfProperties.Servlet servletProperties = this.properties.getServlet();
        registration.setLoadOnStartup(1);
        for (Map.Entry<String, String> entry : servletProperties.getInit().entrySet()) {
            registration.addInitParameter(entry.getKey(), entry.getValue());
        }*/
        registration.addInitParameter("service-list-path", "/info"); // cxf.servlet.init.service-list-path
        return registration;
    }

    /** Creates CXF bus */
    @Configuration
    @ConditionalOnMissingBean(SpringBus.class)
    @ImportResource("classpath:META-INF/cxf/cxf.xml")
    protected static class SpringBusConfiguration {

    }

    /** NB. reusing client-defined bus (but xxxApiImpl is happily not API client) */
    @Bean
    @Primary // TODO HACK
    public Server pcuJaxrsServer(SpringBus bus, JacksonJsonProvider pcuSearchApiJsonProvider,
          PcuSearchApi pcuSearchApiServerImpl/*, PcuApiExceptionMapper pcuApiExceptionMapper,
          PcuApiSwagger2Feature pcuApiSwagger2Feature*/) {
        ArrayList<Object> providers = new ArrayList<Object>();
        providers.add(pcuSearchApiJsonProvider); // else web app ex Response.Status.UNSUPPORTED_MEDIA_TYPE
        ///providers.add(pcuApiExceptionMapper);
        
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(bus);
        endpoint.setAddress("/");
        endpoint.setServiceBean(pcuSearchApiServerImpl);
        endpoint.setProviders(providers);

        ///endpoint.getFeatures().add(pcuApiSwagger2Feature);
        
        String restLogFilePathProp = env.getProperty("pcu.search.es.restLogFile"); // es-rest-mock.log
        if (restLogFilePathProp != null && !restLogFilePathProp.trim().isEmpty()) { // ex. not in prod
           //LOGGER.warn("Enabling logging of all REST exchanges including body to "
           //      + restLogFilePathProp + " (beware, hampers performance)");
           String restLogFilePath = new File(restLogFilePathProp).toURI().toString(); // if local in dev, is in /server
           LoggingFeature loggingFeature = new LoggingFeature(restLogFilePath, restLogFilePath, 500000); // in, out, limit (else 50kb)
           loggingFeature.setPrettyLogging(true);
           endpoint.getFeatures().add(loggingFeature);
        }
        
        return endpoint.create();
    }
    
}
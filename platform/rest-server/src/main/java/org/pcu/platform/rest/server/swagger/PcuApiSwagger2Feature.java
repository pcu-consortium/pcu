package org.pcu.platform.rest.server.swagger;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.Bus;
import org.apache.cxf.annotations.Provider;
import org.apache.cxf.annotations.Provider.Scope;
import org.apache.cxf.annotations.Provider.Type;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;


/**
 * Overrides default swagger conf provided by cxf's integration of it.
 * @author mdutoo
 *
 */
@Component
@Provider(value = Type.Feature, scope = Scope.Server)
public class PcuApiSwagger2Feature extends Swagger2Feature {

   private static final String API_DESCRIPTION_FILE = "api_description.html";
   
   @Autowired
   private ResourceLoader resourceLoader;

   @Override
   public void initialize(Server server, Bus bus) {
      // manually trigger Spring injection :
      // (since instanciated by CXF and not by Spring, else resourceLoader null)
      // see http://stackoverflow.com/questions/21827548/spring-get-current-applicationcontext
      SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
      
      // actual API conf :
      try {
         configure();
      } catch (IOException ioex) {
         throw new RuntimeException("Error configuring Swagger", ioex);
      }
      
      // regular CXF feature init
      super.initialize(server, bus);
   }

   public void configure() throws IOException {
      String apiDescription = IOUtils.toString(resourceLoader
            .getResource("classpath:" + API_DESCRIPTION_FILE).getInputStream(), (Charset) null);
      // (resource nor input stream can't be null)
      
      this.setVersion("1.0.0");
      this.setBasePath("/pcu"); // else gen'd ajax & curl calls are missing it !!!
      this.setTitle("PCU REST Server");
      this.setDescription(apiDescription);
      this.setContact("contact@smile.fr");
      this.setLicense("Apache License 2.0");
      this.setLicenseUrl("http://www.apache.org/licenses/");
      this.setTermsOfServiceUrl(null);
   }
   
}

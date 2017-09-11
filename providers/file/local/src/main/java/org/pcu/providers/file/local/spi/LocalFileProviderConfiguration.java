package org.pcu.providers.file.local.spi;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses={LocalFileProviderConfiguration.class})
public class LocalFileProviderConfiguration {

}

package org.pcu.platform.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PcuPlatformServerApplication extends SpringApplication {

	public PcuPlatformServerApplication() {
		// mandatory for spring
	}

	public PcuPlatformServerApplication(final Class<?>... sources) {
		super(sources);
	}

	public static void main(final String... args) {
		SpringApplication.run(PcuPlatformServerApplication.class, args);
		//new PcuPlatformServerApplication(PcuPlatformServerConfiguration.class).run(args);
	}

}

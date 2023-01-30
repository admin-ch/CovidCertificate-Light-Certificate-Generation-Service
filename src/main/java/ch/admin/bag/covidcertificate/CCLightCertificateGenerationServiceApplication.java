package ch.admin.bag.covidcertificate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@ServletComponentScan
@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@Slf4j
public class CCLightCertificateGenerationServiceApplication {

	public static void main(String[] args) {

		Environment env = SpringApplication.run(CCLightCertificateGenerationServiceApplication.class, args).getEnvironment();

		String protocol = "http";
		if (env.getProperty("server.ssl.key-store") != null) {
			protocol = "https";
		}
		final String message = """
                ----------------------------------------------------------
                    "Yeah!!! {} is running!
                    
                    SwaggerUI:   {}://localhost:{}/swagger-ui.html
                    "Profile(s): {}
                ----------------------------------------------------------
                """;
		log.info(message,
				env.getProperty("spring.application.name"),
				protocol,
				env.getProperty("server.port"),
				env.getActiveProfiles());
	}
}

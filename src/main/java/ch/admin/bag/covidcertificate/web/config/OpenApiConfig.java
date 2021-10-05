package ch.admin.bag.covidcertificate.web.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("CC Light Certificate Generation Service")
                        .description("Rest API for Covid Certificate Light Certificate Generation Service.")
                        .version("0.0.1")
                        .license(new License().name("Apache 2.0"))
                );
    }
}
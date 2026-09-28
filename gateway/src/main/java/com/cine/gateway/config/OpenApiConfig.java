package com.cine.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cineOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Cines Bruma")
                .version("1.0")
                .description("Aquí se ve cómo la tienda pide la cartelera, la dulcería, el inicio de sesión y el pago."));
    }
}

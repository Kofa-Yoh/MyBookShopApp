package com.example.MyBookShopApp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI bookShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("BookShop API")
                        .description("Book shop sample application")
                        .version("v0.0.1"))
                .components(new Components())
                ;
    }
}

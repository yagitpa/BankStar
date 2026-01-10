package com.bank.star.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server()
                .url("http://localhost:" + serverPort)
                .description("Локальный сервер");

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("Банк \"Стар\". API системы рекомендаций")
                        .version("2.0.0")
                        .description("REST API для рекомендательной системы банковских продуктов с поддержкой динамических правил")
                        .contact(new Contact()
                                .name("Bank \"Star\" Development Team")
                                .email("dev@bankstar.ru")
                                .url("https://bankstar.ru"))
                        .license(new License()
                                .name("Bank Star License")
                                .url("https://bankstar.ru/license")));
    }
}

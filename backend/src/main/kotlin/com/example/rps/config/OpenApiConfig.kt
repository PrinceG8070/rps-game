package com.example.rps.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Rock Paper Scissors Full Stack API")
                    .version("1.0.0")
                    .description("REST API backend for Rock Paper Scissors full stack application with Kotlin Spring Boot, MySQL, React, Flutter, and Firebase.")
                    .contact(
                        Contact()
                            .name("API Support")
                            .email("support@example.com")
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https://springdoc.org")
                    )
            )
            .servers(
                listOf(
                    Server().url("http://localhost:8080").description("Local Development Server")
                )
            )
    }
}

package com.onlineshopping.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger UI configuration.
 *
 * <p>Accessible at:
 * <ul>
 *   <li>Swagger UI  → <a href="http://localhost:8080/swagger-ui.html">http://localhost:8080/swagger-ui.html</a></li>
 *   <li>OpenAPI JSON → <a href="http://localhost:8080/v3/api-docs">http://localhost:8080/v3/api-docs</a></li>
 * </ul>
 *
 * <p>The {@code bearerAuth} security scheme allows you to paste a JWT token
 * directly in the Swagger UI "Authorize" dialog and test secured endpoints.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Online Shopping REST API",
                version = "1.0.0",
                description = "Full-stack Online Shopping application built with Spring Boot, MongoDB and Apache Cassandra",
                contact = @Contact(
                        name = "Online Shopping Support",
                        email = "support@onlineshopping.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development"),
                @Server(url = "https://api.onlineshopping.com", description = "Production")
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Bearer token authentication. Obtain a token from POST /api/auth/login then click 'Authorize' and enter: Bearer <token>",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // No bean definitions needed – the annotations above do all the work.
}


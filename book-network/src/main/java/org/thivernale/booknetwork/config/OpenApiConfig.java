package org.thivernale.booknetwork.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        contact = @Contact(name = "T H", email = "", url = ""),
        description = "OpenAPI documentation",
        title = "OpenAPI specification",
        version = "1.0",
        license = @License(name = "", url = ""),
        termsOfService = "ToS"
    ),
    servers = {
        @Server(url = "http://localhost:8080/api/v1", description = "Local environment"),
        @Server(url = "https://some-url.com/api/v1", description = "Production environment")
    },
    security = {
        @SecurityRequirement(name = "bearerAuth")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.APIKEY,
    scheme = "bearer",
    description = "JWT Auth",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}

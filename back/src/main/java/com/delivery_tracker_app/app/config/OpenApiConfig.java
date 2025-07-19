package com.delivery_tracker_app.app.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Delivery Tracker API",
                version = "1.0.0",
                description = "API for managing transport logs, drivers, and clients in the delivery tracking system.",
                contact = @Contact(
                        name = "3ss", // Replace with your name/team
                        email = "contact@example.com" // Replace with your contact email
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server"),
                @Server(url = "https://api.translogs.com", description = "Production / Future Deployment Server")
        }
)
@SecurityScheme( // Define the security scheme here
        name = "BearerAuth", // This name must match the 'name' in @SecurityRequirement
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        description = "JWT authentication using a Bearer token. Enter the token (without 'Bearer ') in the field below."
)

public class OpenApiConfig {
}

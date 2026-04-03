package com.finance.dashboard.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Finance Dashboard API")
                        .description("""
                                REST API for the Finance Dashboard system.

                                **How to authenticate:**
                                1. Call `POST /api/auth/login` with your credentials
                                2. Copy the `token` value from the response
                                3. Click the **Authorize** 🔒 button at the top right
                                4. Paste the token (no "Bearer " prefix needed here)
                                5. All secured endpoints now work

                                **Default seeded users:**
                                | Role    | Email                  | Password   |
                                |---------|------------------------|------------|
                                | ADMIN   | admin@finance.com      | admin123   |
                                | ANALYST | analyst@finance.com    | analyst123 |
                                | VIEWER  | viewer@finance.com     | viewer123  |
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("Finance Dashboard")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local")))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}

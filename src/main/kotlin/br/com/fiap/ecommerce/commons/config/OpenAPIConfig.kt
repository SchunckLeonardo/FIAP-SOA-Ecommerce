package br.com.fiap.ecommerce.commons.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "Ecommerce API",
        version = "1.0",
        description = "API for managing an ecommerce platform.",
        contact = Contact(
            name = "Leonardo Schunck Rainha",
            email = "leonardo@gmail.com",
            url = "https://github.com/SchunckLeonardo/Library-API"
        )
    ),
    security = [
        SecurityRequirement(name = "bearerAuth")
    ]
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    `in` = SecuritySchemeIn.HEADER
)
class OpenAPIConfig {
}
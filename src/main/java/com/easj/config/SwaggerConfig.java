package com.easj.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(title = "API Servicios Backend", version = "1.0", description = "Documentación de servicios backend"),
    security = @SecurityRequirement(name = "BearerAuth") // Nombre del esquema
)
@SecurityScheme(
    name = "BearerAuth", // Tiene que coincidir con el de arriba
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class SwaggerConfig {
    // No es necesario poner métodos aquí, solo las anotaciones ya configuran Swagger correctamente
}

package com.microservicio.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de SpringDoc OpenAPI (Swagger).
 * Añade información sobre la API y la configuración de seguridad para el UI de Swagger.
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                // Información general de la API
                .info(new Info()
                        .title("API Rest de Usuarios - Daniel Prado Amoretty")
                        .version("1.0")
                        .description("API para la gestión de usuarios (CRUD) con Spring Security "
                        )) ;
    }
}

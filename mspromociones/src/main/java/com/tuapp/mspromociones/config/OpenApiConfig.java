package com.tuapp.mspromociones.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * ===========================================================
 * CONFIGURACION DE OPENAPI / SWAGGER
 * ===========================================================
 *
 * Define la metadata que se muestra en la UI de Swagger
 * (titulo, descripcion, version y servidor) para este
 * microservicio.
 *
 * La documentacion interactiva queda disponible en:
 *
 * http://localhost:8087/swagger-ui/index.html
 *
 * El contrato OpenAPI (JSON) queda disponible en:
 *
 * http://localhost:8087/v3/api-docs
 */

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("MS Promociones API")
                        .description("Microservicio encargado de la gestión y validación de cupones de descuento (promociones).")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Arquitectura de Microservicios")
                                .email("equipo@tuapp.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8087")
                                .description("Servidor local de desarrollo")
                ));

    }

}

package com.tuapp.msnotificaciones.config;

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
 * http://localhost:8089/swagger-ui/index.html
 *
 * El contrato OpenAPI (JSON) queda disponible en:
 *
 * http://localhost:8089/v3/api-docs
 */

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("MS Notificaciones API")
                        .description("Microservicio encargado de crear y gestionar las notificaciones generadas por otros microservicios (pagos, pedidos, delivery).")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Arquitectura de Microservicios")
                                .email("equipo@tuapp.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8089")
                                .description("Servidor local de desarrollo")
                ));

    }

}

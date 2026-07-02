package com.tuapp.msnotificaciones.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ===========================================================
 * CONFIGURACIÓN DE WEBCLIENT
 * ===========================================================
 *
 * Define el bean WebClient utilizado para comunicarse con
 * el microservicio msusuarios.
 *
 * La URL base se obtiene desde application.properties
 * (propiedad msusuarios.url), evitando dejar direcciones
 * "quemadas" dentro del código.
 */

@Configuration
public class WebClientConfig {

    @Value("${msusuarios.url}")
    private String msusuariosUrl;

    @Bean
    public WebClient usuarioWebClient() {

        return WebClient.builder()
                .baseUrl(msusuariosUrl)
                .build();

    }

}

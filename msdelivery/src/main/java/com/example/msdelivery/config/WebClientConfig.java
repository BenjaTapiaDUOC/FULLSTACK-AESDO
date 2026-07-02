package com.example.msdelivery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ===========================================================
 * CONFIGURACIÓN WEBCLIENT
 * ===========================================================
 *
 * Define el bean WebClient utilizado para comunicarse con el
 * microservicio mspedidos.
 *
 * La URL base se obtiene desde application.properties
 * (mspedidos.url), evitando así dejar la URL "quemada" en
 * el código.
 */

@Configuration
public class WebClientConfig {

    @Value("${mspedidos.url}")
    private String mspedidosUrl;

    @Bean
    public WebClient pedidosWebClient() {
        return WebClient.builder()
                .baseUrl(mspedidosUrl)
                .build();
    }

}

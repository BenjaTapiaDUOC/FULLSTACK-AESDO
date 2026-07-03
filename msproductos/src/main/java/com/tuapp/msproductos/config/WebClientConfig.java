package com.tuapp.msproductos.config;

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
 * microservicio msrestaurantes.
 *
 * La URL base se obtiene desde application.yml
 * (msrestaurantes.url), evitando así dejar la URL "quemada"
 * en el código.
 */

@Configuration
public class WebClientConfig {

    @Value("${msrestaurantes.url}")
    private String msrestaurantesUrl;

    @Bean
    public WebClient restaurantesWebClient() {
        return WebClient.builder()
                .baseUrl(msrestaurantesUrl)
                .build();
    }

}

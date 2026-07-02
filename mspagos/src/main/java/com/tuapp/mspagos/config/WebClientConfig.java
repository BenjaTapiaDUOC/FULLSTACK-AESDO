package com.tuapp.mspagos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ===========================================================
 * CONFIGURACIÓN DE WEBCLIENT
 * ===========================================================
 *
 * Define el bean WebClient utilizado para comunicarse
 * con otros microservicios (por ejemplo, mspedidos).
 */

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

}

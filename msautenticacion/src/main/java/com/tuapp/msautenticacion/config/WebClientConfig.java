package com.tuapp.msautenticacion.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ===========================================================
 * CONFIGURACIÓN WEBCLIENT
 * ===========================================================
 *
 * Expone un WebClient.Builder como Bean de Spring, el cual
 * es utilizado por UsuarioMsClient para comunicarse con el
 * microservicio msusuarios (puerto 8081).
 */

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}

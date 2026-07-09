package com.tuapp.msautenticacion.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
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
 * microservicio msusuarios.
 *
 * @LoadBalanced hace que este builder resuelva URIs con
 * esquema lb://msusuarios contra el registro de mseureka,
 * en vez de depender de una IP/puerto fijo.
 */

@Configuration
public class WebClientConfig {

    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}

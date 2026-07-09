package com.tuapp.mspagos.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ===========================================================
 * CONFIGURACIÓN DE WEBCLIENT
 * ===========================================================
 *
 * Define el bean WebClient.Builder utilizado para comunicarse
 * con otros microservicios (por ejemplo, mspedidos).
 *
 * @LoadBalanced hace que este builder resuelva URIs con
 * esquema lb://mspedidos contra el registro de mseureka,
 * en vez de depender de una IP/puerto fijo.
 *
 * PedidoClient recibe un WebClient ya construido (sin baseUrl
 * fija) porque arma la URL completa concatenando
 * microservicio.pedidos.url en cada llamada.
 */

@Configuration
public class WebClientConfig {

    @LoadBalanced
    @Bean
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(WebClient.Builder loadBalancedWebClientBuilder) {
        return loadBalancedWebClientBuilder.build();
    }

}

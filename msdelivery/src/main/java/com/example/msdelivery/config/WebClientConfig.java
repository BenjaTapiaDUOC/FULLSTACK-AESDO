package com.example.msdelivery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
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
 * La URL base se obtiene desde application.yml (mspedidos.url)
 * y ahora usa el esquema lb://mspedidos. El builder
 * @LoadBalanced hace que Spring Cloud LoadBalancer resuelva ese
 * nombre lógico contra el registro de mseureka.
 */

@Configuration
public class WebClientConfig {

    @Value("${mspedidos.url}")
    private String mspedidosUrl;

    @LoadBalanced
    @Bean
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient pedidosWebClient(WebClient.Builder loadBalancedWebClientBuilder) {
        return loadBalancedWebClientBuilder
                .baseUrl(mspedidosUrl)
                .build();
    }

}

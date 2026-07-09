package com.tuapp.msproductos.config;

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
 * microservicio msrestaurantes.
 *
 * La URL base se obtiene desde application.yml
 * (msrestaurantes.url) y ahora usa el esquema lb://msrestaurantes.
 * El builder @LoadBalanced hace que Spring Cloud LoadBalancer
 * resuelva ese nombre lógico contra el registro de mseureka.
 */

@Configuration
public class WebClientConfig {

    @Value("${msrestaurantes.url}")
    private String msrestaurantesUrl;

    @LoadBalanced
    @Bean
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient restaurantesWebClient(WebClient.Builder loadBalancedWebClientBuilder) {
        return loadBalancedWebClientBuilder
                .baseUrl(msrestaurantesUrl)
                .build();
    }

}

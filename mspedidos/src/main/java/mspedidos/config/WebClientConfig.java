package mspedidos.config;

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
 * microservicio msusuarios.
 *
 * La URL base se obtiene desde application.yml (msusuarios.url)
 * y ahora usa el esquema lb://msusuarios. El builder
 * @LoadBalanced hace que Spring Cloud LoadBalancer resuelva ese
 * nombre lógico contra el registro de mseureka.
 */

@Configuration
public class WebClientConfig {

    @Value("${msusuarios.url}")
    private String msusuariosUrl;

    @LoadBalanced
    @Bean
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient usuariosWebClient(WebClient.Builder loadBalancedWebClientBuilder) {
        return loadBalancedWebClientBuilder
                .baseUrl(msusuariosUrl)
                .build();
    }

}

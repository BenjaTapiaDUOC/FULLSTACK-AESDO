package mspedidos.config;

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
 * microservicio msusuarios.
 *
 * La URL base se obtiene desde application.properties
 * (msusuarios.url), evitando así dejar la URL "quemada" en
 * el código.
 */

@Configuration
public class WebClientConfig {

    @Value("${msusuarios.url}")
    private String msusuariosUrl;

    @Bean
    public WebClient usuariosWebClient() {
        return WebClient.builder()
                .baseUrl(msusuariosUrl)
                .build();
    }

}

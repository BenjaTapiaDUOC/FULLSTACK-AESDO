package com.tuapp.msnotificaciones.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * ===========================================================
 * CLIENTE HTTP - MSUSUARIOS
 * ===========================================================
 *
 * Esta clase se encarga de la comunicación entre
 * msnotificaciones y el microservicio msusuarios.
 *
 * Se utiliza WebClient para consultar si un usuario existe
 * antes de registrar una notificación asociada a él.
 *
 * URL consumida:
 *
 * http://localhost:8081/usuarios/{id}
 */

@Component
public class UsuarioClient {

    private static final Logger logger =
            LoggerFactory.getLogger(UsuarioClient.class);

    private final WebClient webClient;

    public UsuarioClient(WebClient usuarioWebClient) {
        this.webClient = usuarioWebClient;
    }

    /**
     * ===========================================================
     * VERIFICAR EXISTENCIA DE USUARIO
     * ===========================================================
     *
     * Consulta msusuarios para saber si el usuarioId recibido
     * corresponde a un usuario realmente registrado.
     *
     * - Si msusuarios responde 200 OK -> el usuario existe.
     * - Si msusuarios responde 404 NOT FOUND -> el usuario no existe.
     * - Si ocurre un timeout o el servicio no está disponible,
     *   la excepción se propaga y es capturada por el
     *   GlobalExceptionHandler.
     */
    public boolean existeUsuario(Long usuarioId) {

        logger.info("Validando existencia del usuario {} en msusuarios.", usuarioId);

        try {

            webClient.get()
                    .uri("/{id}", usuarioId)
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(5))
                    .block();

            logger.info("El usuario {} existe en msusuarios.", usuarioId);

            return true;

        } catch (WebClientResponseException.NotFound ex) {

            logger.warn("El usuario {} no existe en msusuarios.", usuarioId);

            return false;

        }

    }

}

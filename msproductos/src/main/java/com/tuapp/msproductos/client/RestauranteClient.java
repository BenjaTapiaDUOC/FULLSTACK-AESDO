package com.tuapp.msproductos.client;

import com.tuapp.msproductos.dto.RestauranteClienteDTO;
import com.tuapp.msproductos.exception.BadRequestException;
import com.tuapp.msproductos.exception.ServicioExternoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * ===========================================================
 * CLIENTE HTTP - MICROSERVICIO MSRESTAURANTES
 * ===========================================================
 *
 * Esta clase centraliza toda la comunicación entre msproductos
 * y msrestaurantes.
 *
 * Se utiliza WebClient para consultar si un restaurante existe
 * y se encuentra activo antes de permitir la creación de un
 * producto asociado a él.
 *
 * Endpoint remoto consumido:
 *
 * GET http://localhost:8086/restaurantes/{id}
 */

@Component
public class RestauranteClient {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteClient.class);

    private final WebClient restaurantesWebClient;

    public RestauranteClient(WebClient restaurantesWebClient) {
        this.restaurantesWebClient = restaurantesWebClient;
    }

    /**
     * ===========================================================
     * OBTENER RESTAURANTE POR ID
     * ===========================================================
     *
     * Consulta al microservicio msrestaurantes para validar que
     * el restaurante del producto efectivamente existe y está
     * activo.
     *
     * Manejo de errores:
     *
     * - 404 en msrestaurantes         -> BadRequestException (400 en msproductos)
     * - Timeout / msrestaurantes caído -> ServicioExternoException (503)
     */
    public RestauranteClienteDTO obtenerRestaurante(Long restauranteId) {

        logger.info("Consultando existencia del restaurante {} en msrestaurantes", restauranteId);

        try {

            return restaurantesWebClient.get()
                    .uri("/restaurantes/{id}", restauranteId)
                    .retrieve()
                    .bodyToMono(RestauranteClienteDTO.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

        } catch (WebClientResponseException.NotFound ex) {

            logger.warn("El restaurante {} no existe en msrestaurantes", restauranteId);

            throw new BadRequestException(
                    "El restaurante con ID " + restauranteId + " no existe."
            );

        } catch (Exception ex) {

            logger.error("No fue posible comunicarse con msrestaurantes: {}", ex.getMessage());

            throw new ServicioExternoException(
                    "No fue posible validar el restaurante. El microservicio msrestaurantes no respondió."
            );

        }

    }

}

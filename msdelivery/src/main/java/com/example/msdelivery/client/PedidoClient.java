package com.example.msdelivery.client;

import com.example.msdelivery.dto.PedidoClienteDTO;
import com.example.msdelivery.exception.BadRequestException;
import com.example.msdelivery.exception.DeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * ===========================================================
 * CLIENTE HTTP - MICROSERVICIO MSPEDIDOS
 * ===========================================================
 *
 * Esta clase centraliza toda la comunicación entre msdelivery
 * y mspedidos.
 *
 * Se utiliza WebClient para consultar si un pedido existe
 * antes de permitir la creación (o actualización) de un
 * delivery asociado a dicho pedido.
 *
 * Endpoint remoto consumido:
 *
 * GET http://localhost:8083/pedidos/{id}
 */

@Component
public class PedidoClient {

    private static final Logger logger = LoggerFactory.getLogger(PedidoClient.class);

    private final WebClient pedidosWebClient;

    public PedidoClient(WebClient pedidosWebClient) {
        this.pedidosWebClient = pedidosWebClient;
    }

    /**
     * ===========================================================
     * OBTENER PEDIDO POR ID
     * ===========================================================
     *
     * Consulta al microservicio mspedidos para validar que el
     * pedido del delivery efectivamente existe.
     *
     * Manejo de errores:
     *
     * - 404 en mspedidos -> BadRequestException (400 en msdelivery)
     * - Timeout / mspedidos caído -> DeliveryException (503)
     */
    public PedidoClienteDTO obtenerPedido(Long pedidoId) {

        logger.info("Consultando existencia del pedido {} en mspedidos", pedidoId);

        try {

            return pedidosWebClient.get()
                    .uri("/pedidos/{id}", pedidoId)
                    .retrieve()
                    .bodyToMono(PedidoClienteDTO.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

        } catch (WebClientResponseException.NotFound ex) {

            logger.warn("El pedido {} no existe en mspedidos", pedidoId);

            throw new BadRequestException(
                    "El pedido con ID " + pedidoId + " no existe."
            );

        } catch (Exception ex) {

            logger.error("No fue posible comunicarse con mspedidos: {}", ex.getMessage());

            throw new DeliveryException(
                    "No fue posible validar el pedido. El microservicio mspedidos no respondió."
            );

        }

    }

}

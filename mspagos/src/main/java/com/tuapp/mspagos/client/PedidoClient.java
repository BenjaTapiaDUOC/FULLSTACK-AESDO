package com.tuapp.mspagos.client;

import com.tuapp.mspagos.exception.BadRequestException;
import com.tuapp.mspagos.exception.PagoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * ===========================================================
 * CLIENTE HTTP - MICROSERVICIO MSPEDIDOS
 * ===========================================================
 *
 * Centraliza toda la comunicación entre mspagos y mspedidos
 * relacionada con la VALIDACIÓN previa del pedido.
 *
 * Se utiliza para confirmar que el pedido asociado a un pago
 * efectivamente existe, ANTES de registrar cualquier pago en
 * la base de datos (evita pagos "aprobados" huérfanos si el
 * pedidoId no existe).
 *
 * Endpoint remoto consumido:
 *
 * GET http://localhost:8083/pedidos/{id}
 */

@Component
public class PedidoClient {

    private static final Logger logger =
            LoggerFactory.getLogger(PedidoClient.class);

    private final WebClient webClient;

    /**
     * URL base del microservicio de Pedidos.
     * Se inyecta desde application.properties
     * (microservicio.pedidos.url).
     */
    @Value("${microservicio.pedidos.url}")
    private String pedidosUrl;

    public PedidoClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * ===========================================================
     * VALIDAR EXISTENCIA DEL PEDIDO
     * ===========================================================
     *
     * Consulta a mspedidos para confirmar que el pedidoId
     * recibido realmente existe.
     *
     * Manejo de errores (mismo criterio que usan mspedidos
     * y msdelivery al validar recursos remotos):
     *
     * - 404 en mspedidos -> BadRequestException (400 en mspagos)
     * - Timeout / mspedidos caído -> PagoException (503 en mspagos)
     */
    public void validarPedidoExiste(Long pedidoId) {

        logger.info("Validando existencia del pedido {} en mspedidos antes de procesar el pago", pedidoId);

        try {

            webClient.get()
                    .uri(pedidosUrl + "/{id}", pedidoId)
                    .retrieve()
                    .toBodilessEntity()
                    .timeout(Duration.ofSeconds(5))
                    .block();

        } catch (WebClientResponseException.NotFound ex) {

            logger.warn("El pedido {} no existe en mspedidos. No se puede procesar el pago.", pedidoId);

            throw new BadRequestException(
                    "El pedido con ID " + pedidoId + " no existe. No es posible procesar el pago."
            );

        } catch (Exception ex) {

            logger.error("No fue posible comunicarse con mspedidos para validar el pedido {}: {}",
                    pedidoId, ex.getMessage());

            throw new PagoException(
                    "No fue posible validar el pedido. El microservicio mspedidos no respondió."
            );

        }

    }

}

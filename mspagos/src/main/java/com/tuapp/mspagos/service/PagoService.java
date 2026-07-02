package com.tuapp.mspagos.service;

import com.tuapp.mspagos.client.PedidoClient;
import com.tuapp.mspagos.dto.PagoRequestDTO;
import com.tuapp.mspagos.dto.PagoResponseDTO;
import com.tuapp.mspagos.exception.BadRequestException;
import com.tuapp.mspagos.exception.PagoException;
import com.tuapp.mspagos.exception.PagoNotFoundException;
import com.tuapp.mspagos.model.Pago;
import com.tuapp.mspagos.repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ===========================================================
 * SERVICIO DE PAGOS
 * ===========================================================
 *
 * Aquí se implementa toda la lógica de negocio.
 *
 * El Controller solamente llama a estos métodos.
 *
 * Esta clase se comunica directamente con el Repository
 * y, además, con el microservicio de Pedidos (mspedidos)
 * mediante WebClient.
 */

@Service
public class PagoService {

    /**
     * Logger del microservicio.
     * Permite registrar eventos importantes durante la ejecución.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(PagoService.class);

    /**
     * Regla de negocio: monto máximo permitido por pago.
     *
     * Si el monto ingresado supera este límite, el pago
     * se marca automáticamente como RECHAZADO.
     */
    private static final Double MONTO_MAXIMO_PERMITIDO = 5_000_000.0;

    private final PagoRepository repository;
    private final WebClient webClient;
    private final PedidoClient pedidoClient;

    /**
     * URL base del microservicio de Pedidos.
     * Se inyecta desde application.properties.
     */
    @Value("${microservicio.pedidos.url}")
    private String pedidosUrl;

    public PagoService(PagoRepository repository, WebClient webClient, PedidoClient pedidoClient) {
        this.repository = repository;
        this.webClient = webClient;
        this.pedidoClient = pedidoClient;
    }

    /**
     * ===========================================================
     * CREAR / PROCESAR PAGO
     * ===========================================================
     *
     * Este metodo:
     *
     * 1. Aplica la regla de negocio de monto máximo permitido.
     * 2. Define el estado del pago (APROBADO o RECHAZADO).
     * 3. Guarda el pago en la base de datos.
     * 4. Si fue aprobado, notifica al microservicio de Pedidos.
     * 5. Devuelve un DTO como respuesta.
     */
    public PagoResponseDTO crearPago(PagoRequestDTO dto) {

        logger.info("Procesando pago para el pedido {}", dto.getPedidoId());

        // Validación previa obligatoria: el pedido debe existir en
        // mspedidos ANTES de registrar cualquier pago (aprobado o
        // rechazado). Esto evita dejar pagos "aprobados" huérfanos
        // en la base de datos cuando el pedidoId no existe.
        pedidoClient.validarPedidoExiste(dto.getPedidoId());

        Pago pago = new Pago(
                null,
                dto.getPedidoId(),
                dto.getMonto(),
                dto.getMetodoPago(),
                "PENDIENTE",
                LocalDateTime.now()
        );

        // Regla de negocio: monto máximo permitido por pago
        if (dto.getMonto() > MONTO_MAXIMO_PERMITIDO) {

            logger.warn("Pago rechazado: el monto {} supera el máximo permitido.", dto.getMonto());

            pago.setEstado("RECHAZADO");

            Pago guardado = repository.save(pago);

            return mapearAResponseDTO(guardado);

        }

        pago.setEstado("APROBADO");

        Pago guardado = repository.save(pago);

        logger.info("Pago {} aprobado correctamente.", guardado.getId());

        // Notificar al microservicio de Pedidos que el pago fue aprobado
        notificarPedido(guardado.getPedidoId());

        return mapearAResponseDTO(guardado);

    }

    /**
     * ===========================================================
     * NOTIFICAR AL MICROSERVICIO DE PEDIDOS
     * ===========================================================
     *
     * Actualiza el estado del pedido asociado a "PAGADO"
     * consumiendo el endpoint remoto mediante WebClient.
     *
     * Los errores de comunicación (timeouts, servicio caído,
     * respuestas de error) son capturados por el
     * GlobalExceptionHandler.
     */
    private void notificarPedido(Long pedidoId) {

        logger.info("Notificando al microservicio de Pedidos sobre el pedido {}", pedidoId);

        try {

            webClient.put()
                    .uri(pedidosUrl + "/" + pedidoId + "/estado?estado=PAGADO")
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            logger.info("Pedido {} notificado correctamente.", pedidoId);

        } catch (Exception ex) {

            logger.error("El pago fue aprobado pero no fue posible notificar a mspedidos sobre el pedido {}: {}",
                    pedidoId, ex.getMessage());

            throw new PagoException(
                    "El pago fue registrado como APROBADO, pero no fue posible notificar al microservicio de pedidos."
            );

        }

    }

    /**
     * ===========================================================
     * LISTAR PAGOS
     * ===========================================================
     *
     * Obtiene todos los pagos registrados.
     */
    public List<PagoResponseDTO> listarPagos() {

        logger.info("Consultando listado completo de pagos.");

        return repository.findAll()

                .stream()

                .map(this::mapearAResponseDTO)

                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * BUSCAR PAGO POR ID
     * ===========================================================
     *
     * Busca un pago utilizando su identificador.
     */
    public PagoResponseDTO obtenerPorId(Long id) {

        logger.info("Buscando pago con ID {}", id);

        Pago pago = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Pago {} no encontrado.", id);

                    return new PagoNotFoundException(
                            "Pago no encontrado."
                    );

                });

        return mapearAResponseDTO(pago);

    }

    /**
     * ===========================================================
     * ACTUALIZAR PAGO
     * ===========================================================
     *
     * Actualiza la información de un pago existente.
     *
     * No permite modificar un pago que ya fue APROBADO,
     * para mantener la integridad de la transacción.
     */
    public PagoResponseDTO actualizar(Long id, PagoRequestDTO dto) {

        logger.info("Actualizando pago {}", id);

        Pago pago = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Pago {} no encontrado.", id);

                    return new PagoNotFoundException(
                            "Pago no encontrado."
                    );

                });

        if ("APROBADO".equals(pago.getEstado())) {

            logger.warn("Intento de modificar un pago ya aprobado ({}).", id);

            throw new BadRequestException(
                    "No es posible modificar un pago que ya fue aprobado."
            );

        }

        pago.setPedidoId(dto.getPedidoId());

        pago.setMonto(dto.getMonto());

        pago.setMetodoPago(dto.getMetodoPago());

        Pago actualizado = repository.save(pago);

        logger.info("Pago {} actualizado correctamente.", id);

        return mapearAResponseDTO(actualizado);

    }

    /**
     * ===========================================================
     * ELIMINAR PAGO
     * ===========================================================
     *
     * Elimina un pago utilizando su ID.
     */
    public void eliminar(Long id) {

        logger.info("Intentando eliminar pago {}", id);

        if (!repository.existsById(id)) {

            logger.warn("No se pudo eliminar el pago {} porque no existe.", id);

            throw new PagoNotFoundException(
                    "Pago no encontrado."
            );

        }

        repository.deleteById(id);

        logger.info("Pago {} eliminado correctamente.", id);

    }

    /**
     * ===========================================================
     * MAPEO A DTO DE RESPUESTA
     * ===========================================================
     */
    private PagoResponseDTO mapearAResponseDTO(Pago pago) {

        return new PagoResponseDTO(

                pago.getId(),

                pago.getPedidoId(),

                pago.getMonto(),

                pago.getMetodoPago(),

                pago.getEstado(),

                pago.getFechaPago()

        );

    }

}

package mspedidos.service;

import mspedidos.client.UsuarioClient;
import mspedidos.dto.DetalleRequestDTO;
import mspedidos.dto.DetalleResponseDTO;
import mspedidos.dto.PedidoRequestDTO;
import mspedidos.dto.PedidoResponseDTO;
import mspedidos.exception.BadRequestException;
import mspedidos.exception.PedidoNotFoundException;
import mspedidos.model.DetallePedido;
import mspedidos.model.Pedido;
import mspedidos.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ===========================================================
 * SERVICIO DE PEDIDOS
 * ===========================================================
 *
 * Aquí se implementa toda la lógica de negocio.
 *
 * El Controller solamente llama a estos métodos.
 *
 * Esta clase se comunica con el Repository (persistencia) y
 * con UsuarioClient (comunicación con el microservicio
 * msusuarios).
 */

@Service
public class PedidoService {

    /**
     * Logger del microservicio.
     * Permite registrar eventos importantes durante la ejecución.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(PedidoService.class);

    /**
     * Estados válidos que puede tener un pedido.
     */
    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "PENDIENTE", "EN_PROCESO", "ENVIADO", "ENTREGADO", "CANCELADO"
    );

    private final PedidoRepository repository;
    private final UsuarioClient usuarioClient;

    public PedidoService(PedidoRepository repository, UsuarioClient usuarioClient) {
        this.repository = repository;
        this.usuarioClient = usuarioClient;
    }

    /**
     * ===========================================================
     * CREAR PEDIDO
     * ===========================================================
     *
     * Este método:
     *
     * 1. Valida que el pedido tenga al menos un detalle.
     * 2. Verifica que el usuario exista, consultando a
     *    msusuarios mediante WebClient.
     * 3. Construye la entidad Pedido junto a sus detalles.
     * 4. Calcula el total del pedido.
     * 5. Guarda el pedido en la base de datos.
     * 6. Devuelve un DTO como respuesta.
     */
    public PedidoResponseDTO crear(PedidoRequestDTO dto) {

        logger.info("Intentando crear pedido para el usuario {}", dto.getUsuarioId());

        // Validación adicional de regla de negocio (además de @NotEmpty en el DTO)
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {

            logger.warn("Intento de crear un pedido sin detalles.");

            throw new BadRequestException("El pedido debe tener al menos un producto.");

        }

        // Comunicación con msusuarios: valida que el usuario exista
        usuarioClient.obtenerUsuario(dto.getUsuarioId());

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(dto.getUsuarioId());
        pedido.setEstado("PENDIENTE");
        pedido.setFechaCreacion(LocalDateTime.now());

        for (DetalleRequestDTO detalleDto : dto.getDetalles()) {

            DetallePedido detalle = new DetallePedido();
            detalle.setProductoId(detalleDto.getProductoId());
            detalle.setCantidad(detalleDto.getCantidad());
            detalle.setPrecio(detalleDto.getPrecio());

            pedido.agregarDetalle(detalle);

        }

        double total = pedido.getDetalles().stream()
                .mapToDouble(DetallePedido::getSubtotal)
                .sum();

        pedido.setTotal(total);

        Pedido guardado = repository.save(pedido);

        logger.info("Pedido {} creado correctamente para el usuario {}.",
                guardado.getId(), guardado.getUsuarioId());

        return mapearAResponseDTO(guardado);

    }

    /**
     * ===========================================================
     * LISTAR PEDIDOS
     * ===========================================================
     *
     * Obtiene todos los pedidos registrados.
     */
    public List<PedidoResponseDTO> listarPedidos() {

        logger.info("Consultando listado completo de pedidos.");

        return repository.findAll()

                .stream()

                .map(this::mapearAResponseDTO)

                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * BUSCAR PEDIDO POR ID
     * ===========================================================
     *
     * Busca un pedido utilizando su identificador.
     */
    public PedidoResponseDTO obtenerPorId(Long id) {

        logger.info("Buscando pedido con ID {}", id);

        Pedido pedido = buscarOFallar(id);

        return mapearAResponseDTO(pedido);

    }

    /**
     * ===========================================================
     * LISTAR PEDIDOS POR USUARIO
     * ===========================================================
     *
     * Obtiene todos los pedidos realizados por un usuario
     * específico.
     */
    public List<PedidoResponseDTO> listarPorUsuario(Long usuarioId) {

        logger.info("Consultando pedidos del usuario {}", usuarioId);

        return repository.findByUsuarioId(usuarioId)

                .stream()

                .map(this::mapearAResponseDTO)

                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * ACTUALIZAR ESTADO DEL PEDIDO
     * ===========================================================
     *
     * Cambia el estado de un pedido existente, validando que
     * el nuevo estado sea uno de los permitidos.
     */
    public PedidoResponseDTO actualizarEstado(Long id, String nuevoEstado) {

        logger.info("Actualizando estado del pedido {} a {}", id, nuevoEstado);

        if (nuevoEstado == null || !ESTADOS_VALIDOS.contains(nuevoEstado.toUpperCase())) {

            logger.warn("Estado inválido recibido: {}", nuevoEstado);

            throw new BadRequestException(
                    "Estado inválido. Valores permitidos: " + ESTADOS_VALIDOS
            );

        }

        Pedido pedido = buscarOFallar(id);

        pedido.setEstado(nuevoEstado.toUpperCase());

        Pedido actualizado = repository.save(pedido);

        logger.info("Pedido {} actualizado a estado {}.", id, actualizado.getEstado());

        return mapearAResponseDTO(actualizado);

    }

    /**
     * ===========================================================
     * ELIMINAR PEDIDO
     * ===========================================================
     *
     * Elimina un pedido utilizando su ID. Gracias a
     * cascade = ALL / orphanRemoval, sus detalles se
     * eliminan automáticamente.
     */
    public void eliminar(Long id) {

        logger.info("Intentando eliminar pedido {}", id);

        if (!repository.existsById(id)) {

            logger.warn("No se pudo eliminar el pedido {} porque no existe.", id);

            throw new PedidoNotFoundException("Pedido no encontrado.");

        }

        repository.deleteById(id);

        logger.info("Pedido {} eliminado correctamente.", id);

    }

    /**
     * ===========================================================
     * MÉTODOS PRIVADOS DE APOYO
     * ===========================================================
     */

    private Pedido buscarOFallar(Long id) {

        return repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Pedido {} no encontrado.", id);

                    return new PedidoNotFoundException("Pedido no encontrado.");

                });

    }

    private PedidoResponseDTO mapearAResponseDTO(Pedido pedido) {

        List<DetalleResponseDTO> detalles = pedido.getDetalles()

                .stream()

                .map(d -> new DetalleResponseDTO(
                        d.getId(),
                        d.getProductoId(),
                        d.getCantidad(),
                        d.getPrecio(),
                        d.getSubtotal()
                ))

                .collect(Collectors.toList());

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getUsuarioId(),
                pedido.getTotal(),
                pedido.getEstado(),
                pedido.getFechaCreacion(),
                detalles
        );

    }

}

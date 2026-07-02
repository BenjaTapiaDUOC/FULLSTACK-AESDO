package mspedidos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ===========================================================
 * DTO DE RESPUESTA
 * ===========================================================
 *
 * Este objeto se envía como respuesta al cliente.
 *
 * De esta forma no se expone directamente la entidad Pedido
 * ni su relación bidireccional con DetallePedido.
 */

@Data
@AllArgsConstructor
public class PedidoResponseDTO {

    /**
     * Identificador del pedido.
     */
    private Long id;

    /**
     * Identificador del usuario dueño del pedido.
     */
    private Long usuarioId;

    /**
     * Monto total calculado del pedido.
     */
    private Double total;

    /**
     * Estado actual del pedido.
     */
    private String estado;

    /**
     * Fecha de creación del pedido.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Detalles (productos) que componen el pedido.
     */
    private List<DetalleResponseDTO> detalles;

}

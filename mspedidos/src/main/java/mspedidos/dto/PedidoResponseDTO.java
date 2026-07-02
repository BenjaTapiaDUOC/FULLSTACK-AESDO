package mspedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Representa un pedido registrado en el sistema.")
public class PedidoResponseDTO {

    @Schema(description = "Identificador del pedido.", example = "1")
    private Long id;

    @Schema(description = "Identificador del usuario dueño del pedido.", example = "1")
    private Long usuarioId;

    @Schema(description = "Monto total calculado del pedido.", example = "24970.0")
    private Double total;

    @Schema(description = "Estado actual del pedido.", example = "PENDIENTE",
            allowableValues = {"PENDIENTE", "EN_PROCESO", "ENVIADO", "ENTREGADO", "CANCELADO"})
    private String estado;

    @Schema(description = "Fecha de creación del pedido.", example = "2026-07-02T10:00:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Detalles (productos) que componen el pedido.")
    private List<DetalleResponseDTO> detalles;

}

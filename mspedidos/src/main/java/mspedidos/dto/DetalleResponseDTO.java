package mspedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE RESPUESTA - DETALLE DE PEDIDO
 * ===========================================================
 *
 * Representa un producto dentro del arreglo "detalles" que
 * viaja dentro de PedidoResponseDTO.
 */

@Data
@AllArgsConstructor
@Schema(description = "Representa un producto dentro del pedido, ya calculado.")
public class DetalleResponseDTO {

    @Schema(description = "Identificador del detalle.", example = "1")
    private Long id;

    @Schema(description = "Identificador del producto.", example = "10")
    private Long productoId;

    @Schema(description = "Cantidad solicitada.", example = "2")
    private Integer cantidad;

    @Schema(description = "Precio unitario del producto.", example = "5990.0")
    private Double precio;

    @Schema(description = "Subtotal (cantidad * precio).", example = "11980.0")
    private Double subtotal;

}

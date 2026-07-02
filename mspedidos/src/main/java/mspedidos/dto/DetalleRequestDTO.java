package mspedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE ENTRADA - DETALLE DE PEDIDO
 * ===========================================================
 *
 * Representa un producto dentro del arreglo "detalles" que
 * viaja dentro de PedidoRequestDTO.
 */

@Data
@Schema(description = "Representa un producto dentro del pedido.")
public class DetalleRequestDTO {

    @Schema(description = "Identificador del producto.", example = "10")
    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

    @Schema(description = "Cantidad solicitada. Debe ser al menos 1.", example = "2")
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @Schema(description = "Precio unitario del producto. Debe ser mayor a 0.", example = "5990.0")
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private Double precio;

}

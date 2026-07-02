package mspedidos.dto;

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
public class DetalleRequestDTO {

    /**
     * Identificador del producto.
     */
    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

    /**
     * Cantidad solicitada. Debe ser al menos 1.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    /**
     * Precio unitario del producto. Debe ser mayor a 0.
     */
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private Double precio;

}

package mspedidos.dto;

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
public class DetalleResponseDTO {

    private Long id;
    private Long productoId;
    private Integer cantidad;
    private Double precio;
    private Double subtotal;

}

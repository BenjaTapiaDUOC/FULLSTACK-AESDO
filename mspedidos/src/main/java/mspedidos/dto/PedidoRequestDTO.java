package mspedidos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * ===========================================================
 * DTO DE ENTRADA
 * ===========================================================
 *
 * Este DTO recibe la información enviada desde Postman para
 * crear un nuevo pedido.
 *
 * Se utiliza para evitar exponer directamente la entidad
 * Pedido.
 */

@Data
public class PedidoRequestDTO {

    /**
     * Identificador del usuario que realiza el pedido.
     *
     * Se valida contra el microservicio msusuarios antes
     * de crear el pedido.
     */
    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    /**
     * Listado de productos incluidos en el pedido.
     *
     * @NotEmpty obliga a que el pedido tenga al menos un
     * detalle.
     *
     * @Valid asegura que las validaciones de cada
     * DetalleRequestDTO también se apliquen.
     */
    @NotEmpty(message = "El pedido debe tener al menos un detalle")
    @Valid
    private List<DetalleRequestDTO> detalles;

}

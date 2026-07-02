package mspedidos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Datos requeridos para crear un nuevo pedido.")
public class PedidoRequestDTO {

    @Schema(description = "Identificador del usuario que realiza el pedido. Se valida contra msusuarios.", example = "1")
    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @Schema(description = "Listado de productos incluidos en el pedido. Debe tener al menos un detalle.")
    @NotEmpty(message = "El pedido debe tener al menos un detalle")
    @Valid
    private List<DetalleRequestDTO> detalles;

}

package com.example.msdelivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE RESPUESTA
 * ===========================================================
 *
 * Este objeto se envía como respuesta al cliente.
 *
 * De esta forma no se expone directamente la entidad Delivery.
 */

@Data
@AllArgsConstructor
@Schema(description = "Representa un delivery registrado en el sistema.")
public class DeliveryResponseDTO {

    @Schema(description = "Identificador del delivery.", example = "1")
    private Long id;

    @Schema(description = "Identificador del pedido asociado.", example = "1")
    private Long pedidoId;

    @Schema(description = "Dirección de entrega.", example = "Av. Siempre Viva 123")
    private String direccionEntrega;

    @Schema(description = "Repartidor asignado.", example = "Juan Pérez")
    private String repartidor;

    @Schema(description = "Estado actual del delivery.", example = "PENDIENTE")
    private String estado;

}

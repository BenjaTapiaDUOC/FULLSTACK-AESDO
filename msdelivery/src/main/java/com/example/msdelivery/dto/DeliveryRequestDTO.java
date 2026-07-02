package com.example.msdelivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE ENTRADA
 * ===========================================================
 *
 * Este DTO recibe la información enviada desde Postman.
 *
 * Se utiliza para evitar exponer directamente la entidad
 * Delivery.
 */

@Data
@Schema(description = "Datos requeridos para crear o actualizar un delivery.")
public class DeliveryRequestDTO {

    @Schema(description = "Identificador del pedido asociado. Debe existir en mspedidos.", example = "1")
    @NotNull(message = "El pedidoId es obligatorio")
    private Long pedidoId;

    @Schema(description = "Dirección de entrega.", example = "Av. Siempre Viva 123")
    @NotBlank(message = "La dirección de entrega es obligatoria")
    private String direccionEntrega;

    @Schema(description = "Repartidor asignado.", example = "Juan Pérez")
    @NotBlank(message = "El repartidor es obligatorio")
    private String repartidor;

    @Schema(description = "Estado del delivery.", example = "PENDIENTE", allowableValues = {"PENDIENTE", "EN_CAMINO", "ENTREGADO"})
    @NotBlank(message = "El estado es obligatorio")
    private String estado;

}

package com.example.msdelivery.dto;

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
public class DeliveryRequestDTO {

    /**
     * Identificador del pedido asociado.
     */
    @NotNull(message = "El pedidoId es obligatorio")
    private Long pedidoId;

    /**
     * Dirección de entrega.
     */
    @NotBlank(message = "La dirección de entrega es obligatoria")
    private String direccionEntrega;

    /**
     * Repartidor asignado.
     */
    @NotBlank(message = "El repartidor es obligatorio")
    private String repartidor;

    /**
     * Estado del delivery.
     *
     * Valores esperados: PENDIENTE, EN_CAMINO, ENTREGADO.
     */
    @NotBlank(message = "El estado es obligatorio")
    private String estado;

}

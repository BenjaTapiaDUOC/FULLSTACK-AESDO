package com.example.msdelivery.dto;

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
public class DeliveryResponseDTO {

    /**
     * Identificador del delivery.
     */
    private Long id;

    /**
     * Identificador del pedido asociado.
     */
    private Long pedidoId;

    /**
     * Dirección de entrega.
     */
    private String direccionEntrega;

    /**
     * Repartidor asignado.
     */
    private String repartidor;

    /**
     * Estado actual del delivery.
     */
    private String estado;

}

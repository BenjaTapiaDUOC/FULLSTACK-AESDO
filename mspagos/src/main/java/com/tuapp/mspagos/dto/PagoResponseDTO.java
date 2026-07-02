package com.tuapp.mspagos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ===========================================================
 * DTO DE RESPUESTA
 * ===========================================================
 *
 * Este objeto se envía como respuesta al cliente.
 *
 * De esta forma no se expone directamente la entidad Pago.
 */

@Data
@AllArgsConstructor
public class PagoResponseDTO {

    /**
     * Identificador del pago.
     */
    private Long id;

    /**
     * Identificador del pedido asociado.
     */
    private Long pedidoId;

    /**
     * Monto del pago.
     */
    private Double monto;

    /**
     * Método de pago utilizado.
     */
    private String metodoPago;

    /**
     * Estado del pago (PENDIENTE, APROBADO, RECHAZADO).
     */
    private String estado;

    /**
     * Fecha y hora en que se registró el pago.
     */
    private LocalDateTime fechaPago;

}

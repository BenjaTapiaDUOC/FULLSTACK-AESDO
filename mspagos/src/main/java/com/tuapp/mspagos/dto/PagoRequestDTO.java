package com.tuapp.mspagos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE ENTRADA
 * ===========================================================
 *
 * Este DTO recibe la información enviada desde Postman.
 *
 * Se utiliza para evitar exponer directamente la entidad
 * Pago.
 */

@Data
public class PagoRequestDTO {

    /**
     * Identificador del pedido asociado.
     */
    @NotNull(message = "El pedidoId es obligatorio")
    private Long pedidoId;

    /**
     * Monto del pago.
     *
     * Debe ser mayor a cero.
     */
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private Double monto;

    /**
     * Método de pago.
     */
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

}

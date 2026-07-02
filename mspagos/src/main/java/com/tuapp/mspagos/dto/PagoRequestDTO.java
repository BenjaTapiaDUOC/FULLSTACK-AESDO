package com.tuapp.mspagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Datos requeridos para crear o actualizar un pago.")
public class PagoRequestDTO {

    @Schema(description = "Identificador del pedido asociado.", example = "1")
    @NotNull(message = "El pedidoId es obligatorio")
    private Long pedidoId;

    @Schema(description = "Monto del pago. Debe ser mayor a cero.", example = "15000")
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private Double monto;

    @Schema(description = "Método de pago.", example = "TARJETA")
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

}

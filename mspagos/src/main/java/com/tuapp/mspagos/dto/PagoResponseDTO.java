package com.tuapp.mspagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Representa un pago registrado en el sistema.")
public class PagoResponseDTO {

    @Schema(description = "Identificador del pago.", example = "1")
    private Long id;

    @Schema(description = "Identificador del pedido asociado.", example = "1")
    private Long pedidoId;

    @Schema(description = "Monto del pago.", example = "15000")
    private Double monto;

    @Schema(description = "Método de pago utilizado.", example = "TARJETA")
    private String metodoPago;

    @Schema(description = "Estado del pago.", example = "APROBADO", allowableValues = {"PENDIENTE", "APROBADO", "RECHAZADO"})
    private String estado;

    @Schema(description = "Fecha y hora en que se registró el pago.", example = "2026-07-02T10:15:30")
    private LocalDateTime fechaPago;

}

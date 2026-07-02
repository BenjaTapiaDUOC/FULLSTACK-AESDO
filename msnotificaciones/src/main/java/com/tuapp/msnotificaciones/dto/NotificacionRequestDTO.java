package com.tuapp.msnotificaciones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE ENTRADA
 * ===========================================================
 *
 * Este DTO recibe la información enviada desde Postman u otro
 * microservicio (mspagos, mspedidos, msdelivery) al generar
 * una notificación.
 *
 * Se utiliza para evitar exponer directamente la entidad
 * Notificacion.
 */

@Data
@Schema(description = "Datos requeridos para crear una notificación.")
public class NotificacionRequestDTO {

    @Schema(description = "Identificador del usuario (msusuarios) que recibirá la notificación.", example = "1")
    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @Schema(description = "Tipo de notificación.", example = "PAGO_APROBADO")
    @NotBlank(message = "El tipo de notificación es obligatorio")
    private String tipo;

    @Schema(description = "Mensaje descriptivo de la notificación.", example = "Tu pago fue aprobado exitosamente.")
    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    @Schema(description = "Microservicio de origen que generó el evento.", example = "PAGOS", allowableValues = {"PAGOS", "PEDIDOS", "DELIVERY"})
    @NotBlank(message = "El origen es obligatorio")
    private String origen;

    @Schema(description = "Identificador de la entidad de origen (id del pago, pedido o delivery) que generó el evento.", example = "10")
    @NotNull(message = "La referenciaId es obligatoria")
    private Long referenciaId;

}

package com.tuapp.msnotificaciones.dto;

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
 * De esta forma no se expone directamente la entidad
 * Notificacion.
 */

@Data
@AllArgsConstructor
@Schema(description = "Representa una notificación registrada en el sistema.")
public class NotificacionResponseDTO {

    @Schema(description = "Identificador de la notificación.", example = "1")
    private Long id;

    @Schema(description = "Identificador del usuario dueño de la notificación.", example = "1")
    private Long usuarioId;

    @Schema(description = "Tipo de notificación.", example = "PAGO_APROBADO")
    private String tipo;

    @Schema(description = "Mensaje descriptivo de la notificación.", example = "Tu pago fue aprobado exitosamente.")
    private String mensaje;

    @Schema(description = "Microservicio de origen que generó el evento.", example = "PAGOS")
    private String origen;

    @Schema(description = "Identificador de la entidad de origen del evento.", example = "10")
    private Long referenciaId;

    @Schema(description = "Fecha y hora en que se generó la notificación.", example = "2026-07-02T10:20:00")
    private LocalDateTime fechaEnvio;

    @Schema(description = "Indica si la notificación ya fue leída.", example = "false")
    private boolean leida;

}

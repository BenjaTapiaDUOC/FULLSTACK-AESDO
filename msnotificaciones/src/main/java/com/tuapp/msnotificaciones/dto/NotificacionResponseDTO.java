package com.tuapp.msnotificaciones.dto;

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
public class NotificacionResponseDTO {

    /**
     * Identificador de la notificación.
     */
    private Long id;

    /**
     * Identificador del usuario dueño de la notificación.
     */
    private Long usuarioId;

    /**
     * Tipo de notificación.
     */
    private String tipo;

    /**
     * Mensaje descriptivo de la notificación.
     */
    private String mensaje;

    /**
     * Microservicio de origen que generó el evento.
     */
    private String origen;

    /**
     * Identificador de la entidad de origen del evento.
     */
    private Long referenciaId;

    /**
     * Fecha y hora en que se generó la notificación.
     */
    private LocalDateTime fechaEnvio;

    /**
     * Indica si la notificación ya fue leída.
     */
    private boolean leida;

}

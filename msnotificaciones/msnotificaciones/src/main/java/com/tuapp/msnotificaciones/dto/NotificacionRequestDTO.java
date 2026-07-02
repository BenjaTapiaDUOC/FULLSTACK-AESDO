package com.tuapp.msnotificaciones.dto;

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
public class NotificacionRequestDTO {

    /**
     * Identificador del usuario (msusuarios) que recibirá
     * la notificación.
     */
    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    /**
     * Tipo de notificación.
     *
     * Ejemplos: PAGO_APROBADO, PEDIDO_ENVIADO, DELIVERY_ASIGNADO.
     */
    @NotBlank(message = "El tipo de notificación es obligatorio")
    private String tipo;

    /**
     * Mensaje descriptivo de la notificación.
     */
    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    /**
     * Microservicio de origen que generó el evento.
     *
     * Valores permitidos: PAGOS, PEDIDOS, DELIVERY.
     */
    @NotBlank(message = "El origen es obligatorio")
    private String origen;

    /**
     * Identificador de la entidad de origen (id del pago,
     * pedido o delivery) que generó el evento.
     */
    @NotNull(message = "La referenciaId es obligatoria")
    private Long referenciaId;

}

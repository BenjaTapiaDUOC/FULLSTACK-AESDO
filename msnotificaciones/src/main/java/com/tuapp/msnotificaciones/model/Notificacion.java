package com.tuapp.msnotificaciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ===========================================================
 * ENTIDAD NOTIFICACION
 * ===========================================================
 *
 * Esta clase representa la tabla "notificaciones" en la base
 * de datos.
 *
 * Cada atributo corresponde a una columna.
 *
 * Spring Data JPA utilizará esta entidad para realizar las
 * operaciones CRUD automáticamente.
 *
 * Una notificación se genera cuando otro microservicio
 * (mspagos, mspedidos o msdelivery) informa un cambio de
 * estado relevante para un usuario (por ejemplo: pago
 * aprobado, pedido enviado, delivery asignado, etc.).
 */

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    /**
     * Identificador único de la notificación.
     * Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador del usuario (msusuarios) al cual
     * pertenece la notificación.
     */
    @Column(nullable = false)
    private Long usuarioId;

    /**
     * Tipo de notificación.
     *
     * Ejemplos: PAGO_APROBADO, PEDIDO_ENVIADO,
     * DELIVERY_ASIGNADO, PROMOCION_APLICADA, etc.
     */
    @Column(nullable = false)
    private String tipo;

    /**
     * Mensaje descriptivo que será mostrado al usuario.
     */
    @Column(nullable = false, length = 500)
    private String mensaje;

    /**
     * Microservicio de origen que generó el evento.
     *
     * Valores permitidos: PAGOS, PEDIDOS, DELIVERY.
     */
    @Column(nullable = false)
    private String origen;

    /**
     * Identificador de la entidad de origen que generó el
     * evento (por ejemplo: el id del pago, del pedido o del
     * delivery).
     *
     * Junto a "origen", "tipo" y "usuarioId" permite detectar
     * notificaciones duplicadas del mismo evento.
     */
    @Column(nullable = false)
    private Long referenciaId;

    /**
     * Fecha y hora en que se generó la notificación.
     * Se asigna automáticamente al momento de la creación.
     */
    @Column(nullable = false)
    private LocalDateTime fechaEnvio;

    /**
     * Indica si el usuario ya leyó la notificación.
     *
     * Regla de negocio: una notificación solo puede marcarse
     * como leída una vez.
     */
    @Column(nullable = false)
    private boolean leida;

}

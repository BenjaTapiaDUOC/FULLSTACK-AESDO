package com.example.msdelivery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================
 * ENTIDAD DELIVERY
 * ===========================================================
 *
 * Esta clase representa la tabla "delivery" en la base de datos.
 *
 * Cada atributo corresponde a una columna.
 *
 * Spring Data JPA utilizará esta entidad para realizar las
 * operaciones CRUD automáticamente.
 */

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    /**
     * Identificador único del delivery.
     * Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador del pedido asociado a este delivery.
     * Cada pedido solo puede tener un delivery asignado.
     */
    @Column(nullable = false, unique = true)
    private Long pedidoId;

    /**
     * Dirección donde se realizará la entrega.
     */
    @Column(nullable = false)
    private String direccionEntrega;

    /**
     * Nombre del repartidor asignado al delivery.
     */
    @Column(nullable = false)
    private String repartidor;

    /**
     * Estado actual del delivery.
     *
     * Valores esperados: PENDIENTE, EN_CAMINO, ENTREGADO.
     */
    @Column(nullable = false)
    private String estado;

}

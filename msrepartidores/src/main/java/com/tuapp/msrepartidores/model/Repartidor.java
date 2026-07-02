package com.tuapp.msrepartidores.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================
 * ENTIDAD REPARTIDOR
 * ===========================================================
 *
 * Esta clase representa la tabla "repartidores" en la base
 * de datos.
 *
 * Cada atributo corresponde a una columna.
 *
 * Spring Data JPA utilizará esta entidad para realizar las
 * operaciones CRUD automáticamente.
 */

@Entity
@Table(name = "repartidores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Repartidor {

    /**
     * Identificador único del repartidor.
     * Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre completo del repartidor.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Tipo de vehículo utilizado por el repartidor.
     *
     * Ejemplos: Moto, Bicicleta, Auto.
     */
    @Column(nullable = false)
    private String vehiculo;

    /**
     * Estado actual del repartidor.
     *
     * Valores posibles: DISPONIBLE, EN_RUTA, INACTIVO.
     *
     * Este campo es consultado por msdelivery para validar
     * si el repartidor puede ser asignado a un nuevo delivery.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoRepartidor estado;

}

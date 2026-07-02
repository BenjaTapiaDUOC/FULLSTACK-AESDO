package com.tuapp.mspromociones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * ===========================================================
 * ENTIDAD PROMOCION
 * ===========================================================
 *
 * Esta clase representa la tabla "promociones" en la base de datos.
 *
 * Cada atributo corresponde a una columna.
 *
 * Spring Data JPA utilizará esta entidad para realizar las
 * operaciones CRUD automáticamente.
 */

@Entity
@Table(name = "promociones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promocion {

    /**
     * Identificador único de la promoción.
     * Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código del cupón de descuento.
     * Debe ser único dentro del sistema.
     */
    @Column(nullable = false, unique = true)
    private String codigo;

    /**
     * Porcentaje de descuento asociado al cupón.
     *
     * Valor entre 0 y 100.
     */
    @Column(nullable = false)
    private Double porcentajeDescuento;

    /**
     * Fecha desde la cual el cupón comienza a ser válido.
     */
    @Column(nullable = false)
    private LocalDate fechaInicio;

    /**
     * Fecha hasta la cual el cupón es válido.
     *
     * Si la fecha actual supera este valor, el cupón
     * se considera vencido.
     */
    @Column(nullable = false)
    private LocalDate fechaFin;

    /**
     * Estado de la promoción.
     *
     * true  -> el cupón está disponible para ser utilizado.
     * false -> el cupón fue utilizado o fue desactivado
     *          manualmente.
     */
    @Column(nullable = false)
    private Boolean activo;

}

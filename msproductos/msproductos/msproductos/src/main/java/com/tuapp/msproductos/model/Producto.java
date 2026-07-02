package com.tuapp.msproductos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================
 * ENTIDAD PRODUCTO
 * ===========================================================
 *
 * Esta clase representa la tabla "productos" en la base de datos.
 *
 * Cada atributo corresponde a una columna.
 *
 * Spring Data JPA utilizará esta entidad para realizar las
 * operaciones CRUD automáticamente.
 */

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    /**
     * Identificador único del producto.
     * Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del producto (ej: "Pizza Napolitana").
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Precio de venta del producto.
     */
    @Column(nullable = false)
    private Double precio;

    /**
     * Categoría a la que pertenece (ej: "Comida rápida", "Bebidas").
     */
    @Column(nullable = false)
    private String categoria;

}
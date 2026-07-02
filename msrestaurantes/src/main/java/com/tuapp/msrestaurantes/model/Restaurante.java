package com.tuapp.msrestaurantes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================
 * ENTIDAD RESTAURANTE
 * ===========================================================
 *
 * Esta clase representa la tabla "restaurantes" en la base de datos.
 *
 * Cada atributo corresponde a una columna.
 *
 * Spring Data JPA utilizará esta entidad para realizar las
 * operaciones CRUD automáticamente.
 *
 * Este microservicio actúa como raíz del dominio: el
 * microservicio msproductos agrega un campo "restauranteId"
 * (FK) y valida contra este servicio (vía WebClient) que el
 * restaurante exista y se encuentre activo antes de permitir
 * la creación de un producto asociado.
 */

@Entity
@Table(name = "restaurantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurante {

    /**
     * Identificador único del restaurante.
     * Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del restaurante.
     * Debe ser único dentro del sistema.
     */
    @Column(nullable = false, unique = true)
    private String nombre;

    /**
     * Dirección física del restaurante.
     */
    @Column(nullable = false)
    private String direccion;

    /**
     * Categoría o tipo de cocina que ofrece el restaurante.
     *
     * Ejemplos: "Italiana", "Comida rápida", "Vegana", "Sushi".
     */
    @Column(nullable = false)
    private String categoria;

    /**
     * Horario de atención del restaurante.
     *
     * Ejemplo: "09:00 - 22:00".
     */
    @Column(nullable = false)
    private String horario;

    /**
     * Estado del restaurante.
     *
     * true  -> ACTIVO (puede recibir productos, pedidos, etc.)
     * false -> INACTIVO (no puede recibir nuevos productos)
     */
    @Column(nullable = false)
    private Boolean activo;

}

package com.tuapp.msusuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================
 * ENTIDAD USUARIO
 * ===========================================================
 *
 * Esta clase representa la tabla "usuario" en la base de datos.
 *
 * Cada atributo corresponde a una columna.
 *
 * Spring Data JPA utilizará esta entidad para realizar las
 * operaciones CRUD automáticamente.
 */

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    /**
     * Identificador único del usuario.
     * Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del usuario.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Correo electrónico.
     * Debe ser único dentro del sistema.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Contraseña del usuario.
     *
     * En un proyecto real debería almacenarse encriptada.
     */
    @Column(nullable = false)
    private String password;

}
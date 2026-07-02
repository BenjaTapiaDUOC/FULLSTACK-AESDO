package com.tuapp.msautenticacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================
 * ENTIDAD USUARIO (AUTENTICACIÓN)
 * ===========================================================
 *
 * Esta clase representa la tabla "usuarios_auth" en la base
 * de datos del microservicio de autenticación.
 *
 * IMPORTANTE:
 *
 * Este microservicio NO reemplaza a msusuarios. La entidad
 * "maestra" del usuario (nombre, email, password) sigue
 * viviendo en msusuarios.
 *
 * Esta entidad guarda una REFERENCIA al usuario real
 * (usuarioId) creado en msusuarios mediante comunicación
 * WebClient, además de una copia local de las credenciales
 * y el rol, necesarias para poder validar el login y generar
 * el JWT sin depender de una llamada remota en cada
 * autenticación.
 *
 * Relación:
 *
 * Usuario (N) -----> (1) Rol   -> @ManyToOne
 */

@Entity
@Table(name = "usuarios_auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    /**
     * Identificador único del registro de autenticación.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID del usuario real, creado y administrado por
     * el microservicio msusuarios.
     */
    @Column(nullable = false)
    private Long usuarioId;

    /**
     * Nombre del usuario (copia local, útil para el token
     * y para no depender de otra llamada remota).
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Correo electrónico.
     * Debe ser único dentro del sistema de autenticación.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Contraseña del usuario.
     *
     * En un proyecto real debería almacenarse encriptada
     * (BCrypt). Se mantiene en texto plano únicamente para
     * los requerimientos académicos del proyecto, igual que
     * en msusuarios.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Rol asignado al usuario.
     *
     * Relación ManyToOne: muchos usuarios pueden tener
     * el mismo rol.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

}

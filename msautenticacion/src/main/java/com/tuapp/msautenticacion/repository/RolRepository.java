package com.tuapp.msautenticacion.repository;

import com.tuapp.msautenticacion.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ===========================================================
 * REPOSITORY DE ROLES
 * ===========================================================
 *
 * Esta interfaz permite comunicarse con la tabla "roles".
 *
 * Se agregan consultas personalizadas para validar el rol
 * enviado durante el registro de un usuario.
 */

public interface RolRepository extends JpaRepository<Rol, Long> {

    /**
     * Buscar un rol por su nombre (ADMIN, CLIENTE, REPARTIDOR).
     */
    Optional<Rol> findByNombre(String nombre);

    /**
     * Verificar si un rol ya existe.
     *
     * Se utiliza en el inicializador de roles y en la
     * validación de registro.
     */
    boolean existsByNombre(String nombre);

}

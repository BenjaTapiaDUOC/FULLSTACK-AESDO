package com.tuapp.msautenticacion.repository;

import com.tuapp.msautenticacion.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ===========================================================
 * REPOSITORY DE USUARIOS (AUTENTICACIÓN)
 * ===========================================================
 *
 * Esta interfaz permite comunicarse con la base de datos.
 *
 * JpaRepository proporciona automáticamente métodos como:
 *
 * save()
 * findAll()
 * findById()
 * deleteById()
 * existsById()
 *
 * Además se agregan consultas personalizadas.
 */

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Buscar usuario de autenticación mediante el correo.
     *
     * Se utiliza en el proceso de login.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verificar si un correo ya existe.
     *
     * Se utiliza para evitar registros duplicados.
     */
    boolean existsByEmail(String email);

}

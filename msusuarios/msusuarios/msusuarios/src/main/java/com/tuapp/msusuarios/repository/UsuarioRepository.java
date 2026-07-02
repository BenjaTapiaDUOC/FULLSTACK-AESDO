package com.tuapp.msusuarios.repository;

import com.tuapp.msusuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ===========================================================
 * REPOSITORY DE USUARIOS
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
     * Buscar usuario mediante el correo electrónico.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verificar si un correo ya existe.
     *
     * Se utiliza para evitar usuarios duplicados.
     */
    boolean existsByEmail(String email);

}
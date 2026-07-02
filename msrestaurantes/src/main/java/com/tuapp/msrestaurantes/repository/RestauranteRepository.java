package com.tuapp.msrestaurantes.repository;

import com.tuapp.msrestaurantes.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * ===========================================================
 * REPOSITORY DE RESTAURANTES
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

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    /**
     * Buscar restaurante mediante su nombre.
     */
    Optional<Restaurante> findByNombre(String nombre);

    /**
     * Verificar si un nombre de restaurante ya existe.
     *
     * Se utiliza para evitar restaurantes duplicados.
     */
    boolean existsByNombre(String nombre);

    /**
     * Listar únicamente los restaurantes activos.
     *
     * Es útil para otros microservicios (por ejemplo
     * msproductos) que solo deben trabajar con
     * restaurantes disponibles.
     */
    List<Restaurante> findByActivoTrue();

}

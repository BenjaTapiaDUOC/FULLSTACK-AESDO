package com.tuapp.mspromociones.repository;

import com.tuapp.mspromociones.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ===========================================================
 * REPOSITORY DE PROMOCIONES
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

public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    /**
     * Buscar una promoción mediante su código.
     *
     * Se utiliza principalmente para validar cupones
     * al momento de aplicarlos a un pedido.
     */
    Optional<Promocion> findByCodigo(String codigo);

    /**
     * Verificar si un código de promoción ya existe.
     *
     * Se utiliza para evitar cupones duplicados.
     */
    boolean existsByCodigo(String codigo);

}

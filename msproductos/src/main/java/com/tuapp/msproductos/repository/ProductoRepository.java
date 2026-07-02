package com.tuapp.msproductos.repository;

import com.tuapp.msproductos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ===========================================================
 * REPOSITORY DE PRODUCTOS
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
 * Además se agrega una consulta personalizada.
 */

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Verificar si ya existe un producto con el mismo nombre.
     *
     * Se utiliza para evitar productos duplicados en el catálogo.
     */
    boolean existsByNombreIgnoreCase(String nombre);

}
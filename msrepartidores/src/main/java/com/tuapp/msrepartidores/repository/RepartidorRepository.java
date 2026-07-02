package com.tuapp.msrepartidores.repository;

import com.tuapp.msrepartidores.model.EstadoRepartidor;
import com.tuapp.msrepartidores.model.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * ===========================================================
 * REPOSITORY DE REPARTIDORES
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

public interface RepartidorRepository extends JpaRepository<Repartidor, Long> {

    /**
     * Buscar repartidores según su estado actual.
     *
     * Es utilizada, por ejemplo, para listar únicamente
     * los repartidores DISPONIBLE.
     */
    List<Repartidor> findByEstado(EstadoRepartidor estado);

}

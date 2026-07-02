package com.example.msdelivery.repository;

import com.example.msdelivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ===========================================================
 * REPOSITORY DE DELIVERY
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

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    /**
     * Buscar delivery mediante el ID del pedido asociado.
     */
    Optional<Delivery> findByPedidoId(Long pedidoId);

    /**
     * Verificar si un pedido ya tiene un delivery asignado.
     *
     * Se utiliza para evitar deliveries duplicados sobre
     * el mismo pedido.
     */
    boolean existsByPedidoId(Long pedidoId);

}

package com.tuapp.mspagos.repository;

import com.tuapp.mspagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * ===========================================================
 * REPOSITORY DE PAGOS
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

public interface PagoRepository extends JpaRepository<Pago, Long> {

    /**
     * Buscar todos los pagos asociados a un pedido.
     */
    List<Pago> findByPedidoId(Long pedidoId);

}

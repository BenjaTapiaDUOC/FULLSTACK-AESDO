package mspedidos.repository;

import mspedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * ===========================================================
 * REPOSITORY DE PEDIDOS
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

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Buscar todos los pedidos realizados por un usuario.
     */
    List<Pedido> findByUsuarioId(Long usuarioId);

}

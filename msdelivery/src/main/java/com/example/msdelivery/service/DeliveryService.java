package com.example.msdelivery.service;

import com.example.msdelivery.client.PedidoClient;
import com.example.msdelivery.dto.DeliveryRequestDTO;
import com.example.msdelivery.dto.DeliveryResponseDTO;
import com.example.msdelivery.exception.BadRequestException;
import com.example.msdelivery.exception.DeliveryNotFoundException;
import com.example.msdelivery.model.Delivery;
import com.example.msdelivery.repository.DeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ===========================================================
 * SERVICIO DE DELIVERY
 * ===========================================================
 *
 * Aquí se implementa toda la lógica de negocio.
 *
 * El Controller solamente llama a estos métodos.
 *
 * Esta clase se comunica directamente con el Repository.
 */

@Service
public class DeliveryService {

    /**
     * Logger del microservicio.
     * Permite registrar eventos importantes durante la ejecución.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRepository repository;

    private final PedidoClient pedidoClient;

    public DeliveryService(DeliveryRepository repository, PedidoClient pedidoClient) {
        this.repository = repository;
        this.pedidoClient = pedidoClient;
    }

    /**
     * ===========================================================
     * CREAR DELIVERY
     * ===========================================================
     *
     * Este metodo:
     *
     * 1. Verifica que el pedido no tenga un delivery ya asignado.
     * 2. Crea la entidad Delivery.
     * 3. Guarda el delivery en la base de datos.
     * 4. Devuelve un DTO como respuesta.
     */
    public DeliveryResponseDTO crearDelivery(DeliveryRequestDTO dto) {

        logger.info("Intentando crear delivery para el pedido: {}", dto.getPedidoId());

        // Validación remota: el pedido debe existir en mspedidos
        pedidoClient.obtenerPedido(dto.getPedidoId());

        // Validación para evitar deliveries duplicados sobre el mismo pedido
        if (repository.existsByPedidoId(dto.getPedidoId())) {

            logger.warn("El pedido {} ya tiene un delivery asignado", dto.getPedidoId());

            throw new BadRequestException(
                    "El pedido ya tiene un delivery asignado."
            );

        }

        Delivery delivery = new Delivery(
                null,
                dto.getPedidoId(),
                dto.getDireccionEntrega(),
                dto.getRepartidor(),
                dto.getEstado()
        );

        Delivery guardado = repository.save(delivery);

        logger.info("Delivery {} creado correctamente.", guardado.getId());

        return new DeliveryResponseDTO(
                guardado.getId(),
                guardado.getPedidoId(),
                guardado.getDireccionEntrega(),
                guardado.getRepartidor(),
                guardado.getEstado()
        );

    }

    /**
     * ===========================================================
     * LISTAR DELIVERIES
     * ===========================================================
     *
     * Obtiene todos los deliveries registrados.
     */
    public List<DeliveryResponseDTO> listarDeliveries() {

        logger.info("Consultando listado completo de deliveries.");

        return repository.findAll()

                .stream()

                .map(delivery -> new DeliveryResponseDTO(

                        delivery.getId(),

                        delivery.getPedidoId(),

                        delivery.getDireccionEntrega(),

                        delivery.getRepartidor(),

                        delivery.getEstado()

                ))

                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * BUSCAR DELIVERY POR ID
     * ===========================================================
     *
     * Busca un delivery utilizando su identificador.
     */
    public DeliveryResponseDTO obtenerPorId(Long id) {

        logger.info("Buscando delivery con ID {}", id);

        Delivery delivery = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Delivery {} no encontrado.", id);

                    return new DeliveryNotFoundException(
                            "Delivery no encontrado."
                    );

                });

        return new DeliveryResponseDTO(

                delivery.getId(),

                delivery.getPedidoId(),

                delivery.getDireccionEntrega(),

                delivery.getRepartidor(),

                delivery.getEstado()

        );

    }

    /**
     * ===========================================================
     * ACTUALIZAR DELIVERY
     * ===========================================================
     *
     * Actualiza la información de un delivery existente.
     */
    public DeliveryResponseDTO actualizar(Long id, DeliveryRequestDTO dto) {

        logger.info("Actualizando delivery {}", id);

        Delivery delivery = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Delivery {} no encontrado.", id);

                    return new DeliveryNotFoundException(
                            "Delivery no encontrado."
                    );

                });

        // Si el pedidoId cambia, se revalida contra mspedidos y contra duplicados
        if (!delivery.getPedidoId().equals(dto.getPedidoId())) {

            pedidoClient.obtenerPedido(dto.getPedidoId());

            if (repository.existsByPedidoId(dto.getPedidoId())) {

                logger.warn("Intento de actualizar con un pedido que ya tiene delivery asignado.");

                throw new BadRequestException(
                        "El pedido ya tiene un delivery asignado a otro registro."
                );

            }

        }

        delivery.setPedidoId(dto.getPedidoId());

        delivery.setDireccionEntrega(dto.getDireccionEntrega());

        delivery.setRepartidor(dto.getRepartidor());

        delivery.setEstado(dto.getEstado());

        Delivery actualizado = repository.save(delivery);

        logger.info("Delivery {} actualizado correctamente.", id);

        return new DeliveryResponseDTO(

                actualizado.getId(),

                actualizado.getPedidoId(),

                actualizado.getDireccionEntrega(),

                actualizado.getRepartidor(),

                actualizado.getEstado()

        );

    }

    /**
     * ===========================================================
     * ELIMINAR DELIVERY
     * ===========================================================
     *
     * Elimina un delivery utilizando su ID.
     */
    public void eliminar(Long id) {

        logger.info("Intentando eliminar delivery {}", id);

        if (!repository.existsById(id)) {

            logger.warn("No se pudo eliminar el delivery {} porque no existe.", id);

            throw new DeliveryNotFoundException(
                    "Delivery no encontrado."
            );

        }

        repository.deleteById(id);

        logger.info("Delivery {} eliminado correctamente.", id);

    }

}

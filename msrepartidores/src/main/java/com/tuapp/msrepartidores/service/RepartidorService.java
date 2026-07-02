package com.tuapp.msrepartidores.service;

import com.tuapp.msrepartidores.dto.CambioEstadoRequestDTO;
import com.tuapp.msrepartidores.dto.RepartidorRequestDTO;
import com.tuapp.msrepartidores.dto.RepartidorResponseDTO;
import com.tuapp.msrepartidores.exception.BadRequestException;
import com.tuapp.msrepartidores.exception.RepartidorNotFoundException;
import com.tuapp.msrepartidores.model.EstadoRepartidor;
import com.tuapp.msrepartidores.model.Repartidor;
import com.tuapp.msrepartidores.repository.RepartidorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ===========================================================
 * SERVICIO DE REPARTIDORES
 * ===========================================================
 *
 * Aquí se implementa toda la lógica de negocio.
 *
 * El Controller solamente llama a estos métodos.
 *
 * Esta clase se comunica directamente con el Repository.
 */

@Service
public class RepartidorService {

    /**
     * Logger del microservicio.
     * Permite registrar eventos importantes durante la ejecución.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(RepartidorService.class);

    private final RepartidorRepository repository;

    public RepartidorService(RepartidorRepository repository) {
        this.repository = repository;
    }

    /**
     * ===========================================================
     * CREAR REPARTIDOR
     * ===========================================================
     *
     * Este método:
     *
     * 1. Crea la entidad Repartidor.
     * 2. Asigna el estado inicial DISPONIBLE.
     * 3. Guarda el repartidor en la base de datos.
     * 4. Devuelve un DTO como respuesta.
     */
    public RepartidorResponseDTO crearRepartidor(RepartidorRequestDTO dto) {

        logger.info("Intentando crear repartidor con nombre: {}", dto.getNombre());

        Repartidor repartidor = new Repartidor(
                null,
                dto.getNombre(),
                dto.getVehiculo(),
                EstadoRepartidor.DISPONIBLE
        );

        Repartidor guardado = repository.save(repartidor);

        logger.info("Repartidor {} creado correctamente con estado DISPONIBLE.", guardado.getId());

        return new RepartidorResponseDTO(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getVehiculo(),
                guardado.getEstado()
        );

    }

    /**
     * ===========================================================
     * LISTAR REPARTIDORES
     * ===========================================================
     *
     * Obtiene todos los repartidores registrados.
     */
    public List<RepartidorResponseDTO> listarRepartidores() {

        logger.info("Consultando listado completo de repartidores.");

        return repository.findAll()

                .stream()

                .map(repartidor -> new RepartidorResponseDTO(

                        repartidor.getId(),

                        repartidor.getNombre(),

                        repartidor.getVehiculo(),

                        repartidor.getEstado()

                ))

                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * LISTAR REPARTIDORES DISPONIBLES
     * ===========================================================
     *
     * Obtiene únicamente los repartidores en estado DISPONIBLE.
     *
     * Este endpoint está pensado para ser consumido por
     * msdelivery al momento de asignar un nuevo delivery.
     */
    public List<RepartidorResponseDTO> listarDisponibles() {

        logger.info("Consultando repartidores en estado DISPONIBLE.");

        return repository.findByEstado(EstadoRepartidor.DISPONIBLE)

                .stream()

                .map(repartidor -> new RepartidorResponseDTO(

                        repartidor.getId(),

                        repartidor.getNombre(),

                        repartidor.getVehiculo(),

                        repartidor.getEstado()

                ))

                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * BUSCAR REPARTIDOR POR ID
     * ===========================================================
     *
     * Busca un repartidor utilizando su identificador.
     */
    public RepartidorResponseDTO obtenerPorId(Long id) {

        logger.info("Buscando repartidor con ID {}", id);

        Repartidor repartidor = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Repartidor {} no encontrado.", id);

                    return new RepartidorNotFoundException(
                            "Repartidor no encontrado."
                    );

                });

        return new RepartidorResponseDTO(

                repartidor.getId(),

                repartidor.getNombre(),

                repartidor.getVehiculo(),

                repartidor.getEstado()

        );

    }

    /**
     * ===========================================================
     * ACTUALIZAR REPARTIDOR
     * ===========================================================
     *
     * Actualiza el nombre y el vehículo de un repartidor
     * existente.
     *
     * El estado NO se modifica en este método, ya que cuenta
     * con su propio endpoint dedicado.
     */
    public RepartidorResponseDTO actualizar(Long id, RepartidorRequestDTO dto) {

        logger.info("Actualizando repartidor {}", id);

        Repartidor repartidor = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Repartidor {} no encontrado.", id);

                    return new RepartidorNotFoundException(
                            "Repartidor no encontrado."
                    );

                });

        repartidor.setNombre(dto.getNombre());

        repartidor.setVehiculo(dto.getVehiculo());

        Repartidor actualizado = repository.save(repartidor);

        logger.info("Repartidor {} actualizado correctamente.", id);

        return new RepartidorResponseDTO(

                actualizado.getId(),

                actualizado.getNombre(),

                actualizado.getVehiculo(),

                actualizado.getEstado()

        );

    }

    /**
     * ===========================================================
     * CAMBIAR ESTADO DEL REPARTIDOR
     * ===========================================================
     *
     * Actualiza únicamente el estado del repartidor.
     *
     * Regla de negocio:
     *
     * Este endpoint es utilizado por msdelivery para marcar
     * al repartidor como EN_RUTA al asignarle un delivery,
     * y para devolverlo a DISPONIBLE una vez finalizada
     * la entrega.
     */
    public RepartidorResponseDTO cambiarEstado(Long id, CambioEstadoRequestDTO dto) {

        logger.info("Cambiando estado del repartidor {} a {}", id, dto.getEstado());

        Repartidor repartidor = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Repartidor {} no encontrado.", id);

                    return new RepartidorNotFoundException(
                            "Repartidor no encontrado."
                    );

                });

        repartidor.setEstado(dto.getEstado());

        Repartidor actualizado = repository.save(repartidor);

        logger.info("Repartidor {} ahora se encuentra en estado {}.", id, actualizado.getEstado());

        return new RepartidorResponseDTO(

                actualizado.getId(),

                actualizado.getNombre(),

                actualizado.getVehiculo(),

                actualizado.getEstado()

        );

    }

    /**
     * ===========================================================
     * ELIMINAR REPARTIDOR
     * ===========================================================
     *
     * Elimina un repartidor utilizando su ID.
     *
     * Regla de negocio:
     *
     * No se permite eliminar un repartidor que se encuentra
     * actualmente EN_RUTA, ya que tiene una entrega activa
     * asociada.
     */
    public void eliminar(Long id) {

        logger.info("Intentando eliminar repartidor {}", id);

        Repartidor repartidor = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("No se pudo eliminar el repartidor {} porque no existe.", id);

                    return new RepartidorNotFoundException(
                            "Repartidor no encontrado."
                    );

                });

        if (repartidor.getEstado() == EstadoRepartidor.EN_RUTA) {

            logger.warn("No se pudo eliminar el repartidor {} porque se encuentra EN_RUTA.", id);

            throw new BadRequestException(
                    "No se puede eliminar un repartidor que se encuentra en ruta."
            );

        }

        repository.deleteById(id);

        logger.info("Repartidor {} eliminado correctamente.", id);

    }

}

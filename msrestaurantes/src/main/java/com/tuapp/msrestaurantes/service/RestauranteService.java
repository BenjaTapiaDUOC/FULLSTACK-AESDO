package com.tuapp.msrestaurantes.service;

import com.tuapp.msrestaurantes.dto.RestauranteRequestDTO;
import com.tuapp.msrestaurantes.dto.RestauranteResponseDTO;
import com.tuapp.msrestaurantes.exception.BadRequestException;
import com.tuapp.msrestaurantes.exception.RestauranteNotFoundException;
import com.tuapp.msrestaurantes.model.Restaurante;
import com.tuapp.msrestaurantes.repository.RestauranteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ===========================================================
 * SERVICIO DE RESTAURANTES
 * ===========================================================
 *
 * Aquí se implementa toda la lógica de negocio.
 *
 * El Controller solamente llama a estos métodos.
 *
 * Esta clase se comunica directamente con el Repository.
 */

@Service
public class RestauranteService {

    /**
     * Logger del microservicio.
     * Permite registrar eventos importantes durante la ejecución.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(RestauranteService.class);

    private final RestauranteRepository repository;

    public RestauranteService(RestauranteRepository repository) {
        this.repository = repository;
    }

    /**
     * ===========================================================
     * CREAR RESTAURANTE
     * ===========================================================
     *
     * Este metodo:
     *
     * 1. Verifica que el nombre no exista.
     * 2. Crea la entidad Restaurante.
     * 3. Si no se envía el campo "activo", se asigna true por defecto.
     * 4. Guarda el restaurante en la base de datos.
     * 5. Devuelve un DTO como respuesta.
     */
    public RestauranteResponseDTO crearRestaurante(RestauranteRequestDTO dto) {

        logger.info("Intentando crear restaurante con nombre: {}", dto.getNombre());

        // Validación para evitar restaurantes duplicados
        if (repository.existsByNombre(dto.getNombre())) {

            logger.warn("Ya existe un restaurante registrado con el nombre {}", dto.getNombre());

            throw new BadRequestException(
                    "Ya existe un restaurante registrado con ese nombre."
            );

        }

        Boolean estadoInicial = dto.getActivo() == null ? Boolean.TRUE : dto.getActivo();

        Restaurante restaurante = new Restaurante(
                null,
                dto.getNombre(),
                dto.getDireccion(),
                dto.getCategoria(),
                dto.getHorario(),
                estadoInicial
        );

        Restaurante guardado = repository.save(restaurante);

        logger.info("Restaurante {} creado correctamente.", guardado.getId());

        return new RestauranteResponseDTO(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getDireccion(),
                guardado.getCategoria(),
                guardado.getHorario(),
                guardado.getActivo()
        );

    }

    /**
     * ===========================================================
     * LISTAR RESTAURANTES
     * ===========================================================
     *
     * Obtiene todos los restaurantes registrados.
     */
    public List<RestauranteResponseDTO> listarRestaurantes() {

        logger.info("Consultando listado completo de restaurantes.");

        return repository.findAll()

                .stream()

                .map(restaurante -> new RestauranteResponseDTO(

                        restaurante.getId(),

                        restaurante.getNombre(),

                        restaurante.getDireccion(),

                        restaurante.getCategoria(),

                        restaurante.getHorario(),

                        restaurante.getActivo()

                ))

                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * LISTAR RESTAURANTES ACTIVOS
     * ===========================================================
     *
     * Obtiene únicamente los restaurantes con estado activo.
     *
     * Pensado para ser consumido por otros microservicios
     * (por ejemplo msproductos) que solo deben trabajar con
     * restaurantes disponibles.
     */
    public List<RestauranteResponseDTO> listarActivos() {

        logger.info("Consultando listado de restaurantes activos.");

        return repository.findByActivoTrue()

                .stream()

                .map(restaurante -> new RestauranteResponseDTO(

                        restaurante.getId(),

                        restaurante.getNombre(),

                        restaurante.getDireccion(),

                        restaurante.getCategoria(),

                        restaurante.getHorario(),

                        restaurante.getActivo()

                ))

                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * BUSCAR RESTAURANTE POR ID
     * ===========================================================
     *
     * Busca un restaurante utilizando su identificador.
     *
     * Este metodo es clave para la comunicación entre
     * microservicios: msproductos consultará este endpoint
     * vía WebClient para validar que el restaurante exista
     * y esté activo antes de crear un producto.
     */
    public RestauranteResponseDTO obtenerPorId(Long id) {

        logger.info("Buscando restaurante con ID {}", id);

        Restaurante restaurante = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Restaurante {} no encontrado.", id);

                    return new RestauranteNotFoundException(
                            "Restaurante no encontrado."
                    );

                });

        return new RestauranteResponseDTO(

                restaurante.getId(),

                restaurante.getNombre(),

                restaurante.getDireccion(),

                restaurante.getCategoria(),

                restaurante.getHorario(),

                restaurante.getActivo()

        );

    }

    /**
     * ===========================================================
     * ACTUALIZAR RESTAURANTE
     * ===========================================================
     *
     * Actualiza la información de un restaurante existente.
     */
    public RestauranteResponseDTO actualizar(Long id, RestauranteRequestDTO dto) {

        logger.info("Actualizando restaurante {}", id);

        Restaurante restaurante = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Restaurante {} no encontrado.", id);

                    return new RestauranteNotFoundException(
                            "Restaurante no encontrado."
                    );

                });

        // Validar que el nuevo nombre no pertenezca a otro restaurante
        if (!restaurante.getNombre().equals(dto.getNombre())
                && repository.existsByNombre(dto.getNombre())) {

            logger.warn("Intento de actualizar con un nombre ya registrado.");

            throw new BadRequestException(
                    "El nombre ya pertenece a otro restaurante."
            );

        }

        restaurante.setNombre(dto.getNombre());

        restaurante.setDireccion(dto.getDireccion());

        restaurante.setCategoria(dto.getCategoria());

        restaurante.setHorario(dto.getHorario());

        // Si no se envía "activo" en la actualización, se mantiene el valor actual
        restaurante.setActivo(dto.getActivo() == null ? restaurante.getActivo() : dto.getActivo());

        Restaurante actualizado = repository.save(restaurante);

        logger.info("Restaurante {} actualizado correctamente.", id);

        return new RestauranteResponseDTO(

                actualizado.getId(),

                actualizado.getNombre(),

                actualizado.getDireccion(),

                actualizado.getCategoria(),

                actualizado.getHorario(),

                actualizado.getActivo()

        );

    }

    /**
     * ===========================================================
     * CAMBIAR ESTADO DEL RESTAURANTE
     * ===========================================================
     *
     * Activa o desactiva un restaurante.
     *
     * Regla de negocio:
     *
     * Un restaurante inactivo no debería poder recibir
     * nuevos productos. Esa regla se valida en msproductos,
     * pero este servicio es la fuente de verdad del estado.
     */
    public RestauranteResponseDTO cambiarEstado(Long id, Boolean nuevoEstado) {

        logger.info("Cambiando estado del restaurante {} a {}", id, nuevoEstado);

        Restaurante restaurante = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Restaurante {} no encontrado.", id);

                    return new RestauranteNotFoundException(
                            "Restaurante no encontrado."
                    );

                });

        restaurante.setActivo(nuevoEstado);

        Restaurante actualizado = repository.save(restaurante);

        logger.info("Restaurante {} ahora está {}.", id, nuevoEstado ? "ACTIVO" : "INACTIVO");

        return new RestauranteResponseDTO(

                actualizado.getId(),

                actualizado.getNombre(),

                actualizado.getDireccion(),

                actualizado.getCategoria(),

                actualizado.getHorario(),

                actualizado.getActivo()

        );

    }

    /**
     * ===========================================================
     * ELIMINAR RESTAURANTE
     * ===========================================================
     *
     * Elimina un restaurante utilizando su ID.
     */
    public void eliminar(Long id) {

        logger.info("Intentando eliminar restaurante {}", id);

        if (!repository.existsById(id)) {

            logger.warn("No se pudo eliminar el restaurante {} porque no existe.", id);

            throw new RestauranteNotFoundException(
                    "Restaurante no encontrado."
            );

        }

        repository.deleteById(id);

        logger.info("Restaurante {} eliminado correctamente.", id);

    }

}

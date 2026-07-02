package com.tuapp.msnotificaciones.service;

import com.tuapp.msnotificaciones.client.UsuarioClient;
import com.tuapp.msnotificaciones.dto.NotificacionRequestDTO;
import com.tuapp.msnotificaciones.dto.NotificacionResponseDTO;
import com.tuapp.msnotificaciones.exception.BadRequestException;
import com.tuapp.msnotificaciones.exception.NotificacionNotFoundException;
import com.tuapp.msnotificaciones.model.Notificacion;
import com.tuapp.msnotificaciones.repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ===========================================================
 * SERVICIO DE NOTIFICACIONES
 * ===========================================================
 *
 * Aquí se implementa toda la lógica de negocio.
 *
 * El Controller solamente llama a estos métodos.
 *
 * Esta clase se comunica directamente con el Repository y,
 * mediante UsuarioClient, con el microservicio msusuarios.
 */

@Service
public class NotificacionService {

    /**
     * Logger del microservicio.
     * Permite registrar eventos importantes durante la ejecución.
     */
    private static final Logger logger =
            LoggerFactory.getLogger(NotificacionService.class);

    /**
     * Orígenes permitidos para generar una notificación.
     *
     * Solo mspagos, mspedidos y msdelivery pueden disparar
     * notificaciones hacia un usuario.
     */
    private static final Set<String> ORIGENES_PERMITIDOS =
            Set.of("PAGOS", "PEDIDOS", "DELIVERY");

    private final NotificacionRepository repository;

    private final UsuarioClient usuarioClient;

    public NotificacionService(NotificacionRepository repository, UsuarioClient usuarioClient) {
        this.repository = repository;
        this.usuarioClient = usuarioClient;
    }

    /**
     * ===========================================================
     * CREAR NOTIFICACIÓN
     * ===========================================================
     *
     * Este metodo:
     *
     * 1. Valida que el origen sea uno de los permitidos.
     * 2. Valida que el usuario exista en msusuarios (WebClient).
     * 3. Valida que no exista ya una notificación para el
     *    mismo evento (usuario + origen + tipo + referenciaId).
     * 4. Crea la entidad Notificacion con fechaEnvio actual
     *    y leida en false.
     * 5. Guarda la notificación en la base de datos.
     * 6. Devuelve un DTO como respuesta.
     */
    public NotificacionResponseDTO crearNotificacion(NotificacionRequestDTO dto) {

        logger.info("Intentando crear notificación para el usuario {}", dto.getUsuarioId());

        String origen = dto.getOrigen().toUpperCase();

        // Validación del origen permitido
        if (!ORIGENES_PERMITIDOS.contains(origen)) {

            logger.warn("Origen inválido recibido: {}", dto.getOrigen());

            throw new BadRequestException(
                    "El origen debe ser uno de los siguientes: PAGOS, PEDIDOS, DELIVERY."
            );

        }

        // Validación de existencia del usuario contra msusuarios
        if (!usuarioClient.existeUsuario(dto.getUsuarioId())) {

            logger.warn("El usuario {} no existe en msusuarios.", dto.getUsuarioId());

            throw new BadRequestException(
                    "El usuario indicado no existe en msusuarios."
            );

        }

        // Validación para evitar notificaciones duplicadas del mismo evento
        boolean yaExiste = repository.existsByUsuarioIdAndOrigenAndTipoAndReferenciaId(
                dto.getUsuarioId(),
                origen,
                dto.getTipo(),
                dto.getReferenciaId()
        );

        if (yaExiste) {

            logger.warn("Ya existe una notificación registrada para este evento.");

            throw new BadRequestException(
                    "Ya existe una notificación registrada para este evento."
            );

        }

        Notificacion notificacion = new Notificacion(
                null,
                dto.getUsuarioId(),
                dto.getTipo(),
                dto.getMensaje(),
                origen,
                dto.getReferenciaId(),
                LocalDateTime.now(),
                false
        );

        Notificacion guardada = repository.save(notificacion);

        logger.info("Notificación {} creada correctamente.", guardada.getId());

        return mapearADto(guardada);

    }

    /**
     * ===========================================================
     * LISTAR NOTIFICACIONES
     * ===========================================================
     *
     * Obtiene todas las notificaciones registradas.
     */
    public List<NotificacionResponseDTO> listarNotificaciones() {

        logger.info("Consultando listado completo de notificaciones.");

        return repository.findAll()

                .stream()

                .map(this::mapearADto)

                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * LISTAR NOTIFICACIONES POR USUARIO
     * ===========================================================
     *
     * Obtiene todas las notificaciones asociadas a un usuario
     * específico.
     */
    public List<NotificacionResponseDTO> listarPorUsuario(Long usuarioId) {

        logger.info("Consultando notificaciones del usuario {}", usuarioId);

        return repository.findByUsuarioId(usuarioId)

                .stream()

                .map(this::mapearADto)

                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * BUSCAR NOTIFICACIÓN POR ID
     * ===========================================================
     *
     * Busca una notificación utilizando su identificador.
     */
    public NotificacionResponseDTO obtenerPorId(Long id) {

        logger.info("Buscando notificación con ID {}", id);

        Notificacion notificacion = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Notificación {} no encontrada.", id);

                    return new NotificacionNotFoundException(
                            "Notificación no encontrada."
                    );

                });

        return mapearADto(notificacion);

    }

    /**
     * ===========================================================
     * MARCAR NOTIFICACIÓN COMO LEÍDA
     * ===========================================================
     *
     * Regla de negocio:
     *
     * Una notificación solo puede marcarse como leída una vez.
     * Si ya se encontraba leída, se rechaza la operación.
     */
    public NotificacionResponseDTO marcarComoLeida(Long id) {

        logger.info("Intentando marcar como leída la notificación {}", id);

        Notificacion notificacion = repository.findById(id)

                .orElseThrow(() -> {

                    logger.warn("Notificación {} no encontrada.", id);

                    return new NotificacionNotFoundException(
                            "Notificación no encontrada."
                    );

                });

        if (notificacion.isLeida()) {

            logger.warn("La notificación {} ya había sido marcada como leída.", id);

            throw new BadRequestException(
                    "La notificación ya fue marcada como leída anteriormente."
            );

        }

        notificacion.setLeida(true);

        Notificacion actualizada = repository.save(notificacion);

        logger.info("Notificación {} marcada como leída correctamente.", id);

        return mapearADto(actualizada);

    }

    /**
     * ===========================================================
     * ELIMINAR NOTIFICACIÓN
     * ===========================================================
     *
     * Elimina una notificación utilizando su ID.
     */
    public void eliminar(Long id) {

        logger.info("Intentando eliminar notificación {}", id);

        if (!repository.existsById(id)) {

            logger.warn("No se pudo eliminar la notificación {} porque no existe.", id);

            throw new NotificacionNotFoundException(
                    "Notificación no encontrada."
            );

        }

        repository.deleteById(id);

        logger.info("Notificación {} eliminada correctamente.", id);

    }

    /**
     * ===========================================================
     * MAPEO ENTIDAD -> DTO
     * ===========================================================
     *
     * Convierte una entidad Notificacion en su respectivo DTO
     * de respuesta, evitando exponer directamente la entidad.
     */
    private NotificacionResponseDTO mapearADto(Notificacion notificacion) {

        return new NotificacionResponseDTO(

                notificacion.getId(),

                notificacion.getUsuarioId(),

                notificacion.getTipo(),

                notificacion.getMensaje(),

                notificacion.getOrigen(),

                notificacion.getReferenciaId(),

                notificacion.getFechaEnvio(),

                notificacion.isLeida()

        );

    }

}

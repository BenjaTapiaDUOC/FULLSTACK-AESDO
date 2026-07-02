package com.tuapp.msnotificaciones.repository;

import com.tuapp.msnotificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * ===========================================================
 * REPOSITORY DE NOTIFICACIONES
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

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /**
     * Buscar todas las notificaciones de un usuario específico.
     */
    List<Notificacion> findByUsuarioId(Long usuarioId);

    /**
     * Verificar si ya existe una notificación registrada para
     * el mismo evento.
     *
     * Un evento se considera el mismo cuando coinciden:
     * usuario, origen, tipo y referenciaId.
     *
     * Se utiliza para evitar notificaciones duplicadas del
     * mismo evento.
     */
    boolean existsByUsuarioIdAndOrigenAndTipoAndReferenciaId(
            Long usuarioId,
            String origen,
            String tipo,
            Long referenciaId
    );

}

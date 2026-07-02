package com.tuapp.msnotificaciones.controller;

import com.tuapp.msnotificaciones.dto.NotificacionRequestDTO;
import com.tuapp.msnotificaciones.dto.NotificacionResponseDTO;
import com.tuapp.msnotificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===========================================================
 * CONTROLADOR DE NOTIFICACIONES
 * ===========================================================
 *
 * Este controlador expone los servicios REST del microservicio
 * de notificaciones.
 *
 * Todas las peticiones realizadas desde Postman (o desde otro
 * microservicio como mspagos, mspedidos o msdelivery) llegan
 * primero a esta clase, la cual se encarga de recibir la
 * solicitud, validar los datos y delegar la lógica de negocio
 * al NotificacionService.
 *
 * Base URL del microservicio:
 *
 * http://localhost:8089/notificaciones
 *
 * Endpoints disponibles:
 *
 * POST    /notificaciones
 * GET     /notificaciones
 * GET     /notificaciones/{id}
 * GET     /notificaciones/usuario/{usuarioId}
 * PUT     /notificaciones/{id}/leida
 * DELETE  /notificaciones/{id}
 *
 * Todos los endpoints devuelven respuestas HTTP utilizando
 * ResponseEntity y pueden probarse fácilmente desde Postman.
 */

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    /**
     * ===========================================================
     * CREAR NOTIFICACIÓN
     * ===========================================================
     *
     * Metodo HTTP:
     * POST
     *
     * URL:
     * http://localhost:8089/notificaciones
     *
     * POSTMAN
     *
     * Body -> raw -> JSON
     *
     * {
     *   "usuarioId": 1,
     *   "tipo": "PAGO_APROBADO",
     *   "mensaje": "Tu pago fue aprobado exitosamente.",
     *   "origen": "PAGOS",
     *   "referenciaId": 10
     * }
     *
     * Respuesta:
     *
     * HTTP 201 CREATED
     */

    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(@Valid @RequestBody NotificacionRequestDTO dto) {

        NotificacionResponseDTO notificacion = service.crearNotificacion(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(notificacion);
    }

    /**
     * ===========================================================
     * LISTAR NOTIFICACIONES
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     * http://localhost:8089/notificaciones
     *
     * POSTMAN
     *
     * Seleccionar GET
     *
     * Presionar SEND
     *
     * Respuesta:
     *
     * HTTP 200 OK
     */

    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarNotificaciones());

    }

    /**
     * ===========================================================
     * OBTENER NOTIFICACIÓN POR ID
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     *
     * http://localhost:8089/notificaciones/1
     *
     * Respuesta:
     *
     * HTTP 200 OK
     *
     * Si no existe:
     *
     * HTTP 404 NOT FOUND
     */

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtener(@PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    /**
     * ===========================================================
     * LISTAR NOTIFICACIONES POR USUARIO
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     *
     * http://localhost:8089/notificaciones/usuario/1
     *
     * Respuesta:
     *
     * HTTP 200 OK
     */

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {

        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));

    }

    /**
     * ===========================================================
     * MARCAR NOTIFICACIÓN COMO LEÍDA
     * ===========================================================
     *
     * Metodo:
     *
     * PUT
     *
     * URL:
     *
     * http://localhost:8089/notificaciones/1/leida
     *
     * POSTMAN
     *
     * No requiere Body.
     *
     * Respuesta:
     *
     * HTTP 200 OK
     *
     * Si no existe:
     *
     * HTTP 404 NOT FOUND
     *
     * Si ya estaba leída:
     *
     * HTTP 400 BAD REQUEST
     */

    @PutMapping("/{id}/leida")
    public ResponseEntity<NotificacionResponseDTO> marcarComoLeida(@PathVariable Long id) {

        return ResponseEntity.ok(service.marcarComoLeida(id));

    }

    /**
     * ===========================================================
     * ELIMINAR NOTIFICACIÓN
     * ===========================================================
     *
     * Metodo:
     *
     * DELETE
     *
     * URL:
     *
     * http://localhost:8089/notificaciones/1
     *
     * POSTMAN
     *
     * Seleccionar DELETE
     *
     * Presionar SEND
     *
     * Respuesta:
     *
     * HTTP 204 NO CONTENT
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();

    }

}

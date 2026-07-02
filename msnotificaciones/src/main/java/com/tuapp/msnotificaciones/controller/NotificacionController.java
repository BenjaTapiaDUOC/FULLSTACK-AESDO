package com.tuapp.msnotificaciones.controller;

import com.tuapp.msnotificaciones.dto.NotificacionRequestDTO;
import com.tuapp.msnotificaciones.dto.NotificacionResponseDTO;
import com.tuapp.msnotificaciones.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===========================================================
 * CONTROLADOR DE NOTIFICACIONES
 * ===========================================================
 *
 * Base URL del microservicio:
 * http://localhost:8089/notificaciones
 *
 * Endpoints disponibles:
 * POST    /notificaciones
 * GET     /notificaciones
 * GET     /notificaciones/{id}
 * GET     /notificaciones/usuario/{usuarioId}
 * PUT     /notificaciones/{id}/leida
 * DELETE  /notificaciones/{id}
 *
 * Documentación interactiva (Swagger UI):
 * http://localhost:8089/swagger-ui/index.html
 */

@RestController
@RequestMapping("/notificaciones")
@Tag(name = "Notificaciones", description = "Creación y gestión de notificaciones generadas por otros microservicios (pagos, pedidos, delivery).")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @Operation(summary = "Crear una notificación", description = "Registra una nueva notificación para un usuario, generada a partir de un evento de otro microservicio.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "usuarioId": 1,
                              "tipo": "PAGO_APROBADO",
                              "mensaje": "Tu pago fue aprobado exitosamente.",
                              "origen": "PAGOS",
                              "referenciaId": 10,
                              "fechaEnvio": "2026-07-02T10:20:00",
                              "leida": false
                            }
                            """)
            )),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la notificación a crear", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "usuarioId": 1,
                              "tipo": "PAGO_APROBADO",
                              "mensaje": "Tu pago fue aprobado exitosamente.",
                              "origen": "PAGOS",
                              "referenciaId": 10
                            }
                            """)))
            @Valid @RequestBody NotificacionRequestDTO dto) {

        NotificacionResponseDTO notificacion = service.crearNotificacion(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(notificacion);
    }

    @Operation(summary = "Listar notificaciones", description = "Retorna el listado completo de notificaciones registradas.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<NotificacionResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarNotificaciones());

    }

    @Operation(summary = "Obtener una notificación por ID", description = "Busca y retorna una notificación específica según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
            @ApiResponse(responseCode = "404", description = "La notificación no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> obtener(
            @Parameter(description = "Identificador de la notificación", example = "1") @PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    @Operation(summary = "Listar notificaciones de un usuario", description = "Retorna todas las notificaciones asociadas a un usuario específico.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorUsuario(
            @Parameter(description = "Identificador del usuario", example = "1") @PathVariable Long usuarioId) {

        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));

    }

    @Operation(summary = "Marcar notificación como leída", description = "Actualiza el estado de una notificación a leída.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación marcada como leída"),
            @ApiResponse(responseCode = "404", description = "La notificación no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "La notificación ya estaba leída", content = @Content)
    })
    @PutMapping("/{id}/leida")
    public ResponseEntity<NotificacionResponseDTO> marcarComoLeida(
            @Parameter(description = "Identificador de la notificación", example = "1") @PathVariable Long id) {

        return ResponseEntity.ok(service.marcarComoLeida(id));

    }

    @Operation(summary = "Eliminar una notificación", description = "Elimina de forma permanente una notificación según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "La notificación no existe", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador de la notificación", example = "1") @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();

    }

}

package com.tuapp.msnotificaciones.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.HashMap;
import java.util.Map;

/**
 * ===========================================================
 * GLOBAL EXCEPTION HANDLER
 * ===========================================================
 *
 * Maneja todas las excepciones del microservicio
 * en un solo lugar.
 *
 * Gracias a esto el Controller queda mucho más limpio.
 *
 * Todas las respuestas de error tendrán un formato
 * consistente para facilitar el consumo desde Postman
 * u otros microservicios.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * ===========================================================
     * NOTIFICACIÓN NO ENCONTRADA
     * ===========================================================
     *
     * Respuesta HTTP:
     *
     * 404 NOT FOUND
     */

    @ExceptionHandler(NotificacionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notificacionNoEncontrada(
            NotificacionNotFoundException ex) {

        logger.warn("Notificación no encontrada: {}", ex.getMessage());

        Map<String, Object> error = new HashMap<>();

        error.put("codigo", 404);
        error.put("error", "NOT FOUND");
        error.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

    }

    /**
     * ===========================================================
     * ERROR DE REGLA DE NEGOCIO
     * ===========================================================
     *
     * Respuesta HTTP:
     *
     * 400 BAD REQUEST
     */

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> badRequest(
            BadRequestException ex) {

        logger.warn("Solicitud inválida: {}", ex.getMessage());

        Map<String, Object> error = new HashMap<>();

        error.put("codigo", 400);
        error.put("error", "BAD REQUEST");
        error.put("mensaje", ex.getMessage());

        return ResponseEntity.badRequest().body(error);

    }

    /**
     * ===========================================================
     * ERRORES DE VALIDACIÓN
     * ===========================================================
     *
     * Se ejecuta automáticamente cuando falla
     * una anotación como:
     *
     * @NotBlank
     * @NotNull
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult()

                .getAllErrors()

                .forEach(error -> {

                    String campo = ((FieldError) error).getField();

                    String mensaje = error.getDefaultMessage();

                    errores.put(campo, mensaje);

                });

        return ResponseEntity.badRequest().body(errores);

    }

    /**
     * ===========================================================
     * ERROR DE COMUNICACIÓN ENTRE MICROSERVICIOS
     * ===========================================================
     *
     * Se produce cuando msnotificaciones no logra comunicarse
     * con otro microservicio (por ejemplo msusuarios) mediante
     * WebClient, ya sea porque el servicio está caído o porque
     * el tiempo de espera se agotó.
     *
     * Respuesta HTTP:
     *
     * 400 BAD REQUEST
     */

    @ExceptionHandler(WebClientException.class)
    public ResponseEntity<Map<String, Object>> errorComunicacion(WebClientException ex) {

        logger.error("Error de comunicación con otro microservicio: {}", ex.getMessage());

        Map<String, Object> error = new HashMap<>();

        error.put("codigo", 400);
        error.put("error", "BAD REQUEST");
        error.put("mensaje", "No fue posible comunicarse con el microservicio de usuarios.");

        return ResponseEntity.badRequest().body(error);

    }

    /**
     * ===========================================================
     * ERRORES NO CONTROLADOS
     * ===========================================================
     *
     * Si ocurre cualquier excepción inesperada,
     * el sistema responderá con HTTP 500.
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> errorGeneral(Exception ex) {

        logger.error("Error interno no controlado: {}", ex.getMessage());

        Map<String, Object> error = new HashMap<>();

        error.put("codigo", 500);

        error.put("error", "INTERNAL SERVER ERROR");

        error.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

                .body(error);

    }

}

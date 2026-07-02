package com.tuapp.msautenticacion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    /**
     * ===========================================================
     * USUARIO NO ENCONTRADO
     * ===========================================================
     *
     * Respuesta HTTP:
     *
     * 404 NOT FOUND
     */

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<Map<String, Object>> usuarioNoEncontrado(
            UsuarioNotFoundException ex) {

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

        Map<String, Object> error = new HashMap<>();

        error.put("codigo", 400);
        error.put("error", "BAD REQUEST");
        error.put("mensaje", ex.getMessage());

        return ResponseEntity.badRequest().body(error);

    }

    /**
     * ===========================================================
     * CREDENCIALES INVÁLIDAS
     * ===========================================================
     *
     * Respuesta HTTP:
     *
     * 401 UNAUTHORIZED
     */

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> credencialesInvalidas(
            CredencialesInvalidasException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("codigo", 401);
        error.put("error", "UNAUTHORIZED");
        error.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

    }

    /**
     * ===========================================================
     * TOKEN INVÁLIDO O EXPIRADO
     * ===========================================================
     *
     * Respuesta HTTP:
     *
     * 401 UNAUTHORIZED
     */

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<Map<String, Object>> tokenInvalido(
            TokenInvalidoException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("codigo", 401);
        error.put("error", "UNAUTHORIZED");
        error.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

    }

    /**
     * ===========================================================
     * ERROR DE COMUNICACIÓN CON OTRO MICROSERVICIO
     * ===========================================================
     *
     * Se lanza cuando msusuarios no responde o no está
     * disponible.
     *
     * Respuesta HTTP:
     *
     * 503 SERVICE UNAVAILABLE
     */

    @ExceptionHandler(ServicioExternoException.class)
    public ResponseEntity<Map<String, Object>> servicioExterno(
            ServicioExternoException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("codigo", 503);
        error.put("error", "SERVICE UNAVAILABLE");
        error.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);

    }

    /**
     * ===========================================================
     * ERRORES DE COMUNICACIÓN WEBCLIENT (RESPUESTA DE ERROR)
     * ===========================================================
     *
     * Se lanza cuando msusuarios responde con un código de
     * error (4xx/5xx) que no fue interceptado previamente.
     */

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, Object>> errorWebClientRespuesta(
            WebClientResponseException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("codigo", 502);
        error.put("error", "BAD GATEWAY");
        error.put("mensaje", "El microservicio de usuarios respondió con un error: " + ex.getStatusCode());

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);

    }

    /**
     * ===========================================================
     * ERRORES DE CONEXIÓN WEBCLIENT
     * ===========================================================
     *
     * Se lanza cuando no fue posible conectarse con
     * msusuarios (por ejemplo, el servicio está apagado).
     */

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<Map<String, Object>> errorWebClientConexion(
            WebClientRequestException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("codigo", 503);
        error.put("error", "SERVICE UNAVAILABLE");
        error.put("mensaje", "No fue posible conectar con el microservicio de usuarios.");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);

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
     * @Email
     * @Size
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
     * ERRORES NO CONTROLADOS
     * ===========================================================
     *
     * Si ocurre cualquier excepción inesperada,
     * el sistema responderá con HTTP 500.
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> errorGeneral(Exception ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("codigo", 500);

        error.put("error", "INTERNAL SERVER ERROR");

        error.put("mensaje", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

                .body(error);

    }

}

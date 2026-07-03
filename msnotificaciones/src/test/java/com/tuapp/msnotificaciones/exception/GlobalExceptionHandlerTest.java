package com.tuapp.msnotificaciones.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - GlobalExceptionHandler
 * ===========================================================
 *
 * Se prueba cada @ExceptionHandler de forma directa,
 * sin necesidad de levantar contexto de Spring, verificando
 * el código HTTP y el cuerpo de la respuesta generada.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ===========================================================
    // TEST 1: NotificacionNotFoundException -> 404 NOT FOUND
    // ===========================================================
    @Test
    void notificacionNoEncontrada_debeRetornar404ConMensaje() {

        NotificacionNotFoundException ex = new NotificacionNotFoundException("Notificación no encontrada.");

        ResponseEntity<Map<String, Object>> response = handler.notificacionNoEncontrada(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("codigo"));
        assertEquals("NOT FOUND", response.getBody().get("error"));
        assertEquals("Notificación no encontrada.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 2: BadRequestException -> 400 BAD REQUEST
    // ===========================================================
    @Test
    void badRequest_debeRetornar400ConMensaje() {

        BadRequestException ex = new BadRequestException("Ya existe una notificación registrada para este evento.");

        ResponseEntity<Map<String, Object>> response = handler.badRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("codigo"));
        assertEquals("BAD REQUEST", response.getBody().get("error"));
        assertEquals("Ya existe una notificación registrada para este evento.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 3: MethodArgumentNotValidException -> 400 con mapa
    // de errores de validación (uno por campo)
    // ===========================================================
    @Test
    void validationErrors_debeRetornar400ConErroresPorCampo() {

        BindingResult bindingResult = mock(BindingResult.class);
        MethodParameter parameter = mock(MethodParameter.class);

        FieldError errorUsuario = new FieldError("notificacionRequestDTO", "usuarioId", "El usuarioId es obligatorio");
        FieldError errorTipo = new FieldError("notificacionRequestDTO", "tipo", "El tipo de notificación es obligatorio");

        when(bindingResult.getAllErrors()).thenReturn(List.of(errorUsuario, errorTipo));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.validationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El usuarioId es obligatorio", response.getBody().get("usuarioId"));
        assertEquals("El tipo de notificación es obligatorio", response.getBody().get("tipo"));
        assertEquals(2, response.getBody().size());
    }

    // ===========================================================
    // TEST 4: WebClientException -> 400 BAD REQUEST con mensaje
    // genérico de comunicación entre microservicios.
    // ===========================================================
    @Test
    void errorComunicacion_debeRetornar400ConMensajeGenerico() {

        WebClientResponseException ex = WebClientResponseException.create(
                503, "Service Unavailable", HttpHeaders.EMPTY, new byte[0], null
        );

        ResponseEntity<Map<String, Object>> response = handler.errorComunicacion(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("codigo"));
        assertEquals("BAD REQUEST", response.getBody().get("error"));
        assertEquals("No fue posible comunicarse con el microservicio de usuarios.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 5: Exception genérica -> 500 INTERNAL SERVER ERROR
    // ===========================================================
    @Test
    void errorGeneral_debeRetornar500ConMensaje() {

        Exception ex = new RuntimeException("Error inesperado de base de datos");

        ResponseEntity<Map<String, Object>> response = handler.errorGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("codigo"));
        assertEquals("INTERNAL SERVER ERROR", response.getBody().get("error"));
        assertEquals("Error inesperado de base de datos", response.getBody().get("mensaje"));
    }

}

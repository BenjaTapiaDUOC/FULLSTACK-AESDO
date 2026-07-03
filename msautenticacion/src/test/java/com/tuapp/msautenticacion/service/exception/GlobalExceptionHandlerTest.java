package com.tuapp.msautenticacion.service.exception;

import com.tuapp.msautenticacion.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
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
 * el codigo HTTP y el cuerpo de la respuesta generada.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ===========================================================
    // TEST 1: UsuarioNotFoundException -> 404 NOT FOUND
    // ===========================================================
    @Test
    void usuarioNoEncontrado_debeRetornar404ConMensaje() {

        UsuarioNotFoundException ex = new UsuarioNotFoundException("Usuario no encontrado.");

        ResponseEntity<Map<String, Object>> response = handler.usuarioNoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("codigo"));
        assertEquals("NOT FOUND", response.getBody().get("error"));
        assertEquals("Usuario no encontrado.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 2: BadRequestException -> 400 BAD REQUEST
    // ===========================================================
    @Test
    void badRequest_debeRetornar400ConMensaje() {

        BadRequestException ex = new BadRequestException("Rol invalido.");

        ResponseEntity<Map<String, Object>> response = handler.badRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("codigo"));
        assertEquals("BAD REQUEST", response.getBody().get("error"));
        assertEquals("Rol invalido.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 3: CredencialesInvalidasException -> 401 UNAUTHORIZED
    // ===========================================================
    @Test
    void credencialesInvalidas_debeRetornar401ConMensaje() {

        CredencialesInvalidasException ex =
                new CredencialesInvalidasException("Correo o contrasena incorrectos.");

        ResponseEntity<Map<String, Object>> response = handler.credencialesInvalidas(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().get("codigo"));
        assertEquals("UNAUTHORIZED", response.getBody().get("error"));
        assertEquals("Correo o contrasena incorrectos.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 4: TokenInvalidoException -> 401 UNAUTHORIZED
    // ===========================================================
    @Test
    void tokenInvalido_debeRetornar401ConMensaje() {

        TokenInvalidoException ex = new TokenInvalidoException("El token es invalido o ha expirado.");

        ResponseEntity<Map<String, Object>> response = handler.tokenInvalido(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().get("codigo"));
        assertEquals("UNAUTHORIZED", response.getBody().get("error"));
        assertEquals("El token es invalido o ha expirado.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 5: ServicioExternoException -> 503 SERVICE UNAVAILABLE
    // ===========================================================
    @Test
    void servicioExterno_debeRetornar503ConMensaje() {

        ServicioExternoException ex =
                new ServicioExternoException("El microservicio de usuarios no esta disponible.");

        ResponseEntity<Map<String, Object>> response = handler.servicioExterno(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(503, response.getBody().get("codigo"));
        assertEquals("SERVICE UNAVAILABLE", response.getBody().get("error"));
    }

    // ===========================================================
    // TEST 6: WebClientResponseException -> 502 BAD GATEWAY
    // ===========================================================
    @Test
    void errorWebClientRespuesta_debeRetornar502() {

        WebClientResponseException ex = WebClientResponseException.create(
                500, "Internal Server Error", HttpHeaders.EMPTY, new byte[0], null
        );

        ResponseEntity<Map<String, Object>> response = handler.errorWebClientRespuesta(ex);

        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(502, response.getBody().get("codigo"));
        assertEquals("BAD GATEWAY", response.getBody().get("error"));
        assertTrue(((String) response.getBody().get("mensaje")).contains("500"));
    }

    // ===========================================================
    // TEST 7: WebClientRequestException -> 503 SERVICE UNAVAILABLE
    // ===========================================================
    @Test
    void errorWebClientConexion_debeRetornar503() {

        WebClientRequestException ex = new WebClientRequestException(
                new IOException("Connection refused"), null, null, HttpHeaders.EMPTY
        );

        ResponseEntity<Map<String, Object>> response = handler.errorWebClientConexion(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(503, response.getBody().get("codigo"));
        assertEquals("SERVICE UNAVAILABLE", response.getBody().get("error"));
    }

    // ===========================================================
    // TEST 8: MethodArgumentNotValidException -> 400 con mapa
    // de errores de validacion (uno por campo)
    // ===========================================================
    @Test
    void validationErrors_debeRetornar400ConErroresPorCampo() {

        BindingResult bindingResult = mock(BindingResult.class);
        MethodParameter parameter = mock(MethodParameter.class);

        FieldError errorEmail = new FieldError("registroRequestDTO", "email", "Debe ingresar un correo valido");
        FieldError errorPassword = new FieldError("registroRequestDTO", "password", "La contrasena debe tener al menos 8 caracteres");

        when(bindingResult.getAllErrors()).thenReturn(List.of(errorEmail, errorPassword));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.validationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Debe ingresar un correo valido", response.getBody().get("email"));
        assertEquals("La contrasena debe tener al menos 8 caracteres", response.getBody().get("password"));
        assertEquals(2, response.getBody().size());
    }

    // ===========================================================
    // TEST 9: Exception generica -> 500 INTERNAL SERVER ERROR
    // ===========================================================
    @Test
    void errorGeneral_debeRetornar500ConMensaje() {

        Exception ex = new RuntimeException("Error inesperado");

        ResponseEntity<Map<String, Object>> response = handler.errorGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("codigo"));
        assertEquals("INTERNAL SERVER ERROR", response.getBody().get("error"));
        assertEquals("Error inesperado", response.getBody().get("mensaje"));
    }

}

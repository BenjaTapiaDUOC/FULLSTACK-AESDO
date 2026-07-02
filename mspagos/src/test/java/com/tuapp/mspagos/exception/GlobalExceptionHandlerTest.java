package com.tuapp.mspagos.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

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
 * verificando el código HTTP y el cuerpo de la respuesta.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ===========================================================
    // TEST 1: PagoNotFoundException -> 404 NOT FOUND
    // ===========================================================
    @Test
    void pagoNoEncontrado_debeRetornar404ConMensaje() {

        PagoNotFoundException ex = new PagoNotFoundException("Pago no encontrado.");

        ResponseEntity<Map<String, Object>> response = handler.pagoNoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("codigo"));
        assertEquals("NOT FOUND", response.getBody().get("error"));
        assertEquals("Pago no encontrado.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 2: BadRequestException -> 400 BAD REQUEST
    // ===========================================================
    @Test
    void badRequest_debeRetornar400ConMensaje() {

        BadRequestException ex = new BadRequestException("No es posible modificar un pago que ya fue aprobado.");

        ResponseEntity<Map<String, Object>> response = handler.badRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("codigo"));
        assertEquals("BAD REQUEST", response.getBody().get("error"));
        assertEquals("No es posible modificar un pago que ya fue aprobado.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 3: PagoException -> 503 SERVICE UNAVAILABLE
    // ===========================================================
    @Test
    void errorComunicacion_debeRetornar503ConMensaje() {

        PagoException ex = new PagoException(
                "No fue posible validar el pedido. El microservicio mspedidos no respondió."
        );

        ResponseEntity<Map<String, Object>> response = handler.errorComunicacion(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(503, response.getBody().get("codigo"));
        assertEquals("SERVICE UNAVAILABLE", response.getBody().get("error"));
        assertEquals(
                "No fue posible validar el pedido. El microservicio mspedidos no respondió.",
                response.getBody().get("mensaje")
        );
    }

    // ===========================================================
    // TEST 4: MethodArgumentNotValidException -> 400 con mapa
    // de errores de validación (uno por campo)
    // ===========================================================
    @Test
    void validationErrors_debeRetornar400ConErroresPorCampo() {

        BindingResult bindingResult = mock(BindingResult.class);
        MethodParameter parameter = mock(MethodParameter.class);

        FieldError errorMonto = new FieldError("pagoRequestDTO", "monto", "El monto debe ser mayor a cero");
        FieldError errorMetodo = new FieldError("pagoRequestDTO", "metodoPago", "El método de pago es obligatorio");

        when(bindingResult.getAllErrors()).thenReturn(List.of(errorMonto, errorMetodo));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.validationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El monto debe ser mayor a cero", response.getBody().get("monto"));
        assertEquals("El método de pago es obligatorio", response.getBody().get("metodoPago"));
        assertEquals(2, response.getBody().size());
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

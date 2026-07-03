package com.tuapp.mspromociones.exception;

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
 * sin necesidad de levantar contexto de Spring, verificando
 * el código HTTP y el cuerpo de la respuesta generada.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ===========================================================
    // TEST 1: PromocionNotFoundException -> 404 NOT FOUND
    // ===========================================================
    @Test
    void promocionNoEncontrada_debeRetornar404ConMensaje() {

        PromocionNotFoundException ex = new PromocionNotFoundException("Promoción no encontrada.");

        ResponseEntity<Map<String, Object>> response = handler.promocionNoEncontrada(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("codigo"));
        assertEquals("NOT FOUND", response.getBody().get("error"));
        assertEquals("Promoción no encontrada.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 2: BadRequestException -> 400 BAD REQUEST
    // ===========================================================
    @Test
    void badRequest_debeRetornar400ConMensaje() {

        BadRequestException ex = new BadRequestException("El cupón se encuentra vencido.");

        ResponseEntity<Map<String, Object>> response = handler.badRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("codigo"));
        assertEquals("BAD REQUEST", response.getBody().get("error"));
        assertEquals("El cupón se encuentra vencido.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 3: MethodArgumentNotValidException -> 400 con mapa
    // de errores de validación (uno por campo)
    // ===========================================================
    @Test
    void validationErrors_debeRetornar400ConErroresPorCampo() {

        BindingResult bindingResult = mock(BindingResult.class);
        MethodParameter parameter = mock(MethodParameter.class);

        FieldError errorCodigo = new FieldError("promocionRequestDTO", "codigo", "El código es obligatorio");
        FieldError errorPorcentaje = new FieldError("promocionRequestDTO", "porcentajeDescuento", "El porcentaje no puede ser mayor a 100");

        when(bindingResult.getAllErrors()).thenReturn(List.of(errorCodigo, errorPorcentaje));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.validationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El código es obligatorio", response.getBody().get("codigo"));
        assertEquals("El porcentaje no puede ser mayor a 100", response.getBody().get("porcentajeDescuento"));
        assertEquals(2, response.getBody().size());
    }

    // ===========================================================
    // TEST 4: Exception genérica -> 500 INTERNAL SERVER ERROR
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

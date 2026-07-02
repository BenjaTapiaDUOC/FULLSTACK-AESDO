package com.tuapp.msrestaurantes.exception;

import org.junit.jupiter.api.Test;
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
 * Se invocan directamente los métodos @ExceptionHandler con
 * las excepciones que manejan, verificando que cada uno arma
 * la respuesta HTTP y el body de error esperado.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ===========================================================
    // RestauranteNotFoundException -> 404 NOT FOUND
    // ===========================================================
    @Test
    void restauranteNoEncontrado_debeRetornar404ConMensajeCorrecto() {

        RestauranteNotFoundException ex = new RestauranteNotFoundException("Restaurante no encontrado.");

        ResponseEntity<Map<String, Object>> respuesta = handler.restauranteNoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(404, respuesta.getBody().get("codigo"));
        assertEquals("NOT FOUND", respuesta.getBody().get("error"));
        assertEquals("Restaurante no encontrado.", respuesta.getBody().get("mensaje"));
    }

    // ===========================================================
    // BadRequestException -> 400 BAD REQUEST
    // ===========================================================
    @Test
    void badRequest_debeRetornar400ConMensajeCorrecto() {

        BadRequestException ex = new BadRequestException("Ya existe un restaurante registrado con ese nombre.");

        ResponseEntity<Map<String, Object>> respuesta = handler.badRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().get("codigo"));
        assertEquals("BAD REQUEST", respuesta.getBody().get("error"));
        assertEquals("Ya existe un restaurante registrado con ese nombre.", respuesta.getBody().get("mensaje"));
    }

    // ===========================================================
    // MethodArgumentNotValidException -> 400 con mapa campo/mensaje
    // ===========================================================
    @Test
    void validationErrors_debeRetornarMapaDeErroresPorCampo() {

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError(
                "restauranteRequestDTO",
                "nombre",
                "El nombre es obligatorio"
        );

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Map<String, String>> respuesta = handler.validationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("El nombre es obligatorio", respuesta.getBody().get("nombre"));
    }

    // ===========================================================
    // Exception genérica -> 500 INTERNAL SERVER ERROR
    // ===========================================================
    @Test
    void errorGeneral_debeRetornar500ConMensajeCorrecto() {

        Exception ex = new RuntimeException("Error inesperado en el servicio.");

        ResponseEntity<Map<String, Object>> respuesta = handler.errorGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(500, respuesta.getBody().get("codigo"));
        assertEquals("INTERNAL SERVER ERROR", respuesta.getBody().get("error"));
        assertEquals("Error inesperado en el servicio.", respuesta.getBody().get("mensaje"));
    }
}

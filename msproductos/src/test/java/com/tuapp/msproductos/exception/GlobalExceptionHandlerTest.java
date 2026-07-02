package com.tuapp.msproductos.exception;

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
    // TEST 1: ProductoNotFoundException -> 404 NOT FOUND
    // ===========================================================
    @Test
    void productoNoEncontrado_debeRetornar404ConMensaje() {

        ProductoNotFoundException ex = new ProductoNotFoundException("Producto no encontrado.");

        ResponseEntity<Map<String, Object>> response = handler.productoNoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("codigo"));
        assertEquals("NOT FOUND", response.getBody().get("error"));
        assertEquals("Producto no encontrado.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 2: BadRequestException -> 400 BAD REQUEST
    // ===========================================================
    @Test
    void badRequest_debeRetornar400ConMensaje() {

        BadRequestException ex = new BadRequestException("Ya existe un producto registrado con ese nombre.");

        ResponseEntity<Map<String, Object>> response = handler.badRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("codigo"));
        assertEquals("BAD REQUEST", response.getBody().get("error"));
        assertEquals("Ya existe un producto registrado con ese nombre.", response.getBody().get("mensaje"));
    }

    // ===========================================================
    // TEST 3: MethodArgumentNotValidException -> 400 con mapa
    // de errores de validación (uno por campo)
    // ===========================================================
    @Test
    void validationErrors_debeRetornar400ConErroresPorCampo() {

        BindingResult bindingResult = mock(BindingResult.class);
        MethodParameter parameter = mock(MethodParameter.class);

        FieldError errorNombre = new FieldError("productoRequestDTO", "nombre", "El nombre es obligatorio");
        FieldError errorPrecio = new FieldError("productoRequestDTO", "precio", "El precio debe ser mayor a 0");

        when(bindingResult.getAllErrors()).thenReturn(List.of(errorNombre, errorPrecio));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.validationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("El nombre es obligatorio", response.getBody().get("nombre"));
        assertEquals("El precio debe ser mayor a 0", response.getBody().get("precio"));
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

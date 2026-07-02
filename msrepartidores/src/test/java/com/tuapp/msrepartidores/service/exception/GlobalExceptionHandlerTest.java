package com.tuapp.msrepartidores.service.exception;

import com.tuapp.msrepartidores.exception.BadRequestException;
import com.tuapp.msrepartidores.exception.GlobalExceptionHandler;
import com.tuapp.msrepartidores.exception.RepartidorNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
 *
 * También se cubren las excepciones de dominio (BadRequestException,
 * RepartidorNotFoundException), ya que se construyen como parte
 * de estas pruebas.
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // ===========================================================
    // RepartidorNotFoundException -> 404 NOT FOUND
    // ===========================================================
    @Test
    void repartidorNoEncontrado_debeRetornar404ConMensajeCorrecto() {

        RepartidorNotFoundException ex = new RepartidorNotFoundException("Repartidor no encontrado.");

        ResponseEntity<Map<String, Object>> respuesta = handler.repartidorNoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(404, respuesta.getBody().get("codigo"));
        assertEquals("NOT FOUND", respuesta.getBody().get("error"));
        assertEquals("Repartidor no encontrado.", respuesta.getBody().get("mensaje"));
    }

    // ===========================================================
    // BadRequestException -> 400 BAD REQUEST
    // ===========================================================
    @Test
    void badRequest_debeRetornar400ConMensajeCorrecto() {

        BadRequestException ex = new BadRequestException("No se puede eliminar un repartidor que se encuentra en ruta.");

        ResponseEntity<Map<String, Object>> respuesta = handler.badRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().get("codigo"));
        assertEquals("BAD REQUEST", respuesta.getBody().get("error"));
        assertEquals("No se puede eliminar un repartidor que se encuentra en ruta.", respuesta.getBody().get("mensaje"));
    }

    // ===========================================================
    // MethodArgumentNotValidException -> 400 con mapa campo/mensaje
    // ===========================================================
    @Test
    void validationErrors_debeRetornarMapaDeErroresPorCampo() {

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError(
                "repartidorRequestDTO",
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
    // HttpMessageNotReadableException -> 400 con mensaje fijo
    // (por ejemplo, un "estado" fuera del enum EstadoRepartidor)
    // ===========================================================
    @Test
    void mensajeIlegible_debeRetornar400ConMensajeFijo() {

        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);

        ResponseEntity<Map<String, Object>> respuesta = handler.mensajeIlegible(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(400, respuesta.getBody().get("codigo"));
        assertEquals("BAD REQUEST", respuesta.getBody().get("error"));
        assertEquals(
                "El cuerpo de la petición es inválido. Verifique que el estado sea DISPONIBLE, EN_RUTA o INACTIVO.",
                respuesta.getBody().get("mensaje")
        );
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

package com.tuapp.msrepartidores.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
     * REPARTIDOR NO ENCONTRADO
     * ===========================================================
     *
     * Respuesta HTTP:
     *
     * 404 NOT FOUND
     */

    @ExceptionHandler(RepartidorNotFoundException.class)
    public ResponseEntity<Map<String, Object>> repartidorNoEncontrado(
            RepartidorNotFoundException ex) {

        Map<String, Object> error = new HashMap<>();

        error.put("codigo",404);
        error.put("error","NOT FOUND");
        error.put("mensaje",ex.getMessage());

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
    public ResponseEntity<Map<String,Object>> badRequest(
            BadRequestException ex){

        Map<String,Object> error = new HashMap<>();

        error.put("codigo",400);
        error.put("error","BAD REQUEST");
        error.put("mensaje",ex.getMessage());

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
    public ResponseEntity<Map<String,String>> validationErrors(

            MethodArgumentNotValidException ex){

        Map<String,String> errores = new HashMap<>();

        ex.getBindingResult()

                .getAllErrors()

                .forEach(error->{

                    String campo=((FieldError)error).getField();

                    String mensaje=error.getDefaultMessage();

                    errores.put(campo,mensaje);

                });

        return ResponseEntity.badRequest().body(errores);

    }

    /**
     * ===========================================================
     * CUERPO DE PETICIÓN ILEGIBLE O ESTADO INVÁLIDO
     * ===========================================================
     *
     * Se ejecuta, por ejemplo, cuando se envía un valor de
     * "estado" que no corresponde a DISPONIBLE, EN_RUTA o
     * INACTIVO.
     *
     * Respuesta HTTP:
     *
     * 400 BAD REQUEST
     */

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,Object>> mensajeIlegible(
            HttpMessageNotReadableException ex){

        Map<String,Object> error = new HashMap<>();

        error.put("codigo",400);
        error.put("error","BAD REQUEST");
        error.put("mensaje","El cuerpo de la petición es inválido. Verifique que el estado sea DISPONIBLE, EN_RUTA o INACTIVO.");

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
    public ResponseEntity<Map<String,Object>> errorGeneral(Exception ex){

        Map<String,Object> error=new HashMap<>();

        error.put("codigo",500);

        error.put("error","INTERNAL SERVER ERROR");

        error.put("mensaje",ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)

                .body(error);

    }

}

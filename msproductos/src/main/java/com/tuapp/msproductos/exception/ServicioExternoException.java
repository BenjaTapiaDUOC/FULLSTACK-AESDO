package com.tuapp.msproductos.exception;

/**
 * ===========================================================
 * SERVICIO EXTERNO EXCEPTION
 * ===========================================================
 *
 * Excepción genérica de comunicación con otro microservicio.
 *
 * Se utiliza cuando ocurre un problema al consultar msrestaurantes
 * (timeout, servicio caído, error inesperado).
 *
 * El GlobalExceptionHandler la captura y responde con
 * HTTP 503 SERVICE UNAVAILABLE.
 */

public class ServicioExternoException extends RuntimeException {

    public ServicioExternoException(String mensaje) {
        super(mensaje);
    }

}

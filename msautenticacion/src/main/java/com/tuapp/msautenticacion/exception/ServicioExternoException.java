package com.tuapp.msautenticacion.exception;

/**
 * ===========================================================
 * SERVICIO EXTERNO EXCEPTION
 * ===========================================================
 *
 * Esta excepción se lanza cuando ocurre un problema al
 * comunicarse con otro microservicio (en este caso,
 * msusuarios) mediante WebClient.
 *
 * Ejemplos:
 *
 * - msusuarios no está levantado (connection refused).
 * - Timeout al esperar la respuesta.
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 503.
 */

public class ServicioExternoException extends RuntimeException {

    public ServicioExternoException(String mensaje) {
        super(mensaje);
    }

}

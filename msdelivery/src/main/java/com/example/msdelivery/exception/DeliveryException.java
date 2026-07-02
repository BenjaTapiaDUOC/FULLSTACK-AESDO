package com.example.msdelivery.exception;

/**
 * ===========================================================
 * DELIVERY EXCEPTION
 * ===========================================================
 *
 * Excepción genérica del dominio de delivery.
 *
 * Se utiliza principalmente cuando ocurre un problema de
 * comunicación con otro microservicio (por ejemplo,
 * mspedidos no responde).
 *
 * El GlobalExceptionHandler la captura y responde con
 * HTTP 503 SERVICE UNAVAILABLE.
 */

public class DeliveryException extends RuntimeException {

    public DeliveryException(String mensaje) {
        super(mensaje);
    }

}

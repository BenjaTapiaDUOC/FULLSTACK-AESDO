package com.example.msdelivery.exception;

/**
 * ===========================================================
 * DELIVERY NOT FOUND EXCEPTION
 * ===========================================================
 *
 * Esta excepción se lanza cuando el delivery solicitado
 * no existe en la base de datos.
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 404.
 */

public class DeliveryNotFoundException extends RuntimeException {

    public DeliveryNotFoundException(String mensaje) {
        super(mensaje);
    }

}

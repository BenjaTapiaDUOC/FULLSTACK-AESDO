package com.tuapp.msrepartidores.exception;

/**
 * ===========================================================
 * REPARTIDOR NOT FOUND EXCEPTION
 * ===========================================================
 *
 * Esta excepción se lanza cuando el repartidor solicitado
 * no existe en la base de datos.
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 404.
 */

public class RepartidorNotFoundException extends RuntimeException {

    public RepartidorNotFoundException(String mensaje) {
        super(mensaje);
    }

}

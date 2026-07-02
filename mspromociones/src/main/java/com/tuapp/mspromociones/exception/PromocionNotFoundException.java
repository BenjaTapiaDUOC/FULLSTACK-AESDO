package com.tuapp.mspromociones.exception;

/**
 * ===========================================================
 * PROMOCION NOT FOUND EXCEPTION
 * ===========================================================
 *
 * Esta excepción se lanza cuando la promoción solicitada
 * no existe en la base de datos, ya sea por ID o por
 * código de cupón.
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 404.
 */

public class PromocionNotFoundException extends RuntimeException {

    public PromocionNotFoundException(String mensaje) {
        super(mensaje);
    }

}

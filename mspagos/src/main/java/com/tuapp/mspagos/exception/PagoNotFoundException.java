package com.tuapp.mspagos.exception;

/**
 * ===========================================================
 * PAGO NOT FOUND EXCEPTION
 * ===========================================================
 *
 * Esta excepción se lanza cuando el pago solicitado
 * no existe en la base de datos.
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 404.
 */

public class PagoNotFoundException extends RuntimeException {

    public PagoNotFoundException(String mensaje) {
        super(mensaje);
    }

}

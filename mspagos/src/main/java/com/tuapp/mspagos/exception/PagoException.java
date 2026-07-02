package com.tuapp.mspagos.exception;

/**
 * ===========================================================
 * PAGO EXCEPTION
 * ===========================================================
 *
 * Se lanza cuando falla la comunicación con otro microservicio
 * (por ejemplo, mspedidos no responde, hay timeout o el
 * servicio está caído).
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 503, igual que hacen
 * mspedidos y msdelivery ante el mismo tipo de falla.
 */

public class PagoException extends RuntimeException {

    public PagoException(String mensaje) {
        super(mensaje);
    }

}

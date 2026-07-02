package com.tuapp.msnotificaciones.exception;

/**
 * ===========================================================
 * NOTIFICACION NOT FOUND EXCEPTION
 * ===========================================================
 *
 * Esta excepción se lanza cuando la notificación solicitada
 * no existe en la base de datos.
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 404.
 */

public class NotificacionNotFoundException extends RuntimeException {

    public NotificacionNotFoundException(String mensaje) {
        super(mensaje);
    }

}

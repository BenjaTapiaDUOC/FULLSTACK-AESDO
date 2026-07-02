package com.tuapp.msnotificaciones.exception;

/**
 * ===========================================================
 * BAD REQUEST EXCEPTION
 * ===========================================================
 *
 * Esta excepción se utiliza cuando el cliente envía
 * información inválida o cuando se incumple alguna
 * regla de negocio.
 *
 * Ejemplos:
 *
 * - Usuario inexistente en msusuarios.
 * - Origen no permitido.
 * - Notificación duplicada para el mismo evento.
 * - Notificación ya marcada como leída.
 */

public class BadRequestException extends RuntimeException {

    public BadRequestException(String mensaje) {
        super(mensaje);
    }

}

package com.tuapp.msrepartidores.exception;

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
 * - Intentar eliminar un repartidor que se encuentra EN_RUTA.
 * - Intentar cambiar a un estado inválido.
 * - Información incompleta.
 */

public class BadRequestException extends RuntimeException {

    public BadRequestException(String mensaje) {
        super(mensaje);
    }

}

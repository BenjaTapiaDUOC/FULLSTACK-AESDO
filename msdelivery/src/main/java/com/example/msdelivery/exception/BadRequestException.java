package com.example.msdelivery.exception;

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
 * - Pedido con delivery duplicado.
 * - Datos incorrectos.
 * - Información incompleta.
 */

public class BadRequestException extends RuntimeException {

    public BadRequestException(String mensaje) {
        super(mensaje);
    }

}

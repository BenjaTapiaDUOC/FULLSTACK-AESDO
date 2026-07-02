package com.tuapp.mspromociones.exception;

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
 * - Código de promoción duplicado.
 * - Cupón vencido.
 * - Cupón ya utilizado o inactivo.
 * - Fechas de vigencia incoherentes.
 */

public class BadRequestException extends RuntimeException {

    public BadRequestException(String mensaje) {
        super(mensaje);
    }

}

package mspedidos.exception;

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
 * - El usuario del pedido no existe.
 * - Estado de pedido inválido.
 * - Datos incompletos o incoherentes.
 */

public class BadRequestException extends RuntimeException {

    public BadRequestException(String mensaje) {
        super(mensaje);
    }

}

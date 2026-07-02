package com.tuapp.msautenticacion.exception;

/**
 * ===========================================================
 * CREDENCIALES INVÁLIDAS EXCEPTION
 * ===========================================================
 *
 * Esta excepción se lanza durante el login cuando:
 *
 * - El correo no está registrado.
 * - La contraseña no coincide.
 *
 * Por seguridad, ambos casos entregan el mismo mensaje
 * genérico, para no indicar si el correo existe o no.
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 401.
 */

public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }

}

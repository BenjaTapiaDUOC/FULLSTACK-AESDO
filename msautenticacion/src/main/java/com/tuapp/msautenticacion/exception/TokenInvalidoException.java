package com.tuapp.msautenticacion.exception;

/**
 * ===========================================================
 * TOKEN INVÁLIDO EXCEPTION
 * ===========================================================
 *
 * Esta excepción se lanza cuando:
 *
 * - No se envía el header Authorization.
 * - El token JWT está mal formado.
 * - El token JWT expiró.
 * - La firma del token no es válida.
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 401.
 */

public class TokenInvalidoException extends RuntimeException {

    public TokenInvalidoException(String mensaje) {
        super(mensaje);
    }

}

package com.tuapp.msautenticacion.exception;

/**
 * ===========================================================
 * USUARIO NOT FOUND EXCEPTION
 * ===========================================================
 *
 * Esta excepción se lanza cuando el usuario de autenticación
 * solicitado no existe en la base de datos.
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 404.
 */

public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(String mensaje) {
        super(mensaje);
    }

}

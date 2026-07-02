package com.tuapp.msproductos.exception;

/**
 * ===========================================================
 * PRODUCTO NOT FOUND EXCEPTION
 * ===========================================================
 *
 * Esta excepción se lanza cuando el producto solicitado
 * no existe en la base de datos.
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 404.
 */

public class ProductoNotFoundException extends RuntimeException {

    public ProductoNotFoundException(String mensaje) {
        super(mensaje);
    }

}
package com.tuapp.msrestaurantes.exception;

/**
 * ===========================================================
 * RESTAURANTE NOT FOUND EXCEPTION
 * ===========================================================
 *
 * Esta excepción se lanza cuando el restaurante solicitado
 * no existe en la base de datos.
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 404.
 *
 * Este mismo comportamiento es el que debe interpretar
 * el microservicio msproductos cuando consuma este
 * servicio vía WebClient.
 */

public class RestauranteNotFoundException extends RuntimeException {

    public RestauranteNotFoundException(String mensaje) {
        super(mensaje);
    }

}

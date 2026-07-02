package com.tuapp.msautenticacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE RESPUESTA - LOGIN
 * ===========================================================
 *
 * Este objeto se entrega al cliente cuando el login es
 * exitoso. Contiene el token JWT que deberá enviarse en el
 * header "Authorization" (formato "Bearer {token}") para
 * las siguientes peticiones que requieran autenticación.
 */

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    /**
     * Token JWT generado.
     */
    private String token;

    /**
     * Tipo de token. Siempre será "Bearer".
     */
    private String tipo;

    /**
     * Tiempo de expiración del token, en milisegundos.
     */
    private long expiraEnMs;

    /**
     * Información básica del usuario autenticado.
     */
    private UsuarioResponseDTO usuario;

}

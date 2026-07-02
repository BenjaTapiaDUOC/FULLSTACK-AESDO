package com.tuapp.msautenticacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Respuesta entregada tras un login exitoso, incluye el token JWT.")
public class LoginResponseDTO {

    @Schema(description = "Token JWT generado.", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiZW5qYW1pbkBnbWFpbC5jb20i...")
    private String token;

    @Schema(description = "Tipo de token. Siempre será \"Bearer\".", example = "Bearer")
    private String tipo;

    @Schema(description = "Tiempo de expiración del token, en milisegundos.", example = "3600000")
    private long expiraEnMs;

    @Schema(description = "Información básica del usuario autenticado.")
    private UsuarioResponseDTO usuario;

}

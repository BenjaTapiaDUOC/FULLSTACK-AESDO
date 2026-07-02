package com.tuapp.msautenticacion.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE ENTRADA - LOGIN
 * ===========================================================
 *
 * Este DTO recibe las credenciales enviadas desde Postman
 * para iniciar sesión.
 */

@Data
public class LoginRequestDTO {

    /**
     * Correo electrónico registrado.
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ingresar un correo válido")
    private String email;

    /**
     * Contraseña del usuario.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

}

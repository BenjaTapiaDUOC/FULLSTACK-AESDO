package com.tuapp.msautenticacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Credenciales requeridas para iniciar sesión.")
public class LoginRequestDTO {

    @Schema(description = "Correo electrónico registrado.", example = "benjamin@gmail.com")
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ingresar un correo válido")
    private String email;

    @Schema(description = "Contraseña del usuario.", example = "12345678")
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

}

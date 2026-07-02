package com.tuapp.msautenticacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE ENTRADA - REGISTRO
 * ===========================================================
 *
 * Este DTO recibe la información enviada desde Postman para
 * registrar un nuevo usuario en el sistema de autenticación.
 *
 * Con estos datos:
 *
 * 1. Se valida el rol.
 * 2. Se crea el usuario "maestro" en msusuarios (vía WebClient).
 * 3. Se guarda localmente la credencial + rol para el login.
 */

@Data
@Schema(description = "Datos requeridos para registrar un nuevo usuario.")
public class RegistroRequestDTO {

    @Schema(description = "Nombre del usuario.", example = "Benjamin")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Correo electrónico.", example = "benjamin@gmail.com")
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ingresar un correo válido")
    private String email;

    @Schema(description = "Contraseña. Debe tener mínimo 8 caracteres.", example = "12345678")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @Schema(description = "Rol solicitado para el usuario.", example = "CLIENTE",
            allowableValues = {"ADMIN", "CLIENTE", "REPARTIDOR"})
    @NotBlank(message = "El rol es obligatorio")
    private String rol;

}

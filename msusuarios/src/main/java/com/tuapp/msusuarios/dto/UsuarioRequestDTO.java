package com.tuapp.msusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE ENTRADA
 * ===========================================================
 *
 * Este DTO recibe la información enviada desde Postman.
 *
 * Se utiliza para evitar exponer directamente la entidad
 * Usuario.
 */

@Data
@Schema(description = "Datos requeridos para crear o actualizar un usuario.")
public class UsuarioRequestDTO {

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

}

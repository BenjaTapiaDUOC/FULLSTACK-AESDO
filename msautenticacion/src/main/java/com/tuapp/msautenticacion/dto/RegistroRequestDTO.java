package com.tuapp.msautenticacion.dto;

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
public class RegistroRequestDTO {

    /**
     * Nombre del usuario.
     */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /**
     * Correo electrónico.
     */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ingresar un correo válido")
    private String email;

    /**
     * Contraseña.
     *
     * Debe tener mínimo 8 caracteres.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    /**
     * Rol solicitado para el usuario.
     *
     * Valores permitidos: ADMIN, CLIENTE, REPARTIDOR.
     */
    @NotBlank(message = "El rol es obligatorio")
    private String rol;

}

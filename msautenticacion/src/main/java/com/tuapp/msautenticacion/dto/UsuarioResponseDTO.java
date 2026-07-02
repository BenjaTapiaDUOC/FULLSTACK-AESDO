package com.tuapp.msautenticacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE RESPUESTA - USUARIO
 * ===========================================================
 *
 * Este objeto se envía como respuesta al cliente luego de
 * registrar un usuario o consultar su información.
 *
 * De esta forma no se expone directamente la entidad Usuario
 * (por ejemplo, no se expone la contraseña).
 */

@Data
@AllArgsConstructor
public class UsuarioResponseDTO {

    /**
     * Identificador del registro de autenticación.
     */
    private Long id;

    /**
     * Identificador del usuario real en msusuarios.
     */
    private Long usuarioId;

    /**
     * Nombre del usuario.
     */
    private String nombre;

    /**
     * Correo electrónico.
     */
    private String email;

    /**
     * Nombre del rol asignado (ADMIN, CLIENTE, REPARTIDOR).
     */
    private String rol;

}

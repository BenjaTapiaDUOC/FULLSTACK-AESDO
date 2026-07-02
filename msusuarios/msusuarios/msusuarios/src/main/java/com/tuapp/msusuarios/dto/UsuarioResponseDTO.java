package com.tuapp.msusuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE RESPUESTA
 * ===========================================================
 *
 * Este objeto se envía como respuesta al cliente.
 *
 * De esta forma no se expone directamente la entidad Usuario.
 */

@Data
@AllArgsConstructor
public class UsuarioResponseDTO {

    /**
     * Identificador del usuario.
     */
    private Long id;

    /**
     * Nombre del usuario.
     */
    private String nombre;

    /**
     * Correo electrónico.
     */
    private String email;

    /**
     * Contraseña.
     *
     * NOTA:
     * Para una aplicación real NO debería enviarse.
     *
     * Se mantiene únicamente para los requerimientos
     * académicos del proyecto.
     */
    private String password;

}
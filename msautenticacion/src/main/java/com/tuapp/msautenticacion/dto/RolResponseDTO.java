package com.tuapp.msautenticacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE RESPUESTA - ROL
 * ===========================================================
 *
 * Representa un rol disponible en el sistema.
 */

@Data
@AllArgsConstructor
public class RolResponseDTO {

    /**
     * Identificador del rol.
     */
    private Long id;

    /**
     * Nombre del rol (ADMIN, CLIENTE, REPARTIDOR).
     */
    private String nombre;

}

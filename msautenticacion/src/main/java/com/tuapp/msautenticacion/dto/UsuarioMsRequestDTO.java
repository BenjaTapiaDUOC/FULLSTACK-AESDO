package com.tuapp.msautenticacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================
 * DTO DE SALIDA HACIA MSUSUARIOS
 * ===========================================================
 *
 * Representa el cuerpo (body) que este microservicio envía
 * mediante WebClient al endpoint POST /usuarios de
 * msusuarios (puerto 8081) para crear el usuario "maestro".
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioMsRequestDTO {

    private String nombre;

    private String email;

    private String password;

}

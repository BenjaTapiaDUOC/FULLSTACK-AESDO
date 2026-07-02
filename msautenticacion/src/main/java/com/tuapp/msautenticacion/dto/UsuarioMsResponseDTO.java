package com.tuapp.msautenticacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================
 * DTO DE ENTRADA DESDE MSUSUARIOS
 * ===========================================================
 *
 * Representa la respuesta que entrega msusuarios (puerto 8081)
 * al crear un usuario mediante POST /usuarios.
 *
 * Se utiliza únicamente para deserializar el JSON recibido
 * por WebClient; el campo relevante para este microservicio
 * es "id", que se guarda como referencia (usuarioId).
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioMsResponseDTO {

    private Long id;

    private String nombre;

    private String email;

    private String password;

}

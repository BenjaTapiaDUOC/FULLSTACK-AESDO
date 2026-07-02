package com.tuapp.msusuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Representa un usuario registrado en la plataforma.")
public class UsuarioResponseDTO {

    @Schema(description = "Identificador del usuario.", example = "1")
    private Long id;

    @Schema(description = "Nombre del usuario.", example = "Benjamin")
    private String nombre;

    @Schema(description = "Correo electrónico.", example = "benjamin@gmail.com")
    private String email;

    @Schema(description = "Contraseña. NOTA: para una aplicación real NO debería enviarse; se mantiene únicamente para los requerimientos académicos del proyecto.", example = "12345678")
    private String password;

}

package com.tuapp.msautenticacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Representa la información de autenticación de un usuario (sin exponer la contraseña).")
public class UsuarioResponseDTO {

    @Schema(description = "Identificador del registro de autenticación.", example = "1")
    private Long id;

    @Schema(description = "Identificador del usuario real en msusuarios.", example = "1")
    private Long usuarioId;

    @Schema(description = "Nombre del usuario.", example = "Benjamin")
    private String nombre;

    @Schema(description = "Correo electrónico.", example = "benjamin@gmail.com")
    private String email;

    @Schema(description = "Nombre del rol asignado.", example = "CLIENTE", allowableValues = {"ADMIN", "CLIENTE", "REPARTIDOR"})
    private String rol;

}

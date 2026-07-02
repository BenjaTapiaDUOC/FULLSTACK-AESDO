package com.tuapp.msautenticacion.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Representa un rol disponible en el sistema.")
public class RolResponseDTO {

    @Schema(description = "Identificador del rol.", example = "1")
    private Long id;

    @Schema(description = "Nombre del rol.", example = "CLIENTE", allowableValues = {"ADMIN", "CLIENTE", "REPARTIDOR"})
    private String nombre;

}

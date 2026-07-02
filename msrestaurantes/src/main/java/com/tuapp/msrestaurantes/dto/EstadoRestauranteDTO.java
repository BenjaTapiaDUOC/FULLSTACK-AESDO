package com.tuapp.msrestaurantes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ===========================================================
 * DTO PARA CAMBIO DE ESTADO
 * ===========================================================
 *
 * Este DTO se utiliza exclusivamente en el endpoint
 * PATCH /restaurantes/{id}/estado
 *
 * Permite activar o desactivar un restaurante sin
 * necesidad de enviar todos sus datos nuevamente.
 */

@Data
@Schema(description = "Nuevo estado a asignar al restaurante.")
public class EstadoRestauranteDTO {

    @Schema(description = "Nuevo estado del restaurante. true -> ACTIVO, false -> INACTIVO.", example = "false")
    @NotNull(message = "El campo 'activo' es obligatorio")
    private Boolean activo;

}

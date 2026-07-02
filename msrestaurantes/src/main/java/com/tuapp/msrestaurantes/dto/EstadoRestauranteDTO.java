package com.tuapp.msrestaurantes.dto;

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
public class EstadoRestauranteDTO {

    /**
     * Nuevo estado del restaurante.
     *
     * true  -> ACTIVO
     * false -> INACTIVO
     */
    @NotNull(message = "El campo 'activo' es obligatorio")
    private Boolean activo;

}

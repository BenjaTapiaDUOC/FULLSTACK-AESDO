package com.tuapp.msrestaurantes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE ENTRADA
 * ===========================================================
 *
 * Este DTO recibe la información enviada desde Postman.
 *
 * Se utiliza para evitar exponer directamente la entidad
 * Restaurante.
 */

@Data
@Schema(description = "Datos requeridos para crear o actualizar un restaurante.")
public class RestauranteRequestDTO {

    @Schema(description = "Nombre del restaurante.", example = "La Trattoria")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Dirección física del restaurante.", example = "Av. Siempre Viva 123")
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @Schema(description = "Categoría o tipo de cocina.", example = "Italiana")
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @Schema(description = "Horario de atención.", example = "09:00 - 22:00")
    @NotBlank(message = "El horario es obligatorio")
    private String horario;

    @Schema(description = "Estado del restaurante. Es opcional al crear; por defecto true (ACTIVO).", example = "true")
    private Boolean activo;

}

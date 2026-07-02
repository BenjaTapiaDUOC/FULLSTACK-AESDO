package com.tuapp.msrestaurantes.dto;

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
 * De esta forma no se expone directamente la entidad
 * Restaurante.
 *
 * Este mismo DTO es el que consumirá el microservicio
 * msproductos a través de WebClient para validar si un
 * restaurante existe y si se encuentra activo.
 */

@Data
@AllArgsConstructor
@Schema(description = "Representa un restaurante registrado en el sistema.")
public class RestauranteResponseDTO {

    @Schema(description = "Identificador del restaurante.", example = "1")
    private Long id;

    @Schema(description = "Nombre del restaurante.", example = "La Trattoria")
    private String nombre;

    @Schema(description = "Dirección física del restaurante.", example = "Av. Siempre Viva 123")
    private String direccion;

    @Schema(description = "Categoría o tipo de cocina.", example = "Italiana")
    private String categoria;

    @Schema(description = "Horario de atención.", example = "09:00 - 22:00")
    private String horario;

    @Schema(description = "Estado del restaurante (activo/inactivo).", example = "true")
    private Boolean activo;

}

package com.tuapp.msproductos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE ENTRADA
 * ===========================================================
 *
 * Este DTO recibe la información enviada desde Postman.
 *
 * Se utiliza para evitar exponer directamente la entidad
 * Producto.
 */

@Data
@Schema(description = "Datos requeridos para crear o actualizar un producto.")
public class ProductoRequestDTO {

    @Schema(description = "Nombre del producto.", example = "Pizza Napolitana")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Precio del producto. Debe ser mayor a 0.", example = "8990")
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @Schema(description = "Categoría del producto.", example = "Comida rápida")
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @Schema(description = "Identificador del restaurante al que pertenece el producto. Debe existir y estar activo en msrestaurantes.", example = "1")
    @NotNull(message = "El restauranteId es obligatorio")
    @Positive(message = "El restauranteId debe ser mayor a 0")
    private Long restauranteId;

}

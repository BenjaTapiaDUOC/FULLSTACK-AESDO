package com.tuapp.msproductos.dto;

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
 * De esta forma no se expone directamente la entidad Producto.
 */

@Data
@AllArgsConstructor
@Schema(description = "Representa un producto del catálogo.")
public class ProductoResponseDTO {

    @Schema(description = "Identificador del producto.", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto.", example = "Pizza Napolitana")
    private String nombre;

    @Schema(description = "Precio del producto.", example = "8990")
    private Double precio;

    @Schema(description = "Categoría del producto.", example = "Comida rápida")
    private String categoria;

    @Schema(description = "Identificador del restaurante al que pertenece el producto.", example = "1")
    private Long restauranteId;

}

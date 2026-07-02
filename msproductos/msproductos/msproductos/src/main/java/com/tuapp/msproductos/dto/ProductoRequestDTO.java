package com.tuapp.msproductos.dto;

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
public class ProductoRequestDTO {

    /**
     * Nombre del producto.
     */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /**
     * Precio del producto.
     *
     * Debe ser mayor a 0.
     */
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    /**
     * Categoría del producto.
     */
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

}
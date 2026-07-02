package com.tuapp.msproductos.dto;

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
public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private Double precio;
    private String categoria;

}
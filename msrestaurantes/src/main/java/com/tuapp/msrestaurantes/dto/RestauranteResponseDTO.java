package com.tuapp.msrestaurantes.dto;

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
public class RestauranteResponseDTO {

    /**
     * Identificador del restaurante.
     */
    private Long id;

    /**
     * Nombre del restaurante.
     */
    private String nombre;

    /**
     * Dirección física del restaurante.
     */
    private String direccion;

    /**
     * Categoría o tipo de cocina.
     */
    private String categoria;

    /**
     * Horario de atención.
     */
    private String horario;

    /**
     * Estado del restaurante (activo/inactivo).
     */
    private Boolean activo;

}

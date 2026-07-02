package com.tuapp.msrestaurantes.dto;

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
public class RestauranteRequestDTO {

    /**
     * Nombre del restaurante.
     */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /**
     * Dirección física del restaurante.
     */
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    /**
     * Categoría o tipo de cocina.
     */
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    /**
     * Horario de atención.
     */
    @NotBlank(message = "El horario es obligatorio")
    private String horario;

    /**
     * Estado del restaurante.
     *
     * Es opcional al crear: si no se envía, el servicio
     * asigna por defecto el valor true (ACTIVO).
     */
    private Boolean activo;

}

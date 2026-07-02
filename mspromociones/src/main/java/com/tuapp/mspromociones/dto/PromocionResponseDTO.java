package com.tuapp.mspromociones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * ===========================================================
 * DTO DE RESPUESTA
 * ===========================================================
 *
 * Este objeto se envía como respuesta al cliente.
 *
 * De esta forma no se expone directamente la entidad Promocion.
 */

@Data
@AllArgsConstructor
public class PromocionResponseDTO {

    /**
     * Identificador de la promoción.
     */
    private Long id;

    /**
     * Código del cupón de descuento.
     */
    private String codigo;

    /**
     * Porcentaje de descuento.
     */
    private Double porcentajeDescuento;

    /**
     * Fecha de inicio de vigencia.
     */
    private LocalDate fechaInicio;

    /**
     * Fecha de término de vigencia.
     */
    private LocalDate fechaFin;

    /**
     * Estado de la promoción.
     */
    private Boolean activo;

}

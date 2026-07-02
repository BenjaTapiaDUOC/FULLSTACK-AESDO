package com.tuapp.mspromociones.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * ===========================================================
 * DTO DE ENTRADA
 * ===========================================================
 *
 * Este DTO recibe la información enviada desde Postman.
 *
 * Se utiliza para evitar exponer directamente la entidad
 * Promocion.
 */

@Data
public class PromocionRequestDTO {

    /**
     * Código del cupón de descuento.
     */
    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    /**
     * Porcentaje de descuento.
     *
     * Debe estar entre 0 y 100.
     */
    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El porcentaje no puede ser negativo")
    @DecimalMax(value = "100.0", inclusive = true, message = "El porcentaje no puede ser mayor a 100")
    private Double porcentajeDescuento;

    /**
     * Fecha de inicio de vigencia del cupón.
     */
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    /**
     * Fecha de término de vigencia del cupón.
     */
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    /**
     * Estado de la promoción.
     *
     * Es opcional. Si no se envía, el servicio la
     * dejará activa por defecto.
     */
    private Boolean activo;

}

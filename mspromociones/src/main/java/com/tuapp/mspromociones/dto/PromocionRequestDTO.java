package com.tuapp.mspromociones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Datos requeridos para crear o actualizar un cupón de descuento.")
public class PromocionRequestDTO {

    @Schema(description = "Código del cupón de descuento.", example = "VERANO2026")
    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @Schema(description = "Porcentaje de descuento. Debe estar entre 0 y 100.", example = "15")
    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El porcentaje no puede ser negativo")
    @DecimalMax(value = "100.0", inclusive = true, message = "El porcentaje no puede ser mayor a 100")
    private Double porcentajeDescuento;

    @Schema(description = "Fecha de inicio de vigencia del cupón.", example = "2026-01-01")
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de término de vigencia del cupón.", example = "2026-03-31")
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @Schema(description = "Estado de la promoción. Es opcional; si no se envía, queda activa por defecto.", example = "true")
    private Boolean activo;

}

package com.tuapp.mspromociones.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Representa un cupón de descuento registrado en el sistema.")
public class PromocionResponseDTO {

    @Schema(description = "Identificador de la promoción.", example = "1")
    private Long id;

    @Schema(description = "Código del cupón de descuento.", example = "VERANO2026")
    private String codigo;

    @Schema(description = "Porcentaje de descuento.", example = "15")
    private Double porcentajeDescuento;

    @Schema(description = "Fecha de inicio de vigencia.", example = "2026-01-01")
    private LocalDate fechaInicio;

    @Schema(description = "Fecha de término de vigencia.", example = "2026-03-31")
    private LocalDate fechaFin;

    @Schema(description = "Estado de la promoción.", example = "true")
    private Boolean activo;

}

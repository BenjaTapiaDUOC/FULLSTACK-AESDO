package com.tuapp.msrepartidores.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ===========================================================
 * DTO DE ENTRADA
 * ===========================================================
 *
 * Este DTO recibe la información enviada desde Postman
 * para crear o actualizar un repartidor.
 *
 * Se utiliza para evitar exponer directamente la entidad
 * Repartidor.
 *
 * NOTA:
 * El estado no se define aquí, ya que un repartidor
 * siempre se crea en estado DISPONIBLE y solo puede
 * cambiar de estado a través del endpoint específico
 * PATCH /repartidores/{id}/estado.
 */

@Data
@Schema(description = "Datos requeridos para crear o actualizar un repartidor.")
public class RepartidorRequestDTO {

    @Schema(description = "Nombre completo del repartidor.", example = "Cristobal Soto")
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Schema(description = "Vehículo utilizado por el repartidor.", example = "Moto")
    @NotBlank(message = "El vehículo es obligatorio")
    private String vehiculo;

}

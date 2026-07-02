package com.tuapp.msrepartidores.dto;

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
public class RepartidorRequestDTO {

    /**
     * Nombre completo del repartidor.
     */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    /**
     * Vehículo utilizado por el repartidor.
     */
    @NotBlank(message = "El vehículo es obligatorio")
    private String vehiculo;

}

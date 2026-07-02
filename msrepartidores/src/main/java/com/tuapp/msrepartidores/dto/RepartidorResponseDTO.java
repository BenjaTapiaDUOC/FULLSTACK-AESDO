package com.tuapp.msrepartidores.dto;

import com.tuapp.msrepartidores.model.EstadoRepartidor;
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
 * Repartidor.
 */

@Data
@AllArgsConstructor
public class RepartidorResponseDTO {

    /**
     * Identificador del repartidor.
     */
    private Long id;

    /**
     * Nombre completo del repartidor.
     */
    private String nombre;

    /**
     * Vehículo utilizado por el repartidor.
     */
    private String vehiculo;

    /**
     * Estado actual del repartidor.
     */
    private EstadoRepartidor estado;

}

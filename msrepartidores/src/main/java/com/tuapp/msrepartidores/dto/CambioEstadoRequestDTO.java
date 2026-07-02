package com.tuapp.msrepartidores.dto;

import com.tuapp.msrepartidores.model.EstadoRepartidor;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ===========================================================
 * DTO CAMBIO DE ESTADO
 * ===========================================================
 *
 * Este DTO se utiliza exclusivamente para actualizar el
 * estado de un repartidor.
 *
 * Es consumido tanto desde Postman como desde otros
 * microservicios (por ejemplo msdelivery), que necesitan
 * marcar al repartidor como EN_RUTA al asignarle un
 * delivery, o devolverlo a DISPONIBLE cuando finaliza.
 */

@Data
public class CambioEstadoRequestDTO {

    /**
     * Nuevo estado del repartidor.
     *
     * Valores permitidos: DISPONIBLE, EN_RUTA, INACTIVO.
     */
    @NotNull(message = "El estado es obligatorio")
    private EstadoRepartidor estado;

}

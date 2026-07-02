package com.tuapp.msrepartidores.dto;

import com.tuapp.msrepartidores.model.EstadoRepartidor;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Representa un repartidor registrado en el sistema.")
public class RepartidorResponseDTO {

    @Schema(description = "Identificador del repartidor.", example = "1")
    private Long id;

    @Schema(description = "Nombre completo del repartidor.", example = "Cristobal Soto")
    private String nombre;

    @Schema(description = "Vehículo utilizado por el repartidor.", example = "Moto")
    private String vehiculo;

    @Schema(description = "Estado actual del repartidor.", example = "DISPONIBLE")
    private EstadoRepartidor estado;

}

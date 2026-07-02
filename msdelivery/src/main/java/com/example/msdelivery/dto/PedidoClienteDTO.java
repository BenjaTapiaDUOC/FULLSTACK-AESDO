package com.example.msdelivery.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================
 * DTO DE CONSUMO - PEDIDO (mspedidos)
 * ===========================================================
 *
 * Este DTO NO representa una entidad local. Se utiliza
 * únicamente para deserializar la respuesta JSON entregada
 * por el microservicio mspedidos al consultar
 * GET /pedidos/{id}.
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) evita errores si
 * mspedidos agrega nuevos campos (por ejemplo "detalles") que
 * msdelivery no necesita utilizar.
 */

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PedidoClienteDTO {

    private Long id;
    private Long usuarioId;
    private String estado;

}

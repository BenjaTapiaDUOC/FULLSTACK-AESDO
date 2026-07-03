package com.tuapp.msproductos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================
 * DTO DE CONSUMO - RESTAURANTE (msrestaurantes)
 * ===========================================================
 *
 * Este DTO NO representa una entidad local. Se utiliza
 * únicamente para deserializar la respuesta JSON entregada
 * por el microservicio msrestaurantes al consultar
 * GET /restaurantes/{id}.
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) evita errores si
 * msrestaurantes agrega nuevos campos (por ejemplo "horario")
 * que msproductos no necesita utilizar.
 */

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestauranteClienteDTO {

    private Long id;
    private String nombre;
    private Boolean activo;

}

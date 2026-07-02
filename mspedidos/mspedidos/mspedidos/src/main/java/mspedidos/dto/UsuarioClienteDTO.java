package mspedidos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ===========================================================
 * DTO DE CONSUMO - USUARIO (msusuarios)
 * ===========================================================
 *
 * Este DTO NO representa una entidad local. Se utiliza
 * únicamente para deserializar la respuesta JSON entregada
 * por el microservicio msusuarios al consultar
 * GET /usuarios/{id}.
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) evita errores si
 * msusuarios agrega nuevos campos (por ejemplo "password") que
 * mspedidos no necesita utilizar.
 */

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioClienteDTO {

    private Long id;
    private String nombre;
    private String email;

}

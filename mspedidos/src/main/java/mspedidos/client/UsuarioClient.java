package mspedidos.client;

import mspedidos.dto.UsuarioClienteDTO;
import mspedidos.exception.BadRequestException;
import mspedidos.exception.PedidoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * ===========================================================
 * CLIENTE HTTP - MICROSERVICIO MSUSUARIOS
 * ===========================================================
 *
 * Esta clase centraliza toda la comunicación entre mspedidos
 * y msusuarios.
 *
 * Se utiliza WebClient para consultar si un usuario existe
 * antes de permitir la creación de un pedido.
 *
 * Endpoint remoto consumido:
 *
 * GET http://localhost:8081/usuarios/{id}
 */

@Component
public class UsuarioClient {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioClient.class);

    private final WebClient usuariosWebClient;

    public UsuarioClient(WebClient usuariosWebClient) {
        this.usuariosWebClient = usuariosWebClient;
    }

    /**
     * ===========================================================
     * OBTENER USUARIO POR ID
     * ===========================================================
     *
     * Consulta al microservicio msusuarios para validar que el
     * usuario del pedido efectivamente existe.
     *
     * Manejo de errores:
     *
     * - 404 en msusuarios -> BadRequestException (400 en mspedidos)
     * - Timeout / msusuarios caído -> PedidoException (503)
     */
    public UsuarioClienteDTO obtenerUsuario(Long usuarioId) {

        logger.info("Consultando existencia del usuario {} en msusuarios", usuarioId);

        try {

            return usuariosWebClient.get()
                    .uri("/usuarios/{id}", usuarioId)
                    .retrieve()
                    .bodyToMono(UsuarioClienteDTO.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

        } catch (WebClientResponseException.NotFound ex) {

            logger.warn("El usuario {} no existe en msusuarios", usuarioId);

            throw new BadRequestException(
                    "El usuario con ID " + usuarioId + " no existe."
            );

        } catch (Exception ex) {

            logger.error("No fue posible comunicarse con msusuarios: {}", ex.getMessage());

            throw new PedidoException(
                    "No fue posible validar el usuario. El microservicio msusuarios no respondió."
            );

        }

    }

}

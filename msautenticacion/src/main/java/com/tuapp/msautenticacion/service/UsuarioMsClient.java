package com.tuapp.msautenticacion.service;

import com.tuapp.msautenticacion.dto.UsuarioMsRequestDTO;
import com.tuapp.msautenticacion.dto.UsuarioMsResponseDTO;
import com.tuapp.msautenticacion.exception.BadRequestException;
import com.tuapp.msautenticacion.exception.ServicioExternoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * ===========================================================
 * CLIENTE WEBCLIENT - MSUSUARIOS
 * ===========================================================
 *
 * Esta clase encapsula toda la comunicación HTTP con el
 * microservicio msusuarios (puerto 8081), utilizando
 * WebClient.
 *
 * Se utiliza durante el registro, ya que el usuario
 * "maestro" (nombre, email, password) debe existir en
 * msusuarios; este microservicio solo administra las
 * credenciales y el rol para la autenticación.
 *
 * Maneja timeouts y errores de conexión, lanzando
 * excepciones propias que son interpretadas por el
 * GlobalExceptionHandler.
 */

@Component
public class UsuarioMsClient {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioMsClient.class);

    private final WebClient webClient;

    public UsuarioMsClient(WebClient.Builder webClientBuilder,
                            @Value("${msusuarios.url}") String msusuariosUrl) {

        this.webClient = webClientBuilder.baseUrl(msusuariosUrl).build();
    }

    /**
     * ===========================================================
     * CREAR USUARIO REMOTO
     * ===========================================================
     *
     * Realiza un POST hacia msusuarios (/usuarios) para crear
     * el usuario maestro.
     *
     * Maneja los siguientes escenarios:
     *
     * - msusuarios responde 4xx (por ejemplo, correo duplicado)
     *   -> se traduce a BadRequestException (400).
     * - msusuarios no está disponible / timeout
     *   -> se traduce a ServicioExternoException (503).
     */
    public UsuarioMsResponseDTO crearUsuarioRemoto(String nombre, String email, String password) {

        logger.info("Solicitando a msusuarios la creación del usuario maestro: {}", email);

        try {

            return webClient.post()
                    .uri("")
                    .bodyValue(new UsuarioMsRequestDTO(nombre, email, password))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), response ->
                            response.bodyToMono(String.class)
                                    .defaultIfEmpty("Solicitud inválida")
                                    .flatMap(mensaje -> Mono.error(new BadRequestException(
                                            "msusuarios rechazó la solicitud: " + mensaje)))
                    )
                    .bodyToMono(UsuarioMsResponseDTO.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

        } catch (BadRequestException ex) {

            throw ex;

        } catch (WebClientRequestException | WebClientResponseException ex) {

            logger.error("No fue posible comunicarse con msusuarios: {}", ex.getMessage());

            throw new ServicioExternoException(
                    "El microservicio de usuarios (msusuarios) no está disponible.");

        } catch (Exception ex) {

            logger.error("Error inesperado al comunicarse con msusuarios: {}", ex.getMessage());

            throw new ServicioExternoException(
                    "Error al comunicarse con el microservicio de usuarios.");

        }

    }

}

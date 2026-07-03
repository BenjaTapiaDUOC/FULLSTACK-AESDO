package com.tuapp.msautenticacion.service.service;

import com.tuapp.msautenticacion.dto.UsuarioMsResponseDTO;
import com.tuapp.msautenticacion.exception.BadRequestException;
import com.tuapp.msautenticacion.exception.ServicioExternoException;

import com.tuapp.msautenticacion.service.UsuarioMsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - UsuarioMsClient
 * ===========================================================
 *
 * UsuarioMsClient construye internamente el WebClient a partir
 * de un WebClient.Builder, y usa la API fluida
 * (post().uri().bodyValue().retrieve().onStatus().bodyToMono()).
 * Por eso se mockea cada eslabon de la cadena para poder
 * controlar la respuesta (o el error) que "llega" desde
 * msusuarios.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
class UsuarioMsClientTest {

    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    private WebClient.RequestBodySpec requestBodySpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    private UsuarioMsClient usuarioMsClient;

    @BeforeEach
    void setUp() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);

        requestBodyUriSpec = mock(WebClient.RequestBodyUriSpec.class);
        requestBodySpec = mock(WebClient.RequestBodySpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(webClient);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

        usuarioMsClient = new UsuarioMsClient(builder, "http://localhost:8081/usuarios");
    }

    // ===========================================================
    // TEST 1: crearUsuarioRemoto() con respuesta exitosa debe
    // retornar el DTO recibido desde msusuarios.
    // ===========================================================
    @Test
    void crearUsuarioRemoto_conRespuestaExitosa_debeRetornarDTO() {

        UsuarioMsResponseDTO dto = new UsuarioMsResponseDTO(55L, "Benjamin", "benjamin@gmail.com", "12345678");

        when(responseSpec.bodyToMono(UsuarioMsResponseDTO.class)).thenReturn(Mono.just(dto));

        UsuarioMsResponseDTO respuesta =
                usuarioMsClient.crearUsuarioRemoto("Benjamin", "benjamin@gmail.com", "12345678");

        assertNotNull(respuesta);
        assertEquals(55L, respuesta.getId());
        assertEquals("Benjamin", respuesta.getNombre());
    }

    // ===========================================================
    // TEST 2: crearUsuarioRemoto() cuando el flujo interno
    // lanza BadRequestException (por ejemplo, msusuarios
    // rechazo la solicitud dentro del onStatus) debe
    // propagarla tal cual, sin envolverla.
    // ===========================================================
    @Test
    void crearUsuarioRemoto_conBadRequestException_debePropagarla() {

        when(responseSpec.bodyToMono(UsuarioMsResponseDTO.class))
                .thenReturn(Mono.error(new BadRequestException("msusuarios rechazo la solicitud: correo duplicado")));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> usuarioMsClient.crearUsuarioRemoto("Benjamin", "benjamin@gmail.com", "12345678")
        );

        assertTrue(ex.getMessage().contains("msusuarios rechazo la solicitud"));
    }

    // ===========================================================
    // TEST 3: crearUsuarioRemoto() cuando msusuarios responde
    // con un error de servidor (WebClientResponseException) que
    // no fue interceptado por onStatus, debe lanzar
    // ServicioExternoException.
    // ===========================================================
    @Test
    void crearUsuarioRemoto_conErrorDeRespuesta_debeLanzarServicioExternoException() {

        WebClientResponseException errorRespuesta = WebClientResponseException.create(
                500, "Internal Server Error", HttpHeaders.EMPTY, new byte[0], null
        );

        when(responseSpec.bodyToMono(UsuarioMsResponseDTO.class)).thenReturn(Mono.error(errorRespuesta));

        assertThrows(
                ServicioExternoException.class,
                () -> usuarioMsClient.crearUsuarioRemoto("Benjamin", "benjamin@gmail.com", "12345678")
        );
    }

    // ===========================================================
    // TEST 4: crearUsuarioRemoto() cuando msusuarios no esta
    // disponible (WebClientRequestException, timeout/conexion
    // rechazada) debe lanzar ServicioExternoException.
    // ===========================================================
    @Test
    void crearUsuarioRemoto_conMsusuariosCaido_debeLanzarServicioExternoException() {

        WebClientRequestException errorConexion = new WebClientRequestException(
                new IOException("Connection refused"), null, null, HttpHeaders.EMPTY
        );

        when(responseSpec.bodyToMono(UsuarioMsResponseDTO.class)).thenReturn(Mono.error(errorConexion));

        assertThrows(
                ServicioExternoException.class,
                () -> usuarioMsClient.crearUsuarioRemoto("Benjamin", "benjamin@gmail.com", "12345678")
        );
    }

    // ===========================================================
    // TEST 5: crearUsuarioRemoto() ante cualquier otro error
    // inesperado (no BadRequest, no WebClient*) tambien debe
    // lanzar ServicioExternoException.
    // ===========================================================
    @Test
    void crearUsuarioRemoto_conErrorInesperado_debeLanzarServicioExternoException() {

        when(responseSpec.bodyToMono(UsuarioMsResponseDTO.class))
                .thenReturn(Mono.error(new RuntimeException("Error inesperado")));

        assertThrows(
                ServicioExternoException.class,
                () -> usuarioMsClient.crearUsuarioRemoto("Benjamin", "benjamin@gmail.com", "12345678")
        );
    }

    // ===========================================================
    // TEST 6: verifica que el filtro registrado en onStatus()
    // (para respuestas 4xx de msusuarios) efectivamente arma un
    // Mono.error(BadRequestException) con el mensaje del cuerpo
    // recibido.
    // ===========================================================
    @Test
    void onStatus_conRespuesta4xx_debeMapearABadRequestException() {

        when(responseSpec.bodyToMono(UsuarioMsResponseDTO.class))
                .thenReturn(Mono.just(new UsuarioMsResponseDTO(1L, "x", "x@x.com", "x")));

        usuarioMsClient.crearUsuarioRemoto("Benjamin", "benjamin@gmail.com", "12345678");

        ArgumentCaptor<Predicate> predicateCaptor = ArgumentCaptor.forClass(Predicate.class);
        ArgumentCaptor<Function> functionCaptor = ArgumentCaptor.forClass(Function.class);

        verify(responseSpec, atLeastOnce()).onStatus(predicateCaptor.capture(), functionCaptor.capture());

        // El predicado debe activarse solo ante codigos 4xx.
        Predicate<HttpStatusCode> predicate = predicateCaptor.getValue();
        assertTrue(predicate.test(HttpStatus.BAD_REQUEST));
        assertFalse(predicate.test(HttpStatus.OK));

        // La funcion de mapeo debe transformar el cuerpo de la
        // respuesta de msusuarios en un BadRequestException.
        Function<ClientResponse, Mono<? extends Throwable>> function = functionCaptor.getValue();

        ClientResponse clientResponse = mock(ClientResponse.class);
        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("Correo ya registrado"));

        // El Mono devuelto termina con onError(BadRequestException) al
        // ser bloqueado (Mono.error no emite el throwable como valor,
        // lo propaga como señal de error), igual que ocurriria dentro
        // de la cadena real de WebClient.
        BadRequestException resultado = assertThrows(
                BadRequestException.class,
                () -> function.apply(clientResponse).block()
        );
        assertTrue(resultado.getMessage().contains("Correo ya registrado"));
    }

}

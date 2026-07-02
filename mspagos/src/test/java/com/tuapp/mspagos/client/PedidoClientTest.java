package com.tuapp.mspagos.client;

import com.tuapp.mspagos.exception.BadRequestException;
import com.tuapp.mspagos.exception.PagoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - PedidoClient
 * ===========================================================
 *
 * OJO: a diferencia de UsuarioClient (mspedidos), acá el método
 * usa retrieve().toBodilessEntity() en vez de bodyToMono(...),
 * porque solo nos interesa confirmar que el pedido existe
 * (no necesitamos el cuerpo de la respuesta).
 */
@SuppressWarnings({"unchecked", "rawtypes"})
class PedidoClientTest {

    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    private PedidoClient pedidoClient;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        pedidoClient = new PedidoClient(webClient);
    }

    // ===========================================================
    // TEST 1: validarPedidoExiste() con pedido existente no debe
    // lanzar ninguna excepción.
    // ===========================================================
    @Test
    void validarPedidoExiste_conPedidoExistente_noDebeLanzarExcepcion() {

        when(responseSpec.toBodilessEntity())
                .thenReturn(Mono.just(ResponseEntity.ok().build()));

        assertDoesNotThrow(() -> pedidoClient.validarPedidoExiste(1L));
    }

    // ===========================================================
    // TEST 2: validarPedidoExiste() cuando mspedidos responde 404
    // debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void validarPedidoExiste_conPedidoInexistente_debeLanzarBadRequestException() {

        WebClientResponseException notFound = WebClientResponseException.create(
                404, "Not Found", HttpHeaders.EMPTY, new byte[0], null
        );

        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(notFound));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> pedidoClient.validarPedidoExiste(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
    }

    // ===========================================================
    // TEST 3: validarPedidoExiste() cuando mspedidos no responde
    // (timeout, caído, etc.) debe lanzar PagoException.
    // ===========================================================
    @Test
    void validarPedidoExiste_conMspedidosCaido_debeLanzarPagoException() {

        when(responseSpec.toBodilessEntity())
                .thenReturn(Mono.error(new RuntimeException("Connection refused")));

        assertThrows(
                PagoException.class,
                () -> pedidoClient.validarPedidoExiste(1L)
        );
    }

}

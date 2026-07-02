package com.example.msdelivery.client;

import com.example.msdelivery.dto.PedidoClienteDTO;
import com.example.msdelivery.exception.BadRequestException;
import com.example.msdelivery.exception.DeliveryException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - PedidoClient
 * ===========================================================
 *
 * PedidoClient usa WebClient para comunicarse con mspedidos.
 * En lugar de levantar un servidor HTTP real (o depender de
 * mspedidos estar arriba), se reemplaza el ExchangeFunction
 * interno del WebClient por uno simulado con Mockito. Así se
 * controla exactamente qué respuesta (o error) "llega" desde
 * mspedidos, sin dependencias externas ni red.
 *
 * Esto permite cubrir las 3 ramas del método obtenerPedido():
 * 1. Respuesta exitosa -> retorna el DTO.
 * 2. Pedido no encontrado (404) -> BadRequestException.
 * 3. Cualquier otro error / timeout -> DeliveryException.
 */
class PedidoClientTest {

    private ExchangeFunction exchangeFunction;
    private PedidoClient pedidoClient;

    @BeforeEach
    void setUp() {
        exchangeFunction = mock(ExchangeFunction.class);

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8083")
                .exchangeFunction(exchangeFunction)
                .build();

        pedidoClient = new PedidoClient(webClient);
    }

    // ===========================================================
    // TEST 1: mspedidos responde 200 OK -> se debe retornar el DTO
    // correctamente deserializado.
    // ===========================================================
    @Test
    void obtenerPedido_conRespuestaExitosa_debeRetornarDTO() {

        ClientResponse response = ClientResponse.create(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body("{\"id\":1,\"usuarioId\":5,\"estado\":\"CREADO\"}")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(response));

        PedidoClienteDTO resultado = pedidoClient.obtenerPedido(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(5L, resultado.getUsuarioId());
        assertEquals("CREADO", resultado.getEstado());
    }

    // ===========================================================
    // TEST 2: mspedidos responde 404 -> debe lanzar BadRequestException
    // (el pedido indicado no existe).
    // ===========================================================
    @Test
    void obtenerPedido_conPedidoInexistente_debeLanzarBadRequestException() {

        ClientResponse response = ClientResponse.create(HttpStatus.NOT_FOUND).build();

        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(response));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> pedidoClient.obtenerPedido(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
    }

    // ===========================================================
    // TEST 3: mspedidos no responde / error de red -> debe lanzar
    // DeliveryException (503 en el GlobalExceptionHandler).
    // ===========================================================
    @Test
    void obtenerPedido_conErrorDeComunicacion_debeLanzarDeliveryException() {

        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Connection refused")));

        DeliveryException ex = assertThrows(
                DeliveryException.class,
                () -> pedidoClient.obtenerPedido(1L)
        );

        assertEquals(
                "No fue posible validar el pedido. El microservicio mspedidos no respondió.",
                ex.getMessage()
        );
    }

    // ===========================================================
    // TEST 4: mspedidos responde con un error 500 -> también debe
    // caer en DeliveryException (rama del catch genérico).
    // ===========================================================
    @Test
    void obtenerPedido_conErrorInternoDelServidor_debeLanzarDeliveryException() {

        ClientResponse response = ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(response));

        assertThrows(
                DeliveryException.class,
                () -> pedidoClient.obtenerPedido(1L)
        );
    }
}

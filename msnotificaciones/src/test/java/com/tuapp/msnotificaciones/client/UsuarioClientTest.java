package com.tuapp.msnotificaciones.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - UsuarioClient
 * ===========================================================
 *
 * En lugar de levantar un servidor HTTP real (msusuarios),
 * se reemplaza el ExchangeFunction interno del WebClient por
 * uno simulado (mock). Así se controla exactamente qué
 * respuesta HTTP "llega" desde msusuarios, sin red real y sin
 * necesidad de tener el otro microservicio corriendo.
 */
class UsuarioClientTest {

    // ===========================================================
    // TEST 1: si msusuarios responde 200 OK, existeUsuario()
    // debe retornar true.
    // ===========================================================
    @Test
    void existeUsuario_conUsuarioExistente_debeRetornarTrue() {

        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);
        ClientResponse respuestaSimulada = ClientResponse.create(HttpStatus.OK).build();

        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(respuestaSimulada));

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8081/usuarios")
                .exchangeFunction(exchangeFunction)
                .build();

        UsuarioClient client = new UsuarioClient(webClient);

        assertTrue(client.existeUsuario(1L));
    }

    // ===========================================================
    // TEST 2: si msusuarios responde 404 NOT FOUND, existeUsuario()
    // debe retornar false (sin lanzar la excepción hacia arriba).
    // ===========================================================
    @Test
    void existeUsuario_conUsuarioInexistente_debeRetornarFalse() {

        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);
        ClientResponse respuestaSimulada = ClientResponse.create(HttpStatus.NOT_FOUND).build();

        when(exchangeFunction.exchange(any())).thenReturn(Mono.just(respuestaSimulada));

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8081/usuarios")
                .exchangeFunction(exchangeFunction)
                .build();

        UsuarioClient client = new UsuarioClient(webClient);

        assertFalse(client.existeUsuario(99L));
    }

}

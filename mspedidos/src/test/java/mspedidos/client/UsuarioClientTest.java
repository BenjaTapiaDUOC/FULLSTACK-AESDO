package mspedidos.client;

import mspedidos.dto.UsuarioClienteDTO;
import mspedidos.exception.BadRequestException;
import mspedidos.exception.PedidoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - UsuarioClient
 * ===========================================================
 *
 * UsuarioClient usa la API fluida de WebClient
 * (get().uri(...).retrieve().bodyToMono(...)), por lo que hay
 * que mockear cada eslabón de la cadena para poder controlar
 * la respuesta (o el error) que "llega" desde msusuarios.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
class UsuarioClientTest {

    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    private UsuarioClient usuarioClient;

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

        usuarioClient = new UsuarioClient(webClient);
    }

    // ===========================================================
    // TEST 1: obtenerUsuario() con usuario existente debe
    // devolver el DTO recibido desde msusuarios.
    // ===========================================================
    @Test
    void obtenerUsuario_conUsuarioExistente_debeRetornarDTO() {

        UsuarioClienteDTO dto = new UsuarioClienteDTO();
        dto.setId(1L);
        dto.setNombre("Benjamin");
        dto.setEmail("benjamin@gmail.com");

        when(responseSpec.bodyToMono(UsuarioClienteDTO.class)).thenReturn(Mono.just(dto));

        UsuarioClienteDTO respuesta = usuarioClient.obtenerUsuario(1L);

        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("Benjamin", respuesta.getNombre());
    }

    // ===========================================================
    // TEST 2: obtenerUsuario() cuando msusuarios responde 404
    // debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void obtenerUsuario_conUsuarioInexistente_debeLanzarBadRequestException() {

        WebClientResponseException notFound = WebClientResponseException.create(
                404, "Not Found", HttpHeaders.EMPTY, new byte[0], null
        );

        when(responseSpec.bodyToMono(UsuarioClienteDTO.class)).thenReturn(Mono.error(notFound));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> usuarioClient.obtenerUsuario(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
    }

    // ===========================================================
    // TEST 3: obtenerUsuario() cuando msusuarios no responde
    // (timeout, conexión rechazada, error 500, etc.) debe
    // lanzar PedidoException.
    // ===========================================================
    @Test
    void obtenerUsuario_conMsusuariosCaido_debeLanzarPedidoException() {

        when(responseSpec.bodyToMono(UsuarioClienteDTO.class))
                .thenReturn(Mono.error(new RuntimeException("Connection refused")));

        assertThrows(
                PedidoException.class,
                () -> usuarioClient.obtenerUsuario(1L)
        );
    }

}

package com.tuapp.mspagos.service;

import com.tuapp.mspagos.client.PedidoClient;
import com.tuapp.mspagos.dto.PagoRequestDTO;
import com.tuapp.mspagos.dto.PagoResponseDTO;
import com.tuapp.mspagos.exception.BadRequestException;
import com.tuapp.mspagos.exception.PagoNotFoundException;
import com.tuapp.mspagos.model.Pago;
import com.tuapp.mspagos.repository.PagoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - PagoService
 * ===========================================================
 *
 * Este servicio es el mas complejo de mockear porque, ademas
 * del repository, recibe un WebClient inyectado directamente
 * (no encapsulado en un @Component como en msautenticacion o
 * mspedidos), y un PedidoClient propio.
 *
 * Para mockear la cadena fluida webClient.put().uri(...)
 * .retrieve().bodyToMono(...).block() usamos
 * Answers.RETURNS_DEEP_STUBS: Mockito genera automaticamente
 * un mock "encadenado" para cada llamada intermedia, sin que
 * tengamos que simular cada interfaz (RequestBodyUriSpec,
 * RequestHeadersSpec, etc.) a mano.
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository repository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    @Mock
    private PedidoClient pedidoClient;

    @InjectMocks
    private PagoService pagoService;

    private PagoRequestDTO requestValido;

    @BeforeEach
    void setUp() {
        requestValido = new PagoRequestDTO();
        requestValido.setPedidoId(10L);
        requestValido.setMonto(15000.0);
        requestValido.setMetodoPago("TARJETA");
    }

    // ===========================================================
    // TEST 1: crearPago() con un monto dentro del limite debe
    // quedar APROBADO y notificar a mspedidos (webClient.put...).
    // ===========================================================
    @Test
    void crearPago_conMontoDentroDelLimite_debeAprobarYNotificarPedido() {

        // GIVEN: el pedido existe (pedidoClient no lanza excepcion),
        // y el repository devuelve el mismo pago con id al guardar.
        doNothing().when(pedidoClient).validarPedidoExiste(10L);

        when(repository.save(any(Pago.class))).thenAnswer(invocacion -> {
            Pago p = invocacion.getArgument(0);
            p.setId(1L);
            return p;
        });

        // Gracias a RETURNS_DEEP_STUBS, no necesitamos stubear
        // cada paso de la cadena; alcanza con dejar que Mockito
        // devuelva mocks encadenados para put()/uri()/retrieve()/
        // bodyToMono()/block(). Por defecto .block() devuelve null,
        // que es exactamente lo que produce bodyToMono(Void.class).

        // WHEN
        PagoResponseDTO respuesta = pagoService.crearPago(requestValido);

        // THEN
        assertNotNull(respuesta);
        assertEquals("APROBADO", respuesta.getEstado());

        // Verificamos que efectivamente se intento notificar a
        // mspedidos mediante un PUT.
        verify(webClient, times(1)).put();
        verify(repository, times(1)).save(any(Pago.class));
    }

    // ===========================================================
    // TEST 2: crearPago() con un monto superior al maximo
    // permitido (5.000.000) debe quedar RECHAZADO, y NO debe
    // intentar notificar a mspedidos.
    // ===========================================================
    @Test
    void crearPago_conMontoSuperiorAlMaximo_debeRechazarPagoSinNotificar() {

        // GIVEN
        requestValido.setMonto(6_000_000.0);

        doNothing().when(pedidoClient).validarPedidoExiste(10L);
        when(repository.save(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setId(2L);
            return p;
        });

        // WHEN
        PagoResponseDTO respuesta = pagoService.crearPago(requestValido);

        // THEN
        assertEquals("RECHAZADO", respuesta.getEstado());

        // Un pago rechazado NUNCA debe notificar al microservicio
        // de pedidos (no tiene sentido marcarlo como "PAGADO").
        verify(webClient, never()).put();
    }

    // ===========================================================
    // TEST 3: crearPago() con un pedido que no existe en
    // mspedidos debe propagar la BadRequestException que lanza
    // PedidoClient, sin guardar nada.
    // ===========================================================
    @Test
    void crearPago_conPedidoInexistente_debeLanzarBadRequestException() {

        // GIVEN: pedidoClient detecta que el pedido no existe.
        doThrow(new BadRequestException("El pedido con ID 10 no existe. No es posible procesar el pago."))
                .when(pedidoClient).validarPedidoExiste(10L);

        // WHEN + THEN
        assertThrows(
                BadRequestException.class,
                () -> pagoService.crearPago(requestValido)
        );

        // La validacion del pedido ocurre ANTES de tocar el
        // repository, por lo que no debe guardarse ningun pago.
        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 4: actualizar() sobre un pago que ya esta APROBADO
    // debe lanzar BadRequestException (regla de integridad de
    // la transaccion).
    // ===========================================================
    @Test
    void actualizar_conPagoYaAprobado_debeLanzarBadRequestException() {

        // GIVEN
        Pago pagoAprobado = new Pago(
                1L, 10L, 15000.0, "TARJETA", "APROBADO", LocalDateTime.now());

        when(repository.findById(1L)).thenReturn(Optional.of(pagoAprobado));

        // WHEN + THEN
        assertThrows(
                BadRequestException.class,
                () -> pagoService.actualizar(1L, requestValido)
        );

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 5: eliminar() con un id inexistente debe lanzar
    // PagoNotFoundException.
    // ===========================================================
    @Test
    void eliminar_conIdInexistente_debeLanzarPagoNotFoundException() {

        // GIVEN
        when(repository.existsById(99L)).thenReturn(false);

        // WHEN + THEN
        assertThrows(
                PagoNotFoundException.class,
                () -> pagoService.eliminar(99L)
        );

        verify(repository, never()).deleteById(any());
    }
}

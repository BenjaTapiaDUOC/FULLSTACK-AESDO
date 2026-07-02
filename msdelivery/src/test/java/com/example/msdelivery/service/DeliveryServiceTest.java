package com.example.msdelivery.service;

import com.example.msdelivery.client.PedidoClient;
import com.example.msdelivery.dto.DeliveryRequestDTO;
import com.example.msdelivery.dto.DeliveryResponseDTO;
import com.example.msdelivery.dto.PedidoClienteDTO;
import com.example.msdelivery.exception.BadRequestException;
import com.example.msdelivery.exception.DeliveryNotFoundException;
import com.example.msdelivery.model.Delivery;
import com.example.msdelivery.repository.DeliveryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - DeliveryService
 * ===========================================================
 *
 * @ExtendWith(MockitoExtension.class) le dice a JUnit que
 * active Mockito en esta clase, para que las anotaciones
 * @Mock y @InjectMocks funcionen automáticamente.
 *
 * No se levanta el contexto de Spring (no es @SpringBootTest),
 * por lo que estas pruebas son rápidas: no tocan MySQL ni
 * hacen llamadas HTTP reales a mspedidos.
 */
@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    // --- DEPENDENCIAS FALSAS (MOCKS) ---
    @Mock
    private DeliveryRepository repository;

    @Mock
    private PedidoClient pedidoClient;

    // --- CLASE REAL BAJO PRUEBA ---
    @InjectMocks
    private DeliveryService deliveryService;

    private DeliveryRequestDTO deliveryRequestValido;

    @BeforeEach
    void setUp() {
        // Se ejecuta antes de CADA test. Preparamos un DTO válido
        // reutilizable para no repetir código en cada método.
        deliveryRequestValido = new DeliveryRequestDTO();
        deliveryRequestValido.setPedidoId(1L);
        deliveryRequestValido.setDireccionEntrega("Av. Siempre Viva 123");
        deliveryRequestValido.setRepartidor("Juan Pérez");
        deliveryRequestValido.setEstado("PENDIENTE");
    }

    // ===========================================================
    // TEST 1: crearDelivery() con datos válidos debe validar el
    // pedido en mspedidos y guardar el delivery correctamente.
    // ===========================================================
    @Test
    void crearDelivery_conDatosValidos_debeGuardarDelivery() {

        // GIVEN: mspedidos confirma que el pedido existe, no hay
        // delivery duplicado y el repository devuelve el id al guardar.
        when(pedidoClient.obtenerPedido(1L)).thenReturn(new PedidoClienteDTO());
        when(repository.existsByPedidoId(1L)).thenReturn(false);
        when(repository.save(any(Delivery.class))).thenAnswer(invocacion -> {
            Delivery d = invocacion.getArgument(0);
            d.setId(1L);
            return d;
        });

        // WHEN
        DeliveryResponseDTO respuesta = deliveryService.crearDelivery(deliveryRequestValido);

        // THEN
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("PENDIENTE", respuesta.getEstado());

        verify(pedidoClient, times(1)).obtenerPedido(1L);
        verify(repository, times(1)).save(any(Delivery.class));
    }

    // ===========================================================
    // TEST 2: crearDelivery() sobre un pedido que ya tiene delivery
    // asignado debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void crearDelivery_conPedidoYaAsignado_debeLanzarBadRequestException() {

        // GIVEN: el pedido existe pero ya tiene un delivery registrado
        when(pedidoClient.obtenerPedido(1L)).thenReturn(new PedidoClienteDTO());
        when(repository.existsByPedidoId(1L)).thenReturn(true);

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> deliveryService.crearDelivery(deliveryRequestValido)
        );

        assertEquals("El pedido ya tiene un delivery asignado.", ex.getMessage());

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 3: obtenerPorId() con un delivery inexistente debe
    // lanzar DeliveryNotFoundException.
    // ===========================================================
    @Test
    void obtenerPorId_conDeliveryInexistente_debeLanzarNotFoundException() {

        // GIVEN: el repositorio no encuentra el delivery
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                DeliveryNotFoundException.class,
                () -> deliveryService.obtenerPorId(99L)
        );
    }

    // ===========================================================
    // TEST 4: actualizar() cambiando el pedidoId hacia uno que ya
    // tiene delivery asignado debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void actualizar_conNuevoPedidoYaAsignado_debeLanzarBadRequestException() {

        // GIVEN: un delivery existente asociado al pedido 1
        Delivery deliveryExistente = new Delivery(1L, 1L, "Av. Siempre Viva 123", "Juan Pérez", "PENDIENTE");

        DeliveryRequestDTO dtoActualizado = new DeliveryRequestDTO();
        dtoActualizado.setPedidoId(2L);
        dtoActualizado.setDireccionEntrega("Nueva dirección 456");
        dtoActualizado.setRepartidor("Juan Pérez");
        dtoActualizado.setEstado("PENDIENTE");

        when(repository.findById(1L)).thenReturn(Optional.of(deliveryExistente));
        when(pedidoClient.obtenerPedido(2L)).thenReturn(new PedidoClienteDTO());
        when(repository.existsByPedidoId(2L)).thenReturn(true);

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> deliveryService.actualizar(1L, dtoActualizado)
        );

        assertEquals("El pedido ya tiene un delivery asignado a otro registro.", ex.getMessage());

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 5: eliminar() con un delivery existente debe eliminarlo
    // correctamente.
    // ===========================================================
    @Test
    void eliminar_conDeliveryExistente_debeEliminarCorrectamente() {

        // GIVEN: el delivery existe en el repositorio
        when(repository.existsById(1L)).thenReturn(true);

        // WHEN
        deliveryService.eliminar(1L);

        // THEN
        verify(repository, times(1)).deleteById(1L);
    }

    // ===========================================================
    // TEST 6 (NUEVO): eliminar() con un delivery inexistente debe
    // lanzar DeliveryNotFoundException y NO debe llamar deleteById.
    // ===========================================================
    @Test
    void eliminar_conDeliveryInexistente_debeLanzarNotFoundException() {

        // GIVEN
        when(repository.existsById(99L)).thenReturn(false);

        // WHEN + THEN
        assertThrows(
                DeliveryNotFoundException.class,
                () -> deliveryService.eliminar(99L)
        );

        verify(repository, never()).deleteById(any());
    }

    // ===========================================================
    // TEST 7 (NUEVO): listarDeliveries() con datos debe mapear
    // correctamente cada Delivery a su DeliveryResponseDTO.
    // ===========================================================
    @Test
    void listarDeliveries_conDatos_debeRetornarListaMapeada() {

        // GIVEN
        Delivery d1 = new Delivery(1L, 10L, "Calle Uno 111", "Ana Soto", "PENDIENTE");
        Delivery d2 = new Delivery(2L, 20L, "Calle Dos 222", "Luis Rojas", "EN_CAMINO");

        when(repository.findAll()).thenReturn(List.of(d1, d2));

        // WHEN
        List<DeliveryResponseDTO> resultado = deliveryService.listarDeliveries();

        // THEN
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals("PENDIENTE", resultado.get(0).getEstado());
        assertEquals(2L, resultado.get(1).getId());
        assertEquals("EN_CAMINO", resultado.get(1).getEstado());
    }

    // ===========================================================
    // TEST 8 (NUEVO): listarDeliveries() sin registros debe
    // retornar una lista vacía (no null).
    // ===========================================================
    @Test
    void listarDeliveries_sinRegistros_debeRetornarListaVacia() {

        // GIVEN
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // WHEN
        List<DeliveryResponseDTO> resultado = deliveryService.listarDeliveries();

        // THEN
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ===========================================================
    // TEST 9 (NUEVO): obtenerPorId() con un delivery existente
    // debe retornar el DTO correctamente mapeado.
    // ===========================================================
    @Test
    void obtenerPorId_conDeliveryExistente_debeRetornarDTO() {

        // GIVEN
        Delivery delivery = new Delivery(5L, 50L, "Av. Central 500", "Pedro Soto", "ENTREGADO");
        when(repository.findById(5L)).thenReturn(Optional.of(delivery));

        // WHEN
        DeliveryResponseDTO resultado = deliveryService.obtenerPorId(5L);

        // THEN
        assertEquals(5L, resultado.getId());
        assertEquals(50L, resultado.getPedidoId());
        assertEquals("ENTREGADO", resultado.getEstado());
    }

    // ===========================================================
    // TEST 10 (NUEVO): actualizar() sin cambiar el pedidoId no debe
    // revalidar contra mspedidos ni contra duplicados, solo guarda.
    // ===========================================================
    @Test
    void actualizar_sinCambiarPedidoId_debeActualizarSinRevalidarPedido() {

        // GIVEN
        Delivery deliveryExistente = new Delivery(1L, 1L, "Dirección vieja", "Juan Pérez", "PENDIENTE");

        DeliveryRequestDTO dtoActualizado = new DeliveryRequestDTO();
        dtoActualizado.setPedidoId(1L); // mismo pedidoId
        dtoActualizado.setDireccionEntrega("Dirección nueva 789");
        dtoActualizado.setRepartidor("Carla Díaz");
        dtoActualizado.setEstado("EN_CAMINO");

        when(repository.findById(1L)).thenReturn(Optional.of(deliveryExistente));
        when(repository.save(any(Delivery.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        DeliveryResponseDTO resultado = deliveryService.actualizar(1L, dtoActualizado);

        // THEN
        assertEquals("Dirección nueva 789", resultado.getDireccionEntrega());
        assertEquals("Carla Díaz", resultado.getRepartidor());
        assertEquals("EN_CAMINO", resultado.getEstado());

        // No debe llamarse a pedidoClient ni a existsByPedidoId porque el pedidoId no cambió
        verify(pedidoClient, never()).obtenerPedido(any());
        verify(repository, never()).existsByPedidoId(any());
        verify(repository, times(1)).save(any(Delivery.class));
    }

    // ===========================================================
    // TEST 11 (NUEVO): actualizar() cambiando el pedidoId hacia uno
    // válido (no duplicado) debe revalidar y guardar correctamente.
    // ===========================================================
    @Test
    void actualizar_cambiandoPedidoIdValido_debeActualizarCorrectamente() {

        // GIVEN
        Delivery deliveryExistente = new Delivery(1L, 1L, "Dirección vieja", "Juan Pérez", "PENDIENTE");

        DeliveryRequestDTO dtoActualizado = new DeliveryRequestDTO();
        dtoActualizado.setPedidoId(3L); // pedidoId distinto y válido
        dtoActualizado.setDireccionEntrega("Nueva dirección 999");
        dtoActualizado.setRepartidor("Marco Vera");
        dtoActualizado.setEstado("ENTREGADO");

        when(repository.findById(1L)).thenReturn(Optional.of(deliveryExistente));
        when(pedidoClient.obtenerPedido(3L)).thenReturn(new PedidoClienteDTO());
        when(repository.existsByPedidoId(3L)).thenReturn(false);
        when(repository.save(any(Delivery.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        DeliveryResponseDTO resultado = deliveryService.actualizar(1L, dtoActualizado);

        // THEN
        assertEquals(3L, resultado.getPedidoId());
        assertEquals("ENTREGADO", resultado.getEstado());

        verify(pedidoClient, times(1)).obtenerPedido(3L);
        verify(repository, times(1)).existsByPedidoId(3L);
        verify(repository, times(1)).save(any(Delivery.class));
    }

    // ===========================================================
    // TEST 12 (NUEVO): actualizar() sobre un delivery inexistente
    // debe lanzar DeliveryNotFoundException.
    // ===========================================================
    @Test
    void actualizar_conDeliveryInexistente_debeLanzarNotFoundException() {

        // GIVEN
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                DeliveryNotFoundException.class,
                () -> deliveryService.actualizar(99L, deliveryRequestValido)
        );

        verify(repository, never()).save(any());
    }
}

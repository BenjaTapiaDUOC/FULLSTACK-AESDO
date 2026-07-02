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
}

package com.example.msdelivery.controller;

import com.example.msdelivery.dto.DeliveryRequestDTO;
import com.example.msdelivery.dto.DeliveryResponseDTO;
import com.example.msdelivery.service.DeliveryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - DeliveryController
 * ===========================================================
 *
 * Se prueba el controller de forma aislada, sin levantar el
 * contexto de Spring (MockMvc/@WebMvcTest), instanciándolo
 * directamente y simulando el DeliveryService con Mockito.
 *
 * Esto valida:
 * - Que cada endpoint delega correctamente en el service.
 * - Que se retorna el código HTTP correcto en cada caso.
 * - Que el body de la respuesta corresponde a lo entregado
 *   por el service.
 */
@ExtendWith(MockitoExtension.class)
class DeliveryControllerTest {

    @Mock
    private DeliveryService service;

    private DeliveryController controller;

    private DeliveryRequestDTO requestDTO;
    private DeliveryResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        controller = new DeliveryController(service);

        requestDTO = new DeliveryRequestDTO();
        requestDTO.setPedidoId(1L);
        requestDTO.setDireccionEntrega("Av. Siempre Viva 123");
        requestDTO.setRepartidor("Juan Pérez");
        requestDTO.setEstado("PENDIENTE");

        responseDTO = new DeliveryResponseDTO(1L, 1L, "Av. Siempre Viva 123", "Juan Pérez", "PENDIENTE");
    }

    // ===========================================================
    // POST /delivery -> debe retornar 201 CREATED con el delivery
    // ===========================================================
    @Test
    void crear_conDatosValidos_debeRetornar201YDelivery() {

        when(service.crearDelivery(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<DeliveryResponseDTO> respuesta = controller.crear(requestDTO);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(responseDTO.getId(), respuesta.getBody().getId());
        assertEquals(responseDTO.getEstado(), respuesta.getBody().getEstado());

        verify(service, times(1)).crearDelivery(requestDTO);
    }

    // ===========================================================
    // GET /delivery -> debe retornar 200 OK con el listado completo
    // ===========================================================
    @Test
    void listar_debeRetornar200YListadoCompleto() {

        when(service.listarDeliveries()).thenReturn(List.of(responseDTO));

        ResponseEntity<List<DeliveryResponseDTO>> respuesta = controller.listar();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(1, respuesta.getBody().size());

        verify(service, times(1)).listarDeliveries();
    }

    // ===========================================================
    // GET /delivery -> con lista vacía también debe responder 200 OK
    // ===========================================================
    @Test
    void listar_sinRegistros_debeRetornar200YListaVacia() {

        when(service.listarDeliveries()).thenReturn(List.of());

        ResponseEntity<List<DeliveryResponseDTO>> respuesta = controller.listar();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().isEmpty());
    }

    // ===========================================================
    // GET /delivery/{id} -> debe retornar 200 OK con el delivery
    // ===========================================================
    @Test
    void obtener_conIdExistente_debeRetornar200YDelivery() {

        when(service.obtenerPorId(1L)).thenReturn(responseDTO);

        ResponseEntity<DeliveryResponseDTO> respuesta = controller.obtener(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(responseDTO.getId(), respuesta.getBody().getId());

        verify(service, times(1)).obtenerPorId(1L);
    }

    // ===========================================================
    // PUT /delivery/{id} -> debe retornar 200 OK con el delivery
    // actualizado
    // ===========================================================
    @Test
    void actualizar_conDatosValidos_debeRetornar200YDeliveryActualizado() {

        DeliveryResponseDTO actualizado = new DeliveryResponseDTO(1L, 1L, "Nueva dirección", "Pedro Soto", "EN_CAMINO");
        when(service.actualizar(1L, requestDTO)).thenReturn(actualizado);

        ResponseEntity<DeliveryResponseDTO> respuesta = controller.actualizar(1L, requestDTO);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("EN_CAMINO", respuesta.getBody().getEstado());

        verify(service, times(1)).actualizar(1L, requestDTO);
    }

    // ===========================================================
    // DELETE /delivery/{id} -> debe retornar 204 NO CONTENT
    // ===========================================================
    @Test
    void eliminar_conIdExistente_debeRetornar204() {

        doNothing().when(service).eliminar(1L);

        ResponseEntity<Void> respuesta = controller.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, respuesta.getStatusCode());
        assertNull(respuesta.getBody());

        verify(service, times(1)).eliminar(1L);
    }
}

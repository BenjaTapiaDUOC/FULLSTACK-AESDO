package com.tuapp.msrepartidores.service.controller;

import com.tuapp.msrepartidores.controller.RepartidorController;
import com.tuapp.msrepartidores.dto.CambioEstadoRequestDTO;
import com.tuapp.msrepartidores.dto.RepartidorRequestDTO;
import com.tuapp.msrepartidores.dto.RepartidorResponseDTO;
import com.tuapp.msrepartidores.model.EstadoRepartidor;
import com.tuapp.msrepartidores.service.RepartidorService;

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
 * PRUEBAS UNITARIAS - RepartidorController
 * ===========================================================
 *
 * Se prueba el controller de forma aislada, sin levantar el
 * contexto de Spring (MockMvc/@WebMvcTest), instanciándolo
 * directamente y simulando el RepartidorService con Mockito.
 *
 * Esto valida:
 * - Que cada endpoint delega correctamente en el service.
 * - Que se retorna el código HTTP correcto en cada caso.
 * - Que el body de la respuesta corresponde a lo entregado
 *   por el service.
 */
@ExtendWith(MockitoExtension.class)
class RepartidorControllerTest {

    @Mock
    private RepartidorService service;

    private RepartidorController controller;

    private RepartidorRequestDTO requestDTO;
    private RepartidorResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        controller = new RepartidorController(service);

        requestDTO = new RepartidorRequestDTO();
        requestDTO.setNombre("Cristobal Soto");
        requestDTO.setVehiculo("Moto");

        responseDTO = new RepartidorResponseDTO(1L, "Cristobal Soto", "Moto", EstadoRepartidor.DISPONIBLE);
    }

    // ===========================================================
    // POST /repartidores -> debe retornar 201 CREATED
    // ===========================================================
    @Test
    void crear_conDatosValidos_debeRetornar201YRepartidor() {

        when(service.crearRepartidor(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<RepartidorResponseDTO> respuesta = controller.crear(requestDTO);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals(EstadoRepartidor.DISPONIBLE, respuesta.getBody().getEstado());

        verify(service, times(1)).crearRepartidor(requestDTO);
    }

    // ===========================================================
    // GET /repartidores -> debe retornar 200 OK con el listado
    // ===========================================================
    @Test
    void listar_debeRetornar200YListadoCompleto() {

        when(service.listarRepartidores()).thenReturn(List.of(responseDTO));

        ResponseEntity<List<RepartidorResponseDTO>> respuesta = controller.listar();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());

        verify(service, times(1)).listarRepartidores();
    }

    // ===========================================================
    // GET /repartidores/disponibles -> debe retornar 200 OK
    // ===========================================================
    @Test
    void listarDisponibles_debeRetornar200YSoloDisponibles() {

        when(service.listarDisponibles()).thenReturn(List.of(responseDTO));

        ResponseEntity<List<RepartidorResponseDTO>> respuesta = controller.listarDisponibles();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
        assertEquals(EstadoRepartidor.DISPONIBLE, respuesta.getBody().get(0).getEstado());

        verify(service, times(1)).listarDisponibles();
    }

    // ===========================================================
    // GET /repartidores/{id} -> debe retornar 200 OK con el
    // repartidor solicitado
    // ===========================================================
    @Test
    void obtener_conIdExistente_debeRetornar200YRepartidor() {

        when(service.obtenerPorId(1L)).thenReturn(responseDTO);

        ResponseEntity<RepartidorResponseDTO> respuesta = controller.obtener(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(responseDTO.getId(), respuesta.getBody().getId());

        verify(service, times(1)).obtenerPorId(1L);
    }

    // ===========================================================
    // PUT /repartidores/{id} -> debe retornar 200 OK con el
    // repartidor actualizado
    // ===========================================================
    @Test
    void actualizar_conDatosValidos_debeRetornar200YRepartidorActualizado() {

        RepartidorResponseDTO actualizado = new RepartidorResponseDTO(1L, "Nombre nuevo", "Bicicleta", EstadoRepartidor.DISPONIBLE);
        when(service.actualizar(1L, requestDTO)).thenReturn(actualizado);

        ResponseEntity<RepartidorResponseDTO> respuesta = controller.actualizar(1L, requestDTO);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Nombre nuevo", respuesta.getBody().getNombre());

        verify(service, times(1)).actualizar(1L, requestDTO);
    }

    // ===========================================================
    // PATCH /repartidores/{id}/estado -> debe retornar 200 OK
    // con el nuevo estado
    // ===========================================================
    @Test
    void cambiarEstado_conDatosValidos_debeRetornar200YNuevoEstado() {

        CambioEstadoRequestDTO cambioEstado = new CambioEstadoRequestDTO();
        cambioEstado.setEstado(EstadoRepartidor.EN_RUTA);

        RepartidorResponseDTO enRuta = new RepartidorResponseDTO(1L, "Cristobal Soto", "Moto", EstadoRepartidor.EN_RUTA);
        when(service.cambiarEstado(1L, cambioEstado)).thenReturn(enRuta);

        ResponseEntity<RepartidorResponseDTO> respuesta = controller.cambiarEstado(1L, cambioEstado);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(EstadoRepartidor.EN_RUTA, respuesta.getBody().getEstado());

        verify(service, times(1)).cambiarEstado(1L, cambioEstado);
    }

    // ===========================================================
    // DELETE /repartidores/{id} -> debe retornar 204 NO CONTENT
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

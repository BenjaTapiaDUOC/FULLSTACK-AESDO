package com.tuapp.msrestaurantes.controller;

import com.tuapp.msrestaurantes.dto.EstadoRestauranteDTO;
import com.tuapp.msrestaurantes.dto.RestauranteRequestDTO;
import com.tuapp.msrestaurantes.dto.RestauranteResponseDTO;
import com.tuapp.msrestaurantes.service.RestauranteService;

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
 * PRUEBAS UNITARIAS - RestauranteController
 * ===========================================================
 *
 * Se prueba el controller de forma aislada, sin levantar el
 * contexto de Spring, instanciándolo directamente y simulando
 * el RestauranteService con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class RestauranteControllerTest {

    @Mock
    private RestauranteService service;

    private RestauranteController controller;

    private RestauranteRequestDTO requestDTO;
    private RestauranteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        controller = new RestauranteController(service);

        requestDTO = new RestauranteRequestDTO();
        requestDTO.setNombre("La Trattoria");
        requestDTO.setDireccion("Av. Siempre Viva 123");
        requestDTO.setCategoria("Italiana");
        requestDTO.setHorario("09:00 - 22:00");
        requestDTO.setActivo(true);

        responseDTO = new RestauranteResponseDTO(1L, "La Trattoria", "Av. Siempre Viva 123", "Italiana", "09:00 - 22:00", true);
    }

    // ===========================================================
    // POST /restaurantes -> debe retornar 201 CREATED
    // ===========================================================
    @Test
    void crear_conDatosValidos_debeRetornar201YRestaurante() {

        when(service.crearRestaurante(requestDTO)).thenReturn(responseDTO);

        ResponseEntity<RestauranteResponseDTO> respuesta = controller.crear(requestDTO);

        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("La Trattoria", respuesta.getBody().getNombre());

        verify(service, times(1)).crearRestaurante(requestDTO);
    }

    // ===========================================================
    // GET /restaurantes -> debe retornar 200 OK con el listado
    // ===========================================================
    @Test
    void listar_debeRetornar200YListadoCompleto() {

        when(service.listarRestaurantes()).thenReturn(List.of(responseDTO));

        ResponseEntity<List<RestauranteResponseDTO>> respuesta = controller.listar();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());

        verify(service, times(1)).listarRestaurantes();
    }

    // ===========================================================
    // GET /restaurantes/activos -> debe retornar 200 OK
    // ===========================================================
    @Test
    void listarActivos_debeRetornar200YSoloActivos() {

        when(service.listarActivos()).thenReturn(List.of(responseDTO));

        ResponseEntity<List<RestauranteResponseDTO>> respuesta = controller.listarActivos();

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(1, respuesta.getBody().size());
        assertTrue(respuesta.getBody().get(0).getActivo());

        verify(service, times(1)).listarActivos();
    }

    // ===========================================================
    // GET /restaurantes/{id} -> debe retornar 200 OK
    // ===========================================================
    @Test
    void obtener_conIdExistente_debeRetornar200YRestaurante() {

        when(service.obtenerPorId(1L)).thenReturn(responseDTO);

        ResponseEntity<RestauranteResponseDTO> respuesta = controller.obtener(1L);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(responseDTO.getId(), respuesta.getBody().getId());

        verify(service, times(1)).obtenerPorId(1L);
    }

    // ===========================================================
    // PUT /restaurantes/{id} -> debe retornar 200 OK
    // ===========================================================
    @Test
    void actualizar_conDatosValidos_debeRetornar200YRestauranteActualizado() {

        RestauranteResponseDTO actualizado = new RestauranteResponseDTO(1L, "La Trattoria", "Dir nueva", "Italiana", "10:00 - 23:00", true);
        when(service.actualizar(1L, requestDTO)).thenReturn(actualizado);

        ResponseEntity<RestauranteResponseDTO> respuesta = controller.actualizar(1L, requestDTO);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Dir nueva", respuesta.getBody().getDireccion());

        verify(service, times(1)).actualizar(1L, requestDTO);
    }

    // ===========================================================
    // PATCH /restaurantes/{id}/estado -> debe retornar 200 OK
    // ===========================================================
    @Test
    void cambiarEstado_conDatosValidos_debeRetornar200YNuevoEstado() {

        EstadoRestauranteDTO estadoDTO = new EstadoRestauranteDTO();
        estadoDTO.setActivo(false);

        RestauranteResponseDTO inactivo = new RestauranteResponseDTO(1L, "La Trattoria", "Dir", "Italiana", "09:00 - 22:00", false);
        when(service.cambiarEstado(1L, false)).thenReturn(inactivo);

        ResponseEntity<RestauranteResponseDTO> respuesta = controller.cambiarEstado(1L, estadoDTO);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertFalse(respuesta.getBody().getActivo());

        verify(service, times(1)).cambiarEstado(1L, false);
    }

    // ===========================================================
    // DELETE /restaurantes/{id} -> debe retornar 204 NO CONTENT
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

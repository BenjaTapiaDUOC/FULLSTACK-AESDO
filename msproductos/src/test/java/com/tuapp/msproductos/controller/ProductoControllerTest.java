package com.tuapp.msproductos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuapp.msproductos.dto.ProductoRequestDTO;
import com.tuapp.msproductos.dto.ProductoResponseDTO;
import com.tuapp.msproductos.service.ProductoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - ProductoController
 * ===========================================================
 *
 * Se prueba el controller de forma aislada (standalone MockMvc),
 * mockeando ProductoService. Así se cubren los 5 endpoints
 * (crear, listar, obtener, actualizar, eliminar) sin necesitar
 * levantar el contexto completo de Spring ni base de datos.
 */
@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService service;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProductoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        ProductoController controller = new ProductoController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        responseDTO = new ProductoResponseDTO(
                1L,
                "Pizza Napolitana",
                8990.0,
                "Comida rápida",
                10L
        );
    }

    // ===========================================================
    // TEST 1: POST /productos -> 201 CREATED
    // ===========================================================
    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {

        ProductoRequestDTO requestDTO = new ProductoRequestDTO();
        requestDTO.setNombre("Pizza Napolitana");
        requestDTO.setPrecio(8990.0);
        requestDTO.setCategoria("Comida rápida");
        requestDTO.setRestauranteId(10L);

        when(service.crearProducto(any(ProductoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/productos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Pizza Napolitana"))
                .andExpect(jsonPath("$.precio").value(8990.0));
    }

    // ===========================================================
    // TEST 2: GET /productos -> 200 OK con listado
    // ===========================================================
    @Test
    void listar_debeRetornar200ConListado() throws Exception {

        when(service.listarProductos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Pizza Napolitana"));
    }

    // ===========================================================
    // TEST 3: GET /productos/{id} -> 200 OK
    // ===========================================================
    @Test
    void obtener_conIdExistente_debeRetornar200() throws Exception {

        when(service.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/productos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.categoria").value("Comida rápida"));
    }

    // ===========================================================
    // TEST 4: PUT /productos/{id} -> 200 OK
    // ===========================================================
    @Test
    void actualizar_conDatosValidos_debeRetornar200() throws Exception {

        ProductoRequestDTO requestDTO = new ProductoRequestDTO();
        requestDTO.setNombre("Pizza Napolitana Familiar");
        requestDTO.setPrecio(12990.0);
        requestDTO.setCategoria("Comida rápida");
        requestDTO.setRestauranteId(10L);

        ProductoResponseDTO actualizado = new ProductoResponseDTO(
                1L, "Pizza Napolitana Familiar", 12990.0, "Comida rápida", 10L
        );

        when(service.actualizar(anyLong(), any(ProductoRequestDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/productos/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pizza Napolitana Familiar"))
                .andExpect(jsonPath("$.precio").value(12990.0));
    }

    // ===========================================================
    // TEST 5: DELETE /productos/{id} -> 204 NO CONTENT
    // ===========================================================
    @Test
    void eliminar_conIdExistente_debeRetornar204() throws Exception {

        mockMvc.perform(delete("/productos/{id}", 1L))
                .andExpect(status().isNoContent());
    }

}

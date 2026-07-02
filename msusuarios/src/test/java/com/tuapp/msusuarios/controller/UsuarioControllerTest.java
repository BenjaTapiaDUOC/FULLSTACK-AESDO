package com.tuapp.msusuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuapp.msusuarios.dto.UsuarioRequestDTO;
import com.tuapp.msusuarios.dto.UsuarioResponseDTO;
import com.tuapp.msusuarios.service.UsuarioService;

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
 * PRUEBAS UNITARIAS - UsuarioController
 * ===========================================================
 *
 * Se prueba el controller de forma aislada (standalone MockMvc),
 * mockeando UsuarioService. Así se cubren los 5 endpoints
 * (crear, listar, obtener, actualizar, eliminar) sin necesitar
 * levantar el contexto completo de Spring ni base de datos.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService service;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UsuarioResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        UsuarioController controller = new UsuarioController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        responseDTO = new UsuarioResponseDTO(
                1L,
                "Benjamin",
                "benjamin@gmail.com",
                "12345678"
        );
    }

    // ===========================================================
    // TEST 1: POST /usuarios -> 201 CREATED
    // ===========================================================
    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNombre("Benjamin");
        requestDTO.setEmail("benjamin@gmail.com");
        requestDTO.setPassword("12345678");

        when(service.crearUsuario(any(UsuarioRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/usuarios")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Benjamin"))
                .andExpect(jsonPath("$.email").value("benjamin@gmail.com"));
    }

    // ===========================================================
    // TEST 2: GET /usuarios -> 200 OK con listado
    // ===========================================================
    @Test
    void listar_debeRetornar200ConListado() throws Exception {

        when(service.listarUsuarios()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Benjamin"));
    }

    // ===========================================================
    // TEST 3: GET /usuarios/{id} -> 200 OK
    // ===========================================================
    @Test
    void obtener_conIdExistente_debeRetornar200() throws Exception {

        when(service.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("benjamin@gmail.com"));
    }

    // ===========================================================
    // TEST 4: PUT /usuarios/{id} -> 200 OK
    // ===========================================================
    @Test
    void actualizar_conDatosValidos_debeRetornar200() throws Exception {

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO();
        requestDTO.setNombre("Benja");
        requestDTO.setEmail("nuevo@gmail.com");
        requestDTO.setPassword("87654321");

        UsuarioResponseDTO actualizado = new UsuarioResponseDTO(
                1L, "Benja", "nuevo@gmail.com", "87654321"
        );

        when(service.actualizar(anyLong(), any(UsuarioRequestDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/usuarios/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Benja"))
                .andExpect(jsonPath("$.email").value("nuevo@gmail.com"));
    }

    // ===========================================================
    // TEST 5: DELETE /usuarios/{id} -> 204 NO CONTENT
    // ===========================================================
    @Test
    void eliminar_conIdExistente_debeRetornar204() throws Exception {

        mockMvc.perform(delete("/usuarios/{id}", 1L))
                .andExpect(status().isNoContent());
    }

}

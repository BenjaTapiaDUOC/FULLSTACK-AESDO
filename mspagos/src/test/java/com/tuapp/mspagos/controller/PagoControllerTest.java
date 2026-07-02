package com.tuapp.mspagos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuapp.mspagos.dto.PagoRequestDTO;
import com.tuapp.mspagos.dto.PagoResponseDTO;
import com.tuapp.mspagos.service.PagoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - PagoController
 * ===========================================================
 *
 * Se prueba el controller de forma aislada (standalone MockMvc),
 * mockeando PagoService. Cubre los 5 endpoints sin necesitar
 * levantar el contexto de Spring, base de datos ni comunicación
 * real con mspedidos.
 */
@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    @Mock
    private PagoService service;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private PagoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        PagoController controller = new PagoController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        responseDTO = new PagoResponseDTO(
                1L, 10L, 15000.0, "TARJETA", "APROBADO", LocalDateTime.now()
        );
    }

    // ===========================================================
    // TEST 1: POST /pagos -> 201 CREATED
    // ===========================================================
    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {

        PagoRequestDTO requestDTO = new PagoRequestDTO();
        requestDTO.setPedidoId(10L);
        requestDTO.setMonto(15000.0);
        requestDTO.setMetodoPago("TARJETA");

        when(service.crearPago(any(PagoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/pagos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }

    // ===========================================================
    // TEST 2: GET /pagos -> 200 OK con listado
    // ===========================================================
    @Test
    void listar_debeRetornar200ConListado() throws Exception {

        when(service.listarPagos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // ===========================================================
    // TEST 3: GET /pagos/{id} -> 200 OK
    // ===========================================================
    @Test
    void obtener_conIdExistente_debeRetornar200() throws Exception {

        when(service.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/pagos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ===========================================================
    // TEST 4: PUT /pagos/{id} -> 200 OK
    // ===========================================================
    @Test
    void actualizar_conDatosValidos_debeRetornar200() throws Exception {

        PagoRequestDTO requestDTO = new PagoRequestDTO();
        requestDTO.setPedidoId(10L);
        requestDTO.setMonto(20000.0);
        requestDTO.setMetodoPago("EFECTIVO");

        PagoResponseDTO actualizado = new PagoResponseDTO(
                1L, 10L, 20000.0, "EFECTIVO", "PENDIENTE", LocalDateTime.now()
        );

        when(service.actualizar(eq(1L), any(PagoRequestDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/pagos/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metodoPago").value("EFECTIVO"));
    }

    // ===========================================================
    // TEST 5: DELETE /pagos/{id} -> 204 NO CONTENT
    // ===========================================================
    @Test
    void eliminar_conIdExistente_debeRetornar204() throws Exception {

        mockMvc.perform(delete("/pagos/{id}", 1L))
                .andExpect(status().isNoContent());
    }

}

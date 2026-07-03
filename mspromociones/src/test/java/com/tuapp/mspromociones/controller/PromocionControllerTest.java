package com.tuapp.mspromociones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tuapp.mspromociones.dto.PromocionRequestDTO;
import com.tuapp.mspromociones.dto.PromocionResponseDTO;
import com.tuapp.mspromociones.service.PromocionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - PromocionController
 * ===========================================================
 *
 * Se prueba el controller de forma aislada (standalone MockMvc),
 * mockeando PromocionService. Así se cubren los 7 endpoints
 * (crear, listar, obtener, actualizar, eliminar, validar,
 * aplicar) sin necesitar levantar el contexto completo de
 * Spring ni base de datos.
 */
@ExtendWith(MockitoExtension.class)
class PromocionControllerTest {

    @Mock
    private PromocionService service;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private PromocionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        PromocionController controller = new PromocionController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        responseDTO = new PromocionResponseDTO(
                1L,
                "VERANO2026",
                15.0,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30),
                true
        );
    }

    // ===========================================================
    // TEST 1: POST /promociones -> 201 CREATED
    // ===========================================================
    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {

        PromocionRequestDTO requestDTO = new PromocionRequestDTO();
        requestDTO.setCodigo("VERANO2026");
        requestDTO.setPorcentajeDescuento(15.0);
        requestDTO.setFechaInicio(LocalDate.now().minusDays(1));
        requestDTO.setFechaFin(LocalDate.now().plusDays(30));
        requestDTO.setActivo(true);

        when(service.crearPromocion(any(PromocionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/promociones")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("VERANO2026"));
    }

    // ===========================================================
    // TEST 2: POST /promociones con datos inválidos -> 400
    // (activa la validación @Valid del DTO)
    // ===========================================================
    @Test
    void crear_conDatosInvalidos_debeRetornar400() throws Exception {

        PromocionRequestDTO requestInvalido = new PromocionRequestDTO();
        requestInvalido.setCodigo("");
        requestInvalido.setPorcentajeDescuento(150.0);

        mockMvc.perform(post("/promociones")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    // ===========================================================
    // TEST 3: GET /promociones -> 200 OK con listado
    // ===========================================================
    @Test
    void listar_debeRetornar200ConListado() throws Exception {

        when(service.listarPromociones()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/promociones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].codigo").value("VERANO2026"));
    }

    // ===========================================================
    // TEST 4: GET /promociones/{id} -> 200 OK
    // ===========================================================
    @Test
    void obtener_conIdExistente_debeRetornar200() throws Exception {

        when(service.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/promociones/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("VERANO2026"));
    }

    // ===========================================================
    // TEST 5: PUT /promociones/{id} -> 200 OK
    // ===========================================================
    @Test
    void actualizar_conDatosValidos_debeRetornar200() throws Exception {

        PromocionRequestDTO requestDTO = new PromocionRequestDTO();
        requestDTO.setCodigo("VERANO2026");
        requestDTO.setPorcentajeDescuento(20.0);
        requestDTO.setFechaInicio(LocalDate.now().minusDays(1));
        requestDTO.setFechaFin(LocalDate.now().plusDays(60));
        requestDTO.setActivo(true);

        PromocionResponseDTO actualizado = new PromocionResponseDTO(
                1L, "VERANO2026", 20.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(60), true
        );

        when(service.actualizar(anyLong(), any(PromocionRequestDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/promociones/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porcentajeDescuento").value(20.0));
    }

    // ===========================================================
    // TEST 6: DELETE /promociones/{id} -> 204 NO CONTENT
    // ===========================================================
    @Test
    void eliminar_conIdExistente_debeRetornar204() throws Exception {

        mockMvc.perform(delete("/promociones/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).eliminar(1L);
    }

    // ===========================================================
    // TEST 7: GET /promociones/validar/{codigo} -> 200 OK
    // ===========================================================
    @Test
    void validar_conCuponVigente_debeRetornar200() throws Exception {

        when(service.validarCupon(anyString())).thenReturn(responseDTO);

        mockMvc.perform(get("/promociones/validar/{codigo}", "VERANO2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("VERANO2026"));
    }

    // ===========================================================
    // TEST 8: PUT /promociones/aplicar/{codigo} -> 200 OK
    // ===========================================================
    @Test
    void aplicar_conCuponVigente_debeRetornar200() throws Exception {

        PromocionResponseDTO cuponAplicado = new PromocionResponseDTO(
                1L, "VERANO2026", 15.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), false
        );

        when(service.aplicarCupon(anyString())).thenReturn(cuponAplicado);

        mockMvc.perform(put("/promociones/aplicar/{codigo}", "VERANO2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

}

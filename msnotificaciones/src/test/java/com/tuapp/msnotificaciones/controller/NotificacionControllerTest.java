package com.tuapp.msnotificaciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tuapp.msnotificaciones.dto.NotificacionRequestDTO;
import com.tuapp.msnotificaciones.dto.NotificacionResponseDTO;
import com.tuapp.msnotificaciones.service.NotificacionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - NotificacionController
 * ===========================================================
 *
 * Se prueba el controller de forma aislada (standalone MockMvc),
 * mockeando NotificacionService. Así se cubren los 6 endpoints
 * (crear, listar, obtener, listarPorUsuario, marcarComoLeida,
 * eliminar) sin necesitar levantar el contexto completo de
 * Spring ni base de datos.
 */
@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    @Mock
    private NotificacionService service;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private NotificacionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        NotificacionController controller = new NotificacionController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        responseDTO = new NotificacionResponseDTO(
                1L,
                1L,
                "PAGO_APROBADO",
                "Tu pago fue aprobado exitosamente.",
                "PAGOS",
                10L,
                LocalDateTime.now(),
                false
        );
    }

    // ===========================================================
    // TEST 1: POST /notificaciones -> 201 CREATED
    // ===========================================================
    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {

        NotificacionRequestDTO requestDTO = new NotificacionRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setTipo("PAGO_APROBADO");
        requestDTO.setMensaje("Tu pago fue aprobado exitosamente.");
        requestDTO.setOrigen("PAGOS");
        requestDTO.setReferenciaId(10L);

        when(service.crearNotificacion(any(NotificacionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/notificaciones")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("PAGO_APROBADO"));
    }

    // ===========================================================
    // TEST 2: POST /notificaciones con datos inválidos -> 400
    // (activa la validación @Valid del DTO)
    // ===========================================================
    @Test
    void crear_conDatosInvalidos_debeRetornar400() throws Exception {

        NotificacionRequestDTO requestInvalido = new NotificacionRequestDTO();
        requestInvalido.setTipo("");
        requestInvalido.setMensaje("");
        requestInvalido.setOrigen("");

        mockMvc.perform(post("/notificaciones")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    // ===========================================================
    // TEST 3: GET /notificaciones -> 200 OK con listado
    // ===========================================================
    @Test
    void listar_debeRetornar200ConListado() throws Exception {

        when(service.listarNotificaciones()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tipo").value("PAGO_APROBADO"));
    }

    // ===========================================================
    // TEST 4: GET /notificaciones/{id} -> 200 OK
    // ===========================================================
    @Test
    void obtener_conIdExistente_debeRetornar200() throws Exception {

        when(service.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/notificaciones/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ===========================================================
    // TEST 5: GET /notificaciones/usuario/{usuarioId} -> 200 OK
    // ===========================================================
    @Test
    void listarPorUsuario_debeRetornar200ConListado() throws Exception {

        when(service.listarPorUsuario(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/notificaciones/usuario/{usuarioId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(1));
    }

    // ===========================================================
    // TEST 6: PUT /notificaciones/{id}/leida -> 200 OK
    // ===========================================================
    @Test
    void marcarComoLeida_conNotificacionExistente_debeRetornar200() throws Exception {

        NotificacionResponseDTO leida = new NotificacionResponseDTO(
                1L, 1L, "PAGO_APROBADO", "Tu pago fue aprobado exitosamente.",
                "PAGOS", 10L, LocalDateTime.now(), true
        );

        when(service.marcarComoLeida(anyLong())).thenReturn(leida);

        mockMvc.perform(put("/notificaciones/{id}/leida", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leida").value(true));
    }

    // ===========================================================
    // TEST 7: DELETE /notificaciones/{id} -> 204 NO CONTENT
    // ===========================================================
    @Test
    void eliminar_conIdExistente_debeRetornar204() throws Exception {

        mockMvc.perform(delete("/notificaciones/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(service).eliminar(1L);
    }

}

package mspedidos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mspedidos.dto.DetalleRequestDTO;
import mspedidos.dto.DetalleResponseDTO;
import mspedidos.dto.PedidoRequestDTO;
import mspedidos.dto.PedidoResponseDTO;
import mspedidos.service.PedidoService;

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
 * PRUEBAS UNITARIAS - PedidoController
 * ===========================================================
 *
 * Se prueba el controller de forma aislada (standalone MockMvc),
 * mockeando PedidoService. Así se cubren los 6 endpoints sin
 * necesitar levantar el contexto completo de Spring ni base
 * de datos ni comunicación real con msusuarios.
 */
@ExtendWith(MockitoExtension.class)
class PedidoControllerTest {

    @Mock
    private PedidoService service;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();

    private PedidoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        PedidoController controller = new PedidoController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        DetalleResponseDTO detalle = new DetalleResponseDTO(1L, 10L, 2, 5990.0, 11980.0);

        responseDTO = new PedidoResponseDTO(
                1L, 1L, 11980.0, "PENDIENTE", LocalDateTime.now(), List.of(detalle)
        );
    }

    // ===========================================================
    // TEST 1: POST /pedidos -> 201 CREATED
    // ===========================================================
    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {

        DetalleRequestDTO detalle = new DetalleRequestDTO();
        detalle.setProductoId(10L);
        detalle.setCantidad(2);
        detalle.setPrecio(5990.0);

        PedidoRequestDTO requestDTO = new PedidoRequestDTO();
        requestDTO.setUsuarioId(1L);
        requestDTO.setDetalles(List.of(detalle));

        when(service.crear(any(PedidoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/pedidos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.total").value(11980.0));
    }

    // ===========================================================
    // TEST 2: GET /pedidos -> 200 OK con listado
    // ===========================================================
    @Test
    void listar_debeRetornar200ConListado() throws Exception {

        when(service.listarPedidos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // ===========================================================
    // TEST 3: GET /pedidos/{id} -> 200 OK
    // ===========================================================
    @Test
    void obtener_conIdExistente_debeRetornar200() throws Exception {

        when(service.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/pedidos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // ===========================================================
    // TEST 4: GET /pedidos/usuario/{usuarioId} -> 200 OK
    // ===========================================================
    @Test
    void listarPorUsuario_debeRetornar200ConListado() throws Exception {

        when(service.listarPorUsuario(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/pedidos/usuario/{usuarioId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(1));
    }

    // ===========================================================
    // TEST 5: PUT /pedidos/{id}/estado -> 200 OK
    // ===========================================================
    @Test
    void actualizarEstado_conDatosValidos_debeRetornar200() throws Exception {

        PedidoResponseDTO actualizado = new PedidoResponseDTO(
                1L, 1L, 11980.0, "ENVIADO", LocalDateTime.now(), List.of()
        );

        when(service.actualizarEstado(eq(1L), eq("ENVIADO"))).thenReturn(actualizado);

        mockMvc.perform(put("/pedidos/{id}/estado", 1L)
                        .param("estado", "ENVIADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADO"));
    }

    // ===========================================================
    // TEST 6: DELETE /pedidos/{id} -> 204 NO CONTENT
    // ===========================================================
    @Test
    void eliminar_conIdExistente_debeRetornar204() throws Exception {

        mockMvc.perform(delete("/pedidos/{id}", 1L))
                .andExpect(status().isNoContent());
    }

}

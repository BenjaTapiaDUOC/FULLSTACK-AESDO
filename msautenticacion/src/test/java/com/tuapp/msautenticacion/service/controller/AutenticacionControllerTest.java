package com.tuapp.msautenticacion.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuapp.msautenticacion.controller.AutenticacionController;
import com.tuapp.msautenticacion.dto.LoginRequestDTO;
import com.tuapp.msautenticacion.dto.LoginResponseDTO;
import com.tuapp.msautenticacion.dto.RegistroRequestDTO;
import com.tuapp.msautenticacion.dto.RolResponseDTO;
import com.tuapp.msautenticacion.dto.UsuarioResponseDTO;
import com.tuapp.msautenticacion.service.AutenticacionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - AutenticacionController
 * ===========================================================
 *
 * Se prueba el controller de forma aislada (standalone MockMvc),
 * mockeando AutenticacionService. Asi se cubren los 5 endpoints
 * (registro, login, roles, obtenerUsuario, validar) sin
 * necesitar levantar el contexto completo de Spring ni base de
 * datos.
 */
@ExtendWith(MockitoExtension.class)
class AutenticacionControllerTest {

    @Mock
    private AutenticacionService service;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UsuarioResponseDTO usuarioResponseDTO;

    @BeforeEach
    void setUp() {
        AutenticacionController controller = new AutenticacionController(service);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        usuarioResponseDTO = new UsuarioResponseDTO(
                1L, 55L, "Benjamin", "benjamin@gmail.com", "CLIENTE"
        );
    }

    // ===========================================================
    // TEST 1: POST /autenticacion/registro -> 201 CREATED
    // ===========================================================
    @Test
    void registrar_conDatosValidos_debeRetornar201() throws Exception {

        RegistroRequestDTO requestDTO = new RegistroRequestDTO();
        requestDTO.setNombre("Benjamin");
        requestDTO.setEmail("benjamin@gmail.com");
        requestDTO.setPassword("12345678");
        requestDTO.setRol("CLIENTE");

        when(service.registrar(any(RegistroRequestDTO.class))).thenReturn(usuarioResponseDTO);

        mockMvc.perform(post("/autenticacion/registro")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("benjamin@gmail.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    // ===========================================================
    // TEST 2: POST /autenticacion/login -> 200 OK con token
    // ===========================================================
    @Test
    void login_conCredencialesValidas_debeRetornar200ConToken() throws Exception {

        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail("benjamin@gmail.com");
        loginDto.setPassword("12345678");

        LoginResponseDTO loginResponse = new LoginResponseDTO(
                "token.jwt.simulado", "Bearer", 3600000L, usuarioResponseDTO
        );

        when(service.login(any(LoginRequestDTO.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/autenticacion/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token.jwt.simulado"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.usuario.email").value("benjamin@gmail.com"));
    }

    // ===========================================================
    // TEST 3: GET /autenticacion/roles -> 200 OK con listado
    // ===========================================================
    @Test
    void listarRoles_debeRetornar200ConListado() throws Exception {

        when(service.listarRoles()).thenReturn(
                List.of(new RolResponseDTO(1L, "ADMIN"), new RolResponseDTO(2L, "CLIENTE"))
        );

        mockMvc.perform(get("/autenticacion/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("ADMIN"))
                .andExpect(jsonPath("$[1].nombre").value("CLIENTE"));
    }

    // ===========================================================
    // TEST 4: GET /autenticacion/usuarios/{id} -> 200 OK
    // ===========================================================
    @Test
    void obtenerUsuario_conIdExistente_debeRetornar200() throws Exception {

        when(service.obtenerPorId(1L)).thenReturn(usuarioResponseDTO);

        mockMvc.perform(get("/autenticacion/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Benjamin"));
    }

    // ===========================================================
    // TEST 5: GET /autenticacion/validar -> 200 OK con los datos
    // del token decodificado.
    // ===========================================================
    @Test
    void validar_conTokenValido_debeRetornar200() throws Exception {

        when(service.validarToken(anyString())).thenReturn(
                Map.of("valido", true, "email", "benjamin@gmail.com", "rol", "CLIENTE")
        );

        mockMvc.perform(get("/autenticacion/validar")
                        .header("Authorization", "Bearer token.jwt.simulado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true))
                .andExpect(jsonPath("$.email").value("benjamin@gmail.com"));
    }

}

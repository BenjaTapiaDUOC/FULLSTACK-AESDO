package com.tuapp.msautenticacion.service.service;

import com.tuapp.msautenticacion.dto.LoginRequestDTO;
import com.tuapp.msautenticacion.dto.LoginResponseDTO;
import com.tuapp.msautenticacion.dto.RegistroRequestDTO;
import com.tuapp.msautenticacion.dto.RolResponseDTO;
import com.tuapp.msautenticacion.dto.UsuarioMsResponseDTO;
import com.tuapp.msautenticacion.dto.UsuarioResponseDTO;
import com.tuapp.msautenticacion.exception.BadRequestException;
import com.tuapp.msautenticacion.exception.CredencialesInvalidasException;
import com.tuapp.msautenticacion.exception.TokenInvalidoException;
import com.tuapp.msautenticacion.exception.UsuarioNotFoundException;
import com.tuapp.msautenticacion.model.Rol;
import com.tuapp.msautenticacion.model.Usuario;
import com.tuapp.msautenticacion.repository.RolRepository;
import com.tuapp.msautenticacion.repository.UsuarioRepository;
import com.tuapp.msautenticacion.security.JwtUtil;

import com.tuapp.msautenticacion.service.AutenticacionService;
import com.tuapp.msautenticacion.service.UsuarioMsClient;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - AutenticacionService
 * ===========================================================
 *
 * Este servicio tiene 4 dependencias externas, todas mockeadas:
 *
 * - usuarioRepository: credenciales locales
 * - rolRepository:      validacion del rol enviado
 * - usuarioMsClient:    comunicacion con msusuarios (WebClient
 *                        encapsulado en un @Component aparte,
 *                        por lo que aqui se mockea directo sin
 *                        necesidad de simular WebClient)
 * - jwtUtil:             generacion/validacion de tokens
 */
@ExtendWith(MockitoExtension.class)
class AutenticacionServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UsuarioMsClient usuarioMsClient;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AutenticacionService autenticacionService;

    private RegistroRequestDTO registroValido;
    private Rol rolCliente;

    @BeforeEach
    void setUp() {
        registroValido = new RegistroRequestDTO();
        registroValido.setNombre("Benjamin");
        registroValido.setEmail("benjamin@gmail.com");
        registroValido.setPassword("12345678");
        registroValido.setRol("CLIENTE");

        rolCliente = new Rol(1L, "CLIENTE");
    }

    // ===========================================================
    // TEST 1: registrar() con un rol que no existe en la tabla
    // roles debe lanzar BadRequestException, sin llamar a
    // msusuarios ni guardar nada localmente.
    // ===========================================================
    @Test
    void registrar_conRolInvalido_debeLanzarBadRequestException() {

        // GIVEN: el rol "CLIENTE" (mayusculas, tal como lo
        // normaliza el servicio) no existe.
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                BadRequestException.class,
                () -> autenticacionService.registrar(registroValido)
        );

        // La validacion de rol debe cortar el flujo ANTES de
        // consultar el correo o llamar a msusuarios.
        verify(usuarioRepository, never()).existsByEmail(anyString());
        verify(usuarioMsClient, never()).crearUsuarioRemoto(anyString(), anyString(), anyString());
    }

    // ===========================================================
    // TEST 2: registrar() con un correo ya registrado localmente
    // debe lanzar BadRequestException, sin llamar a msusuarios.
    // ===========================================================
    @Test
    void registrar_conCorreoDuplicado_debeLanzarBadRequestException() {

        // GIVEN: el rol es valido, pero el correo ya esta
        // registrado en la tabla usuarios_auth.
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolCliente));
        when(usuarioRepository.existsByEmail("benjamin@gmail.com")).thenReturn(true);

        // WHEN + THEN
        assertThrows(
                BadRequestException.class,
                () -> autenticacionService.registrar(registroValido)
        );

        verify(usuarioMsClient, never()).crearUsuarioRemoto(anyString(), anyString(), anyString());
    }

    // ===========================================================
    // TEST 3: registrar() con datos validos debe crear el
    // usuario remoto en msusuarios y guardar la credencial local.
    // ===========================================================
    @Test
    void registrar_conDatosValidos_debeRegistrarUsuarioCorrectamente() {

        // GIVEN
        when(rolRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(rolCliente));
        when(usuarioRepository.existsByEmail("benjamin@gmail.com")).thenReturn(false);

        // msusuarios "responde" con el usuario maestro creado (id 55)
        UsuarioMsResponseDTO usuarioRemoto =
                new UsuarioMsResponseDTO(55L, "Benjamin", "benjamin@gmail.com", "12345678");
        when(usuarioMsClient.crearUsuarioRemoto("Benjamin", "benjamin@gmail.com", "12345678"))
                .thenReturn(usuarioRemoto);

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario u = invocacion.getArgument(0);
            u.setId(1L);
            return u;
        });

        // WHEN
        UsuarioResponseDTO respuesta = autenticacionService.registrar(registroValido);

        // THEN
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals(55L, respuesta.getUsuarioId());
        assertEquals("CLIENTE", respuesta.getRol());

        verify(usuarioMsClient, times(1))
                .crearUsuarioRemoto("Benjamin", "benjamin@gmail.com", "12345678");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    // ===========================================================
    // TEST 4: login() con credenciales validas debe generar
    // y devolver el token JWT.
    // ===========================================================
    @Test
    void login_conCredencialesValidas_debeRetornarToken() {

        // GIVEN
        Usuario usuarioGuardado = new Usuario(
                1L, 55L, "Benjamin", "benjamin@gmail.com", "12345678", rolCliente);

        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail("benjamin@gmail.com");
        loginDto.setPassword("12345678");

        when(usuarioRepository.findByEmail("benjamin@gmail.com"))
                .thenReturn(Optional.of(usuarioGuardado));
        when(jwtUtil.generarToken(usuarioGuardado)).thenReturn("token.jwt.simulado");
        when(jwtUtil.getExpiracionMs()).thenReturn(3600000L);

        // WHEN
        LoginResponseDTO respuesta = autenticacionService.login(loginDto);

        // THEN
        assertNotNull(respuesta);
        assertEquals("token.jwt.simulado", respuesta.getToken());
        assertEquals(3600000L, respuesta.getExpiraEnMs());
        assertEquals("benjamin@gmail.com", respuesta.getUsuario().getEmail());
    }

    // ===========================================================
    // TEST 5: login() con contrasena incorrecta debe lanzar
    // CredencialesInvalidasException.
    // ===========================================================
    @Test
    void login_conContrasenaIncorrecta_debeLanzarCredencialesInvalidasException() {

        // GIVEN: el usuario existe, pero la contrasena no coincide.
        Usuario usuarioGuardado = new Usuario(
                1L, 55L, "Benjamin", "benjamin@gmail.com", "claveCorrecta", rolCliente);

        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail("benjamin@gmail.com");
        loginDto.setPassword("claveIncorrecta");

        when(usuarioRepository.findByEmail("benjamin@gmail.com"))
                .thenReturn(Optional.of(usuarioGuardado));

        // WHEN + THEN
        assertThrows(
                CredencialesInvalidasException.class,
                () -> autenticacionService.login(loginDto)
        );

        // No deberia intentar generar un token si el login fallo.
        verify(jwtUtil, never()).generarToken(any());
    }

    // ===========================================================
    // TEST 6: login() con correo no registrado debe lanzar
    // CredencialesInvalidasException (mismo mensaje generico
    // que una contrasena incorrecta, por seguridad).
    // ===========================================================
    @Test
    void login_conCorreoNoRegistrado_debeLanzarCredencialesInvalidasException() {

        LoginRequestDTO loginDto = new LoginRequestDTO();
        loginDto.setEmail("noexiste@gmail.com");
        loginDto.setPassword("12345678");

        when(usuarioRepository.findByEmail("noexiste@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                CredencialesInvalidasException.class,
                () -> autenticacionService.login(loginDto)
        );

        verify(jwtUtil, never()).generarToken(any());
    }

    // ===========================================================
    // TEST 7: listarRoles() debe retornar todos los roles
    // mapeados a RolResponseDTO.
    // ===========================================================
    @Test
    void listarRoles_debeRetornarListadoDeRoles() {

        Rol rolAdmin = new Rol(2L, "ADMIN");

        when(rolRepository.findAll()).thenReturn(List.of(rolCliente, rolAdmin));

        List<RolResponseDTO> roles = autenticacionService.listarRoles();

        assertEquals(2, roles.size());
        assertEquals("CLIENTE", roles.get(0).getNombre());
        assertEquals("ADMIN", roles.get(1).getNombre());
    }

    // ===========================================================
    // TEST 8: obtenerPorId() con un ID existente debe retornar
    // el UsuarioResponseDTO correspondiente.
    // ===========================================================
    @Test
    void obtenerPorId_conIdExistente_debeRetornarUsuario() {

        Usuario usuarioGuardado = new Usuario(
                1L, 55L, "Benjamin", "benjamin@gmail.com", "12345678", rolCliente);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioGuardado));

        UsuarioResponseDTO respuesta = autenticacionService.obtenerPorId(1L);

        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("benjamin@gmail.com", respuesta.getEmail());
        assertEquals("CLIENTE", respuesta.getRol());
    }

    // ===========================================================
    // TEST 9: obtenerPorId() con un ID inexistente debe lanzar
    // UsuarioNotFoundException.
    // ===========================================================
    @Test
    void obtenerPorId_conIdInexistente_debeLanzarUsuarioNotFoundException() {

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                UsuarioNotFoundException.class,
                () -> autenticacionService.obtenerPorId(99L)
        );
    }

    // ===========================================================
    // TEST 10: validarToken() sin header Authorization (nulo)
    // debe lanzar TokenInvalidoException, sin llegar a
    // consultar a JwtUtil.
    // ===========================================================
    @Test
    void validarToken_conHeaderNulo_debeLanzarTokenInvalidoException() {

        assertThrows(
                TokenInvalidoException.class,
                () -> autenticacionService.validarToken(null)
        );

        verify(jwtUtil, never()).validarToken(anyString());
    }

    // ===========================================================
    // TEST 11: validarToken() con header en blanco debe lanzar
    // TokenInvalidoException.
    // ===========================================================
    @Test
    void validarToken_conHeaderEnBlanco_debeLanzarTokenInvalidoException() {

        assertThrows(
                TokenInvalidoException.class,
                () -> autenticacionService.validarToken("   ")
        );

        verify(jwtUtil, never()).validarToken(anyString());
    }

    // ===========================================================
    // TEST 12: validarToken() con un token valido (prefijo
    // "Bearer ") debe retornar el mapa con los datos del claim,
    // quitando correctamente el prefijo antes de delegar a
    // JwtUtil.
    // ===========================================================
    @Test
    void validarToken_conTokenValidoYPrefijoBearer_debeRetornarDatos() {

        Claims claims = mock(Claims.class);
        when(claims.get("id")).thenReturn(1);
        when(claims.get("usuarioId")).thenReturn(55);
        when(claims.get("nombre")).thenReturn("Benjamin");
        when(claims.getSubject()).thenReturn("benjamin@gmail.com");
        when(claims.get("rol")).thenReturn("CLIENTE");
        when(claims.getExpiration()).thenReturn(new java.util.Date());

        when(jwtUtil.validarToken("token.jwt.simulado")).thenReturn(claims);

        Map<String, Object> resultado =
                autenticacionService.validarToken("Bearer token.jwt.simulado");

        assertNotNull(resultado);
        assertEquals(true, resultado.get("valido"));
        assertEquals("benjamin@gmail.com", resultado.get("email"));
        assertEquals("CLIENTE", resultado.get("rol"));

        verify(jwtUtil, times(1)).validarToken("token.jwt.simulado");
    }

    // ===========================================================
    // TEST 13: validarToken() con un token sin el prefijo
    // "Bearer " tambien debe funcionar, enviando el token
    // completo tal cual a JwtUtil.
    // ===========================================================
    @Test
    void validarToken_conTokenSinPrefijoBearer_debeDelegarTokenCompleto() {

        Claims claims = mock(Claims.class);
        when(claims.get("id")).thenReturn(1);
        when(claims.get("usuarioId")).thenReturn(55);
        when(claims.get("nombre")).thenReturn("Benjamin");
        when(claims.getSubject()).thenReturn("benjamin@gmail.com");
        when(claims.get("rol")).thenReturn("CLIENTE");
        when(claims.getExpiration()).thenReturn(new java.util.Date());

        when(jwtUtil.validarToken("token.sin.prefijo")).thenReturn(claims);

        Map<String, Object> resultado =
                autenticacionService.validarToken("token.sin.prefijo");

        assertNotNull(resultado);
        verify(jwtUtil, times(1)).validarToken("token.sin.prefijo");
    }
}

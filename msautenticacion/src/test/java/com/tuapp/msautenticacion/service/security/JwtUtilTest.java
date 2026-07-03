package com.tuapp.msautenticacion.service.security;

import com.tuapp.msautenticacion.exception.TokenInvalidoException;
import com.tuapp.msautenticacion.model.Rol;
import com.tuapp.msautenticacion.model.Usuario;

import com.tuapp.msautenticacion.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - JwtUtil
 * ===========================================================
 *
 * Como "secret" y "expiracionMs" se inyectan con @Value desde
 * application.yml, aqui se configuran manualmente con
 * ReflectionTestUtils para poder instanciar JwtUtil fuera del
 * contexto de Spring.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Llave de al menos 32 caracteres, requerida por HS256.
        ReflectionTestUtils.setField(jwtUtil, "secret", "clave-secreta-de-pruebas-unitarias-1234567890");
        ReflectionTestUtils.setField(jwtUtil, "expiracionMs", 3600000L);

        Rol rolCliente = new Rol(1L, "CLIENTE");
        usuario = new Usuario(1L, 55L, "Benjamin", "benjamin@gmail.com", "12345678", rolCliente);
    }

    // ===========================================================
    // TEST 1: generarToken() debe retornar un token JWT no vacio
    // y con formato valido (3 segmentos separados por ".").
    // ===========================================================
    @Test
    void generarToken_debeRetornarTokenValido() {

        String token = jwtUtil.generarToken(usuario);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);
    }

    // ===========================================================
    // TEST 2: un token generado por generarToken() debe poder
    // ser validado por validarToken(), recuperando los mismos
    // datos del usuario (subject, id, usuarioId, nombre, rol).
    // ===========================================================
    @Test
    void validarToken_conTokenGeneradoPorElMismoUtil_debeRetornarClaimsCorrectos() {

        String token = jwtUtil.generarToken(usuario);

        Claims claims = jwtUtil.validarToken(token);

        assertEquals("benjamin@gmail.com", claims.getSubject());
        assertEquals(1, ((Number) claims.get("id")).intValue());
        assertEquals(55, ((Number) claims.get("usuarioId")).intValue());
        assertEquals("Benjamin", claims.get("nombre"));
        assertEquals("CLIENTE", claims.get("rol"));
        assertNotNull(claims.getExpiration());
    }

    // ===========================================================
    // TEST 3: validarToken() con un token mal formado debe
    // lanzar TokenInvalidoException.
    // ===========================================================
    @Test
    void validarToken_conTokenMalFormado_debeLanzarTokenInvalidoException() {

        assertThrows(
                TokenInvalidoException.class,
                () -> jwtUtil.validarToken("esto-no-es-un-token-valido")
        );
    }

    // ===========================================================
    // TEST 4: validarToken() con un token firmado con otra
    // llave debe lanzar TokenInvalidoException (firma invalida).
    // ===========================================================
    @Test
    void validarToken_conFirmaInvalida_debeLanzarTokenInvalidoException() {

        String token = jwtUtil.generarToken(usuario);

        JwtUtil otroJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(otroJwtUtil, "secret", "otra-clave-secreta-completamente-diferente-987654");
        ReflectionTestUtils.setField(otroJwtUtil, "expiracionMs", 3600000L);

        assertThrows(
                TokenInvalidoException.class,
                () -> otroJwtUtil.validarToken(token)
        );
    }

    // ===========================================================
    // TEST 5: getExpiracionMs() debe retornar el valor
    // configurado.
    // ===========================================================
    @Test
    void getExpiracionMs_debeRetornarValorConfigurado() {

        assertEquals(3600000L, jwtUtil.getExpiracionMs());
    }

}

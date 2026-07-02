package com.tuapp.msautenticacion.security;

import com.tuapp.msautenticacion.exception.TokenInvalidoException;
import com.tuapp.msautenticacion.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * ===========================================================
 * UTILIDAD JWT
 * ===========================================================
 *
 * Esta clase se encarga de:
 *
 * 1. Generar el token JWT durante el login, incluyendo
 *    el rol del usuario dentro de los claims.
 * 2. Validar un token JWT recibido (endpoint /validar).
 *
 * El secreto y el tiempo de expiración se configuran en
 * application.properties (jwt.secret / jwt.expiracion-ms).
 */

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiracion-ms}")
    private long expiracionMs;

    /**
     * Construye la llave de firma a partir del secreto
     * configurado.
     */
    private SecretKey obtenerLlave() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * ===========================================================
     * GENERAR TOKEN
     * ===========================================================
     *
     * Genera un JWT firmado que incluye:
     *
     * - subject: email del usuario
     * - id: id local de autenticación
     * - usuarioId: id real en msusuarios
     * - nombre
     * - rol
     */
    public String generarToken(Usuario usuario) {

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiracionMs);

        logger.info("Generando token JWT para el usuario {} con rol {}",
                usuario.getEmail(), usuario.getRol().getNombre());

        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("usuarioId", usuario.getUsuarioId())
                .claim("nombre", usuario.getNombre())
                .claim("rol", usuario.getRol().getNombre())
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(obtenerLlave(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * ===========================================================
     * VALIDAR TOKEN
     * ===========================================================
     *
     * Verifica la firma y la vigencia del token.
     *
     * Si el token es inválido, está mal formado o expiró,
     * se lanza TokenInvalidoException, la cual es capturada
     * por el GlobalExceptionHandler (HTTP 401).
     */
    public Claims validarToken(String token) {
        try {

            return Jwts.parserBuilder()
                    .setSigningKey(obtenerLlave())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (Exception ex) {

            logger.warn("Token JWT inválido o expirado: {}", ex.getMessage());

            throw new TokenInvalidoException("El token es inválido o ha expirado.");

        }
    }

    /**
     * Tiempo de expiración configurado, en milisegundos.
     * Se utiliza para informarlo en la respuesta del login.
     */
    public long getExpiracionMs() {
        return expiracionMs;
    }

}

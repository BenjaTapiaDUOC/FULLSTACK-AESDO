package com.tuapp.msautenticacion.service;

import com.tuapp.msautenticacion.dto.*;
import com.tuapp.msautenticacion.exception.BadRequestException;
import com.tuapp.msautenticacion.exception.CredencialesInvalidasException;
import com.tuapp.msautenticacion.exception.TokenInvalidoException;
import com.tuapp.msautenticacion.exception.UsuarioNotFoundException;
import com.tuapp.msautenticacion.model.Rol;
import com.tuapp.msautenticacion.model.Usuario;
import com.tuapp.msautenticacion.repository.RolRepository;
import com.tuapp.msautenticacion.repository.UsuarioRepository;
import com.tuapp.msautenticacion.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ===========================================================
 * SERVICIO DE AUTENTICACIÓN
 * ===========================================================
 *
 * Aquí se implementa toda la lógica de negocio del
 * microservicio.
 *
 * El Controller solamente llama a estos métodos.
 *
 * Responsabilidades:
 *
 * 1. Registrar un usuario nuevo (validando el rol, creando
 *    el usuario maestro en msusuarios y guardando la
 *    credencial local).
 * 2. Autenticar (login) validando credenciales y generando
 *    el JWT con el rol incluido.
 * 3. Listar los roles disponibles.
 * 4. Validar un token JWT recibido.
 * 5. Consultar un usuario de autenticación por ID.
 */

@Service
public class AutenticacionService {

    private static final Logger logger = LoggerFactory.getLogger(AutenticacionService.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMsClient usuarioMsClient;
    private final JwtUtil jwtUtil;

    public AutenticacionService(UsuarioRepository usuarioRepository,
                                 RolRepository rolRepository,
                                 UsuarioMsClient usuarioMsClient,
                                 JwtUtil jwtUtil) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioMsClient = usuarioMsClient;
        this.jwtUtil = jwtUtil;
    }

    /**
     * ===========================================================
     * REGISTRAR USUARIO
     * ===========================================================
     *
     * Este método:
     *
     * 1. Valida que el rol enviado exista (ADMIN, CLIENTE,
     *    REPARTIDOR). Si no existe, se rechaza con 400.
     * 2. Verifica que el correo no esté registrado localmente.
     * 3. Crea el usuario maestro en msusuarios mediante
     *    WebClient (comunicación entre microservicios).
     * 4. Guarda localmente la credencial + rol, referenciando
     *    el ID real entregado por msusuarios.
     */
    public UsuarioResponseDTO registrar(RegistroRequestDTO dto) {

        logger.info("Intentando registrar usuario con correo: {}", dto.getEmail());

        String nombreRol = dto.getRol().trim().toUpperCase();

        // Validación de rol inválido
        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> {

                    logger.warn("Intento de registro con rol inválido: {}", dto.getRol());

                    return new BadRequestException(
                            "Rol inválido. Debe ser uno de: ADMIN, CLIENTE, REPARTIDOR.");

                });

        // Validación para evitar correos duplicados localmente
        if (usuarioRepository.existsByEmail(dto.getEmail())) {

            logger.warn("Ya existe un usuario de autenticación con el correo {}", dto.getEmail());

            throw new BadRequestException(
                    "Ya existe un usuario registrado con ese correo.");

        }

        // Comunicación con msusuarios: se crea el usuario maestro
        UsuarioMsResponseDTO usuarioRemoto =
                usuarioMsClient.crearUsuarioRemoto(dto.getNombre(), dto.getEmail(), dto.getPassword());

        Usuario usuario = new Usuario(
                null,
                usuarioRemoto.getId(),
                dto.getNombre(),
                dto.getEmail(),
                dto.getPassword(),
                rol
        );

        Usuario guardado = usuarioRepository.save(usuario);

        logger.info("Usuario {} registrado correctamente con rol {} (usuarioId msusuarios: {})",
                guardado.getId(), rol.getNombre(), guardado.getUsuarioId());

        return mapearResponse(guardado);

    }

    /**
     * ===========================================================
     * LOGIN
     * ===========================================================
     *
     * Este método:
     *
     * 1. Busca el usuario de autenticación por correo.
     * 2. Valida que la contraseña coincida.
     * 3. Genera el token JWT, incluyendo el rol asignado.
     *
     * Por seguridad, si el correo no existe o la contraseña
     * no coincide, se entrega el mismo mensaje genérico.
     */
    public LoginResponseDTO login(LoginRequestDTO dto) {

        logger.info("Intento de inicio de sesión para: {}", dto.getEmail());

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {

                    logger.warn("Login fallido: correo no registrado {}", dto.getEmail());

                    return new CredencialesInvalidasException("Correo o contraseña incorrectos.");

                });

        if (!usuario.getPassword().equals(dto.getPassword())) {

            logger.warn("Login fallido: contraseña incorrecta para {}", dto.getEmail());

            throw new CredencialesInvalidasException("Correo o contraseña incorrectos.");

        }

        String token = jwtUtil.generarToken(usuario);

        logger.info("Usuario {} autenticado correctamente. Rol asignado al token: {}",
                usuario.getEmail(), usuario.getRol().getNombre());

        return new LoginResponseDTO(
                token,
                "Bearer",
                jwtUtil.getExpiracionMs(),
                mapearResponse(usuario)
        );

    }

    /**
     * ===========================================================
     * LISTAR ROLES
     * ===========================================================
     *
     * Devuelve todos los roles disponibles en el sistema.
     */
    public List<RolResponseDTO> listarRoles() {

        logger.info("Consultando listado de roles disponibles.");

        return rolRepository.findAll()
                .stream()
                .map(rol -> new RolResponseDTO(rol.getId(), rol.getNombre()))
                .collect(Collectors.toList());

    }

    /**
     * ===========================================================
     * OBTENER USUARIO DE AUTENTICACIÓN POR ID
     * ===========================================================
     *
     * Busca un registro de autenticación utilizando su
     * identificador local.
     */
    public UsuarioResponseDTO obtenerPorId(Long id) {

        logger.info("Buscando usuario de autenticación con ID {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {

                    logger.warn("Usuario de autenticación {} no encontrado.", id);

                    return new UsuarioNotFoundException("Usuario no encontrado.");

                });

        return mapearResponse(usuario);

    }

    /**
     * ===========================================================
     * VALIDAR TOKEN
     * ===========================================================
     *
     * Verifica un token JWT recibido en el header
     * Authorization (formato "Bearer {token}") y devuelve
     * los datos contenidos en él.
     */
    public Map<String, Object> validarToken(String headerAuthorization) {

        logger.info("Validando token JWT recibido.");

        if (headerAuthorization == null || headerAuthorization.isBlank()) {

            throw new TokenInvalidoException(
                    "Debe enviar el token en el header Authorization.");

        }

        String token = headerAuthorization.startsWith("Bearer ")
                ? headerAuthorization.substring(7)
                : headerAuthorization;

        Claims claims = jwtUtil.validarToken(token);

        Map<String, Object> resultado = new HashMap<>();

        resultado.put("valido", true);
        resultado.put("id", claims.get("id"));
        resultado.put("usuarioId", claims.get("usuarioId"));
        resultado.put("nombre", claims.get("nombre"));
        resultado.put("email", claims.getSubject());
        resultado.put("rol", claims.get("rol"));
        resultado.put("expira", claims.getExpiration());

        return resultado;

    }

    /**
     * Convierte la entidad Usuario en su DTO de respuesta,
     * evitando exponer directamente la entidad.
     */
    private UsuarioResponseDTO mapearResponse(Usuario usuario) {

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getUsuarioId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol().getNombre()
        );

    }

}

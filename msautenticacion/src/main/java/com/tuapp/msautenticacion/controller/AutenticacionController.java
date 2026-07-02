package com.tuapp.msautenticacion.controller;

import com.tuapp.msautenticacion.dto.LoginRequestDTO;
import com.tuapp.msautenticacion.dto.LoginResponseDTO;
import com.tuapp.msautenticacion.dto.RegistroRequestDTO;
import com.tuapp.msautenticacion.dto.RolResponseDTO;
import com.tuapp.msautenticacion.dto.UsuarioResponseDTO;
import com.tuapp.msautenticacion.service.AutenticacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ===========================================================
 * CONTROLADOR DE AUTENTICACIÓN
 * ===========================================================
 *
 * Este controlador expone los servicios REST del microservicio
 * de autenticación.
 *
 * Todas las peticiones realizadas desde Postman llegan primero
 * a esta clase, la cual se encarga de recibir la solicitud,
 * validar los datos y delegar la lógica de negocio al
 * AutenticacionService.
 *
 * Base URL del microservicio:
 *
 * http://localhost:8090/autenticacion
 *
 * Endpoints disponibles:
 *
 * POST    /autenticacion/registro
 * POST    /autenticacion/login
 * GET     /autenticacion/roles
 * GET     /autenticacion/usuarios/{id}
 * GET     /autenticacion/validar
 *
 * Todos los endpoints devuelven respuestas HTTP utilizando
 * ResponseEntity y pueden probarse fácilmente desde Postman.
 *
 * Documentación interactiva (Swagger UI):
 * http://localhost:8090/swagger-ui/index.html
 */

@RestController
@RequestMapping("/autenticacion")
@Tag(name = "Autenticación", description = "Registro, login y validación de tokens JWT de los usuarios de la plataforma.")
public class AutenticacionController {

    private final AutenticacionService service;

    public AutenticacionController(AutenticacionService service) {
        this.service = service;
    }

    /**
     * ===========================================================
     * REGISTRAR USUARIO
     * ===========================================================
     */

    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Valida el rol, crea el usuario \"maestro\" en msusuarios (vía WebClient) y guarda localmente la credencial y el rol para el login."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "usuarioId": 1,
                              "nombre": "Benjamin",
                              "email": "benjamin@gmail.com",
                              "rol": "CLIENTE"
                            }
                            """)
            )),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, rol inexistente o correo ya registrado", content = @Content)
    })
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registrar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario a registrar", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "nombre": "Benjamin",
                              "email": "benjamin@gmail.com",
                              "password": "12345678",
                              "rol": "CLIENTE"
                            }
                            """)))
            @Valid @RequestBody RegistroRequestDTO dto) {

        UsuarioResponseDTO usuario = service.registrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);

    }

    /**
     * ===========================================================
     * LOGIN
     * ===========================================================
     */

    @Operation(
            summary = "Iniciar sesión",
            description = "Valida las credenciales del usuario y, de ser correctas, retorna un token JWT junto con su tiempo de expiración e información básica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, incluye el token JWT", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                              "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiZW5qYW1pbkBnbWFpbC5jb20i...",
                              "tipo": "Bearer",
                              "expiraEnMs": 3600000,
                              "usuario": {
                                "id": 1,
                                "usuarioId": 1,
                                "nombre": "Benjamin",
                                "email": "benjamin@gmail.com",
                                "rol": "CLIENTE"
                              }
                            }
                            """)
            )),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales del usuario", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "email": "benjamin@gmail.com",
                              "password": "12345678"
                            }
                            """)))
            @Valid @RequestBody LoginRequestDTO dto) {

        return ResponseEntity.ok(service.login(dto));

    }

    /**
     * ===========================================================
     * LISTAR ROLES
     * ===========================================================
     */

    @Operation(summary = "Listar roles disponibles", description = "Retorna el listado de roles disponibles en el sistema (ADMIN, CLIENTE, REPARTIDOR).")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente", content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples = @ExampleObject(value = """
                    [
                      { "id": 1, "nombre": "ADMIN" },
                      { "id": 2, "nombre": "CLIENTE" },
                      { "id": 3, "nombre": "REPARTIDOR" }
                    ]
                    """)
    ))
    @GetMapping("/roles")
    public ResponseEntity<List<RolResponseDTO>> listarRoles() {

        return ResponseEntity.ok(service.listarRoles());

    }

    /**
     * ===========================================================
     * OBTENER USUARIO DE AUTENTICACIÓN POR ID
     * ===========================================================
     */

    @Operation(summary = "Obtener usuario de autenticación por ID", description = "Busca y retorna la información de autenticación (rol incluido) de un usuario según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content)
    })
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuario(
            @Parameter(description = "Identificador del registro de autenticación", example = "1") @PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    /**
     * ===========================================================
     * VALIDAR TOKEN
     * ===========================================================
     */

    @Operation(
            summary = "Validar token JWT",
            description = "Valida el token JWT enviado en el header Authorization (formato \"Bearer {token}\")."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token válido"),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado", content = @Content)
    })
    @GetMapping("/validar")
    public ResponseEntity<Map<String, Object>> validar(
            @Parameter(description = "Header de autorización, formato: Bearer {token}", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        return ResponseEntity.ok(service.validarToken(authorization));

    }

}

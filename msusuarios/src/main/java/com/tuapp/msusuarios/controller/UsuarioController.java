package com.tuapp.msusuarios.controller;

import com.tuapp.msusuarios.dto.UsuarioRequestDTO;
import com.tuapp.msusuarios.dto.UsuarioResponseDTO;
import com.tuapp.msusuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===========================================================
 * CONTROLADOR DE USUARIOS
 * ===========================================================
 *
 * Este controlador expone los servicios REST del microservicio
 * de usuarios.
 *
 * Todas las peticiones realizadas desde Postman llegan primero
 * a esta clase, la cual se encarga de recibir la solicitud,
 * validar los datos y delegar la lógica de negocio al
 * UsuarioService.
 *
 * Base URL del microservicio:
 *
 * http://localhost:8081/usuarios
 *
 * Endpoints disponibles:
 *
 * POST    /usuarios
 * GET     /usuarios
 * GET     /usuarios/{id}
 * PUT     /usuarios/{id}
 * DELETE  /usuarios/{id}
 *
 * Todos los endpoints devuelven respuestas HTTP utilizando
 * ResponseEntity y pueden probarse fácilmente desde Postman.
 *
 * Documentación interactiva (Swagger UI):
 * http://localhost:8081/swagger-ui/index.html
 */

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestión de los usuarios de la plataforma (alta, consulta, actualización y eliminación).")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    /**
     * ===========================================================
     * CREAR USUARIO
     * ===========================================================
     */

    @Operation(
            summary = "Crear un nuevo usuario",
            description = "Registra un nuevo usuario en el sistema. El correo debe ser único y la contraseña debe tener al menos 8 caracteres."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "nombre": "Benjamin",
                              "email": "benjamin@gmail.com",
                              "password": "12345678"
                            }
                            """)
            )),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o correo ya registrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del usuario a registrar",
                    required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "nombre": "Benjamin",
                              "email": "benjamin@gmail.com",
                              "password": "12345678"
                            }
                            """))
            )
            @Valid @RequestBody UsuarioRequestDTO dto) {

        UsuarioResponseDTO usuario = service.crearUsuario(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    /**
     * ===========================================================
     * LISTAR USUARIOS
     * ===========================================================
     */

    @Operation(
            summary = "Listar todos los usuarios",
            description = "Retorna el listado completo de usuarios registrados en la plataforma."
    )
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarUsuarios());

    }

    /**
     * ===========================================================
     * OBTENER USUARIO POR ID
     * ===========================================================
     */

    @Operation(
            summary = "Obtener un usuario por ID",
            description = "Busca y retorna un usuario específico según su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtener(
            @Parameter(description = "Identificador del usuario", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    /**
     * ===========================================================
     * ACTUALIZAR USUARIO
     * ===========================================================
     */

    @Operation(
            summary = "Actualizar un usuario existente",
            description = "Actualiza el nombre, correo y/o contraseña de un usuario existente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @Parameter(description = "Identificador del usuario", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del usuario",
                    required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "nombre": "Benjamin",
                              "email": "nuevo@gmail.com",
                              "password": "12345678"
                            }
                            """))
            )
            @Valid @RequestBody UsuarioRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));

    }

    /**
     * ===========================================================
     * ELIMINAR USUARIO
     * ===========================================================
     */

    @Operation(
            summary = "Eliminar un usuario",
            description = "Elimina de forma permanente un usuario según su identificador."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El usuario no existe", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del usuario", example = "1")
            @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();

    }

}

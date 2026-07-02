package com.tuapp.msrestaurantes.controller;

import com.tuapp.msrestaurantes.dto.EstadoRestauranteDTO;
import com.tuapp.msrestaurantes.dto.RestauranteRequestDTO;
import com.tuapp.msrestaurantes.dto.RestauranteResponseDTO;
import com.tuapp.msrestaurantes.service.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
 * CONTROLADOR DE RESTAURANTES
 * ===========================================================
 *
 * Base URL del microservicio:
 * http://localhost:8086/restaurantes
 *
 * Endpoints disponibles:
 * POST    /restaurantes
 * GET     /restaurantes
 * GET     /restaurantes/activos
 * GET     /restaurantes/{id}
 * PUT     /restaurantes/{id}
 * PATCH   /restaurantes/{id}/estado
 * DELETE  /restaurantes/{id}
 *
 * Documentación interactiva (Swagger UI):
 * http://localhost:8086/swagger-ui/index.html
 */

@RestController
@RequestMapping("/restaurantes")
@Tag(name = "Restaurantes", description = "Gestión de restaurantes y su estado (activo/inactivo).")
public class RestauranteController {

    private final RestauranteService service;

    public RestauranteController(RestauranteService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un restaurante", description = "Registra un nuevo restaurante en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante creado exitosamente", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "nombre": "La Trattoria",
                              "direccion": "Av. Siempre Viva 123",
                              "categoria": "Italiana",
                              "horario": "09:00 - 22:00",
                              "activo": true
                            }
                            """)
            )),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del restaurante a crear", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "nombre": "La Trattoria",
                              "direccion": "Av. Siempre Viva 123",
                              "categoria": "Italiana",
                              "horario": "09:00 - 22:00",
                              "activo": true
                            }
                            """)))
            @Valid @RequestBody RestauranteRequestDTO dto) {

        RestauranteResponseDTO restaurante = service.crearRestaurante(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(restaurante);
    }

    @Operation(summary = "Listar restaurantes", description = "Retorna el listado completo de restaurantes registrados.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<RestauranteResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarRestaurantes());

    }

    @Operation(summary = "Listar restaurantes activos", description = "Retorna únicamente los restaurantes que se encuentran activos.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping("/activos")
    public ResponseEntity<List<RestauranteResponseDTO>> listarActivos() {

        return ResponseEntity.ok(service.listarActivos());

    }

    @Operation(summary = "Obtener un restaurante por ID", description = "Busca y retorna un restaurante específico según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
            @ApiResponse(responseCode = "404", description = "El restaurante no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> obtener(
            @Parameter(description = "Identificador del restaurante", example = "1") @PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    @Operation(summary = "Actualizar un restaurante", description = "Actualiza los datos de un restaurante existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El restaurante no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> actualizar(
            @Parameter(description = "Identificador del restaurante", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del restaurante", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "nombre": "La Trattoria",
                              "direccion": "Av. Nueva 456",
                              "categoria": "Italiana",
                              "horario": "10:00 - 23:00",
                              "activo": true
                            }
                            """)))
            @Valid @RequestBody RestauranteRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));

    }

    @Operation(summary = "Cambiar el estado del restaurante", description = "Activa o desactiva un restaurante sin necesidad de enviar todos sus datos nuevamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El restaurante no existe", content = @Content)
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<RestauranteResponseDTO> cambiarEstado(
            @Parameter(description = "Identificador del restaurante", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo estado del restaurante", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "activo": false
                            }
                            """)))
            @Valid @RequestBody EstadoRestauranteDTO dto) {

        return ResponseEntity.ok(service.cambiarEstado(id, dto.getActivo()));

    }

    @Operation(summary = "Eliminar un restaurante", description = "Elimina de forma permanente un restaurante según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Restaurante eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El restaurante no existe", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del restaurante", example = "1") @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();

    }

}

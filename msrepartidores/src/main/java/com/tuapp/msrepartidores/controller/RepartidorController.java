package com.tuapp.msrepartidores.controller;

import com.tuapp.msrepartidores.dto.CambioEstadoRequestDTO;
import com.tuapp.msrepartidores.dto.RepartidorRequestDTO;
import com.tuapp.msrepartidores.dto.RepartidorResponseDTO;
import com.tuapp.msrepartidores.service.RepartidorService;
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
 * CONTROLADOR DE REPARTIDORES
 * ===========================================================
 *
 * Base URL del microservicio:
 * http://localhost:8088/repartidores
 *
 * Endpoints disponibles:
 * POST    /repartidores
 * GET     /repartidores
 * GET     /repartidores/disponibles
 * GET     /repartidores/{id}
 * PUT     /repartidores/{id}
 * PATCH   /repartidores/{id}/estado
 * DELETE  /repartidores/{id}
 *
 * Documentación interactiva (Swagger UI):
 * http://localhost:8088/swagger-ui/index.html
 */

@RestController
@RequestMapping("/repartidores")
@Tag(name = "Repartidores", description = "Gestión de los repartidores y su disponibilidad.")
public class RepartidorController {

    private final RepartidorService service;

    public RepartidorController(RepartidorService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un repartidor", description = "Registra un nuevo repartidor. Se crea automáticamente en estado DISPONIBLE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Repartidor creado exitosamente", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "nombre": "Cristobal Soto",
                              "vehiculo": "Moto",
                              "estado": "DISPONIBLE"
                            }
                            """)
            )),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<RepartidorResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del repartidor a crear", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "nombre": "Cristobal Soto",
                              "vehiculo": "Moto"
                            }
                            """)))
            @Valid @RequestBody RepartidorRequestDTO dto) {

        RepartidorResponseDTO repartidor = service.crearRepartidor(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(repartidor);
    }

    @Operation(summary = "Listar repartidores", description = "Retorna el listado completo de repartidores registrados.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<RepartidorResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarRepartidores());

    }

    @Operation(
            summary = "Listar repartidores disponibles",
            description = "Retorna únicamente los repartidores en estado DISPONIBLE. Pensado para ser consumido por msdelivery."
    )
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping("/disponibles")
    public ResponseEntity<List<RepartidorResponseDTO>> listarDisponibles() {

        return ResponseEntity.ok(service.listarDisponibles());

    }

    @Operation(summary = "Obtener un repartidor por ID", description = "Busca y retorna un repartidor específico según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Repartidor encontrado"),
            @ApiResponse(responseCode = "404", description = "El repartidor no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RepartidorResponseDTO> obtener(
            @Parameter(description = "Identificador del repartidor", example = "1") @PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    @Operation(
            summary = "Actualizar un repartidor",
            description = "Actualiza el nombre y/o vehículo de un repartidor. No modifica su estado; para ello usar PATCH /repartidores/{id}/estado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Repartidor actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El repartidor no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<RepartidorResponseDTO> actualizar(
            @Parameter(description = "Identificador del repartidor", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del repartidor", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "nombre": "Cristobal Soto",
                              "vehiculo": "Bicicleta"
                            }
                            """)))
            @Valid @RequestBody RepartidorRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));

    }

    @Operation(
            summary = "Cambiar el estado del repartidor",
            description = "Actualiza el estado del repartidor. Valores permitidos: DISPONIBLE, EN_RUTA, INACTIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "nombre": "Cristobal Soto",
                              "vehiculo": "Moto",
                              "estado": "EN_RUTA"
                            }
                            """)
            )),
            @ApiResponse(responseCode = "404", description = "El repartidor no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "Estado no válido", content = @Content)
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<RepartidorResponseDTO> cambiarEstado(
            @Parameter(description = "Identificador del repartidor", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo estado del repartidor", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "estado": "EN_RUTA"
                            }
                            """)))
            @Valid @RequestBody CambioEstadoRequestDTO dto) {

        return ResponseEntity.ok(service.cambiarEstado(id, dto));

    }

    @Operation(
            summary = "Eliminar un repartidor",
            description = "Elimina de forma permanente un repartidor según su identificador. No se puede eliminar si se encuentra EN_RUTA."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Repartidor eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El repartidor no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "El repartidor se encuentra EN_RUTA", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del repartidor", example = "1") @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();

    }

}

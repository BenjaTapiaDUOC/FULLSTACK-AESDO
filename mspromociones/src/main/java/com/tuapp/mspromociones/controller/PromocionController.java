package com.tuapp.mspromociones.controller;

import com.tuapp.mspromociones.dto.PromocionRequestDTO;
import com.tuapp.mspromociones.dto.PromocionResponseDTO;
import com.tuapp.mspromociones.service.PromocionService;
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
 * CONTROLADOR DE PROMOCIONES
 * ===========================================================
 *
 * Base URL del microservicio:
 * http://localhost:8087/promociones
 *
 * Endpoints disponibles:
 * POST    /promociones
 * GET     /promociones
 * GET     /promociones/{id}
 * PUT     /promociones/{id}
 * DELETE  /promociones/{id}
 * GET     /promociones/validar/{codigo}
 * PUT     /promociones/aplicar/{codigo}
 *
 * Documentación interactiva (Swagger UI):
 * http://localhost:8087/swagger-ui/index.html
 */

@RestController
@RequestMapping("/promociones")
@Tag(name = "Promociones", description = "Gestión y validación de cupones de descuento.")
public class PromocionController {

    private final PromocionService service;

    public PromocionController(PromocionService service) {
        this.service = service;
    }

    @Operation(summary = "Crear una promoción", description = "Registra un nuevo cupón de descuento.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Promoción creada exitosamente", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "codigo": "VERANO2026",
                              "porcentajeDescuento": 15,
                              "fechaInicio": "2026-01-01",
                              "fechaFin": "2026-03-31",
                              "activo": true
                            }
                            """)
            )),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PromocionResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la promoción a crear", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "codigo": "VERANO2026",
                              "porcentajeDescuento": 15,
                              "fechaInicio": "2026-01-01",
                              "fechaFin": "2026-03-31"
                            }
                            """)))
            @Valid @RequestBody PromocionRequestDTO dto) {

        PromocionResponseDTO promocion = service.crearPromocion(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(promocion);
    }

    @Operation(summary = "Listar promociones", description = "Retorna el listado completo de promociones registradas.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<PromocionResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarPromociones());

    }

    @Operation(summary = "Obtener una promoción por ID", description = "Busca y retorna una promoción específica según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción encontrada"),
            @ApiResponse(responseCode = "404", description = "La promoción no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PromocionResponseDTO> obtener(
            @Parameter(description = "Identificador de la promoción", example = "1") @PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    @Operation(summary = "Actualizar una promoción", description = "Actualiza los datos de una promoción existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "La promoción no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "El código pertenece a otra promoción", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PromocionResponseDTO> actualizar(
            @Parameter(description = "Identificador de la promoción", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos de la promoción", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "codigo": "VERANO2026",
                              "porcentajeDescuento": 20,
                              "fechaInicio": "2026-01-01",
                              "fechaFin": "2026-04-30",
                              "activo": true
                            }
                            """)))
            @Valid @RequestBody PromocionRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));

    }

    @Operation(summary = "Eliminar una promoción", description = "Elimina de forma permanente una promoción según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Promoción eliminada exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "La promoción no existe", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador de la promoción", example = "1") @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();

    }

    @Operation(
            summary = "Validar un cupón",
            description = "Verifica si un cupón puede ser aplicado a un pedido, sin modificar su estado. Utilizado por mspedidos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupón válido"),
            @ApiResponse(responseCode = "404", description = "El cupón no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "El cupón está vencido, ya usado o aún no vigente", content = @Content)
    })
    @GetMapping("/validar/{codigo}")
    public ResponseEntity<PromocionResponseDTO> validar(
            @Parameter(description = "Código del cupón", example = "VERANO2026") @PathVariable String codigo) {

        return ResponseEntity.ok(service.validarCupon(codigo));

    }

    @Operation(
            summary = "Aplicar un cupón",
            description = "Valida el cupón y lo marca como utilizado (activo = false), de manera que no pueda volver a aplicarse en otro pedido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cupón aplicado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El cupón no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "El cupón está vencido, ya usado o aún no vigente", content = @Content)
    })
    @PutMapping("/aplicar/{codigo}")
    public ResponseEntity<PromocionResponseDTO> aplicar(
            @Parameter(description = "Código del cupón", example = "VERANO2026") @PathVariable String codigo) {

        return ResponseEntity.ok(service.aplicarCupon(codigo));

    }

}

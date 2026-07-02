package com.tuapp.mspagos.controller;

import com.tuapp.mspagos.dto.PagoRequestDTO;
import com.tuapp.mspagos.dto.PagoResponseDTO;
import com.tuapp.mspagos.service.PagoService;
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
 * CONTROLADOR DE PAGOS
 * ===========================================================
 *
 * Base URL del microservicio:
 * http://localhost:8084/pagos
 *
 * Endpoints disponibles:
 * POST    /pagos
 * GET     /pagos
 * GET     /pagos/{id}
 * PUT     /pagos/{id}
 * DELETE  /pagos/{id}
 *
 * Documentación interactiva (Swagger UI):
 * http://localhost:8084/swagger-ui/index.html
 */

@RestController
@RequestMapping("/pagos")
@Tag(name = "Pagos", description = "Procesamiento y gestión de los pagos asociados a los pedidos.")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @Operation(summary = "Crear / procesar un pago", description = "Registra y procesa un nuevo pago asociado a un pedido existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago creado exitosamente", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "pedidoId": 1,
                              "monto": 15000,
                              "metodoPago": "TARJETA",
                              "estado": "APROBADO",
                              "fechaPago": "2026-07-02T10:15:30"
                            }
                            """)
            )),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o pedido inexistente", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PagoResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del pago a procesar", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "pedidoId": 1,
                              "monto": 15000,
                              "metodoPago": "TARJETA"
                            }
                            """)))
            @Valid @RequestBody PagoRequestDTO dto) {

        PagoResponseDTO pago = service.crearPago(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(pago);
    }

    @Operation(summary = "Listar pagos", description = "Retorna el listado completo de pagos registrados.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarPagos());

    }

    @Operation(summary = "Obtener un pago por ID", description = "Busca y retorna un pago específico según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado"),
            @ApiResponse(responseCode = "404", description = "El pago no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> obtener(
            @Parameter(description = "Identificador del pago", example = "1") @PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    @Operation(summary = "Actualizar un pago", description = "Actualiza el monto y/o método de un pago existente. No permite modificar pagos ya aprobados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El pago no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "El pago ya fue aprobado y no puede modificarse", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> actualizar(
            @Parameter(description = "Identificador del pago", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del pago", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "pedidoId": 1,
                              "monto": 20000,
                              "metodoPago": "EFECTIVO"
                            }
                            """)))
            @Valid @RequestBody PagoRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));

    }

    @Operation(summary = "Eliminar un pago", description = "Elimina de forma permanente un pago según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pago eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El pago no existe", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del pago", example = "1") @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();

    }

}

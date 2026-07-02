package mspedidos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mspedidos.dto.PedidoRequestDTO;
import mspedidos.dto.PedidoResponseDTO;
import mspedidos.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===========================================================
 * CONTROLADOR DE PEDIDOS
 * ===========================================================
 *
 * Al crear un pedido, este microservicio se comunica con
 * msusuarios (WebClient) para validar que el usuario exista.
 *
 * Base URL del microservicio:
 * http://localhost:8083/pedidos
 *
 * Endpoints disponibles:
 * POST    /pedidos
 * GET     /pedidos
 * GET     /pedidos/{id}
 * GET     /pedidos/usuario/{usuarioId}
 * PUT     /pedidos/{id}/estado
 * DELETE  /pedidos/{id}
 *
 * Documentación interactiva (Swagger UI):
 * http://localhost:8083/swagger-ui/index.html
 */

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Gestión de pedidos y sus detalles.")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @Operation(
            summary = "Crear un pedido",
            description = "Registra un nuevo pedido con uno o más detalles. El usuarioId debe existir previamente en msusuarios (http://localhost:8081/usuarios); si no existe, se responde HTTP 400."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "usuarioId": 1,
                              "total": 24970.0,
                              "estado": "PENDIENTE",
                              "fechaCreacion": "2026-07-02T10:00:00",
                              "detalles": [
                                { "id": 1, "productoId": 10, "cantidad": 2, "precio": 5990.0, "subtotal": 11980.0 },
                                { "id": 2, "productoId": 15, "cantidad": 1, "precio": 12990.0, "subtotal": 12990.0 }
                              ]
                            }
                            """)
            )),
            @ApiResponse(responseCode = "400", description = "El usuarioId no existe o los datos son inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del pedido a crear", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "usuarioId": 1,
                              "detalles": [
                                { "productoId": 10, "cantidad": 2, "precio": 5990.0 },
                                { "productoId": 15, "cantidad": 1, "precio": 12990.0 }
                              ]
                            }
                            """)))
            @Valid @RequestBody PedidoRequestDTO dto) {

        PedidoResponseDTO pedido = service.crear(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @Operation(summary = "Listar pedidos", description = "Retorna el listado completo de pedidos registrados.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarPedidos());

    }

    @Operation(summary = "Obtener un pedido por ID", description = "Busca y retorna un pedido específico según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "El pedido no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtener(
            @Parameter(description = "Identificador del pedido", example = "1") @PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    @Operation(summary = "Listar pedidos de un usuario", description = "Retorna todos los pedidos realizados por un usuario específico.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorUsuario(
            @Parameter(description = "Identificador del usuario", example = "1") @PathVariable Long usuarioId) {

        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));

    }

    @Operation(
            summary = "Actualizar el estado del pedido",
            description = "Actualiza el estado de un pedido. Valores permitidos: PENDIENTE, EN_PROCESO, ENVIADO, ENTREGADO, CANCELADO."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El pedido no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "El estado enviado no es válido", content = @Content)
    })
    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstado(
            @Parameter(description = "Identificador del pedido", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado del pedido", example = "ENVIADO",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(allowableValues = {"PENDIENTE", "EN_PROCESO", "ENVIADO", "ENTREGADO", "CANCELADO"}))
            @RequestParam String estado) {

        return ResponseEntity.ok(service.actualizarEstado(id, estado));

    }

    @Operation(summary = "Eliminar un pedido", description = "Elimina de forma permanente un pedido según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El pedido no existe", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del pedido", example = "1") @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();

    }

}

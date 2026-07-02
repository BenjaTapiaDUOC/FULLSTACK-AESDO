package com.example.msdelivery.controller;

import com.example.msdelivery.dto.DeliveryRequestDTO;
import com.example.msdelivery.dto.DeliveryResponseDTO;
import com.example.msdelivery.service.DeliveryService;
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
 * CONTROLADOR DE DELIVERY
 * ===========================================================
 *
 * Al crear o actualizar un delivery, este microservicio se
 * comunica con mspedidos (WebClient) para validar que el
 * pedido exista.
 *
 * Base URL del microservicio:
 * http://localhost:8085/delivery
 *
 * Endpoints disponibles:
 * POST    /delivery
 * GET     /delivery
 * GET     /delivery/{id}
 * PUT     /delivery/{id}
 * DELETE  /delivery/{id}
 *
 * Documentación interactiva (Swagger UI):
 * http://localhost:8085/swagger-ui/index.html
 */

@RestController
@RequestMapping("/delivery")
@Tag(name = "Delivery", description = "Gestión de los envíos (deliveries) asociados a los pedidos.")
public class DeliveryController {

    private final DeliveryService service;

    public DeliveryController(DeliveryService service) {
        this.service = service;
    }

    @Operation(
            summary = "Crear un delivery",
            description = "Registra un nuevo delivery. El pedidoId debe existir previamente en mspedidos (http://localhost:8083/pedidos); si no existe, se responde HTTP 400. Si mspedidos no responde, se retorna HTTP 503."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Delivery creado exitosamente", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "pedidoId": 1,
                              "direccionEntrega": "Av. Siempre Viva 123",
                              "repartidor": "Juan Pérez",
                              "estado": "PENDIENTE"
                            }
                            """)
            )),
            @ApiResponse(responseCode = "400", description = "El pedidoId no existe en mspedidos", content = @Content),
            @ApiResponse(responseCode = "503", description = "mspedidos no responde", content = @Content)
    })
    @PostMapping
    public ResponseEntity<DeliveryResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del delivery a crear", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "pedidoId": 1,
                              "direccionEntrega": "Av. Siempre Viva 123",
                              "repartidor": "Juan Pérez",
                              "estado": "PENDIENTE"
                            }
                            """)))
            @Valid @RequestBody DeliveryRequestDTO dto) {

        DeliveryResponseDTO delivery = service.crearDelivery(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(delivery);
    }

    @Operation(summary = "Listar deliveries", description = "Retorna el listado completo de deliveries registrados.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<DeliveryResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarDeliveries());

    }

    @Operation(summary = "Obtener un delivery por ID", description = "Busca y retorna un delivery específico según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delivery encontrado"),
            @ApiResponse(responseCode = "404", description = "El delivery no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> obtener(
            @Parameter(description = "Identificador del delivery", example = "1") @PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    @Operation(summary = "Actualizar un delivery", description = "Actualiza la dirección, repartidor y/o estado de un delivery existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delivery actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El delivery no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> actualizar(
            @Parameter(description = "Identificador del delivery", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del delivery", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "pedidoId": 1,
                              "direccionEntrega": "Av. Nueva 456",
                              "repartidor": "Pedro Soto",
                              "estado": "EN_CAMINO"
                            }
                            """)))
            @Valid @RequestBody DeliveryRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));

    }

    @Operation(summary = "Eliminar un delivery", description = "Elimina de forma permanente un delivery según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delivery eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El delivery no existe", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del delivery", example = "1") @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();

    }

}

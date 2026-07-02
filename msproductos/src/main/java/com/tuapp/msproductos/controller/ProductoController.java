package com.tuapp.msproductos.controller;

import com.tuapp.msproductos.dto.ProductoRequestDTO;
import com.tuapp.msproductos.dto.ProductoResponseDTO;
import com.tuapp.msproductos.service.ProductoService;
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
 * CONTROLADOR DE PRODUCTOS
 * ===========================================================
 *
 * Este controlador expone los servicios REST del microservicio
 * de productos.
 *
 * Base URL del microservicio:
 * http://localhost:8082/productos
 *
 * Endpoints disponibles:
 * POST    /productos
 * GET     /productos
 * GET     /productos/{id}
 * PUT     /productos/{id}
 * DELETE  /productos/{id}
 *
 * Documentación interactiva (Swagger UI):
 * http://localhost:8082/swagger-ui/index.html
 */

@RestController
@RequestMapping("/productos")
@Tag(name = "Productos", description = "Gestión del catálogo de productos.")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un producto", description = "Registra un nuevo producto en el catálogo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "nombre": "Pizza Napolitana",
                              "precio": 8990,
                              "categoria": "Comida rápida"
                            }
                            """)
            )),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto a crear", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "nombre": "Pizza Napolitana",
                              "precio": 8990,
                              "categoria": "Comida rápida"
                            }
                            """)))
            @Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO producto = service.crearProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    @Operation(summary = "Listar productos", description = "Retorna el listado completo de productos del catálogo.")
    @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarProductos());
    }

    @Operation(summary = "Obtener un producto por ID", description = "Busca y retorna un producto específico según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "El producto no existe", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtener(
            @Parameter(description = "Identificador del producto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @Operation(summary = "Actualizar un producto", description = "Actualiza el nombre, precio y/o categoría de un producto existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "El producto no existe", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(
            @Parameter(description = "Identificador del producto", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del producto", required = true,
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "nombre": "Pizza Napolitana Familiar",
                              "precio": 12990,
                              "categoria": "Comida rápida"
                            }
                            """)))
            @Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar un producto", description = "Elimina de forma permanente un producto según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "El producto no existe", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del producto", example = "1") @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}

package com.tuapp.msproductos.controller;

import com.tuapp.msproductos.dto.ProductoRequestDTO;
import com.tuapp.msproductos.dto.ProductoResponseDTO;
import com.tuapp.msproductos.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
 * Todas las peticiones realizadas desde Postman llegan primero
 * a esta clase, la cual se encarga de recibir la solicitud,
 * validar los datos y delegar la lógica de negocio al
 * ProductoService.
 *
 * Base URL del microservicio:
 *
 * http://localhost:8082/productos
 *
 * Endpoints disponibles:
 *
 * POST    /productos
 * GET     /productos
 * GET     /productos/{id}
 * PUT     /productos/{id}
 * DELETE  /productos/{id}
 *
 * Todos los endpoints devuelven respuestas HTTP utilizando
 * ResponseEntity y pueden probarse fácilmente desde Postman.
 */

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    /**
     * ===========================================================
     * CREAR PRODUCTO
     * ===========================================================
     *
     * Metodo HTTP: POST
     * URL: http://localhost:8082/productos
     *
     * POSTMAN
     * Body -> raw -> JSON
     *
     * {
     *   "nombre": "Pizza Napolitana",
     *   "precio": 8990,
     *   "categoria": "Comida rápida"
     * }
     *
     * Respuesta: HTTP 201 CREATED
     */
    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO dto) {
        ProductoResponseDTO producto = service.crearProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }

    /**
     * ===========================================================
     * LISTAR PRODUCTOS
     * ===========================================================
     *
     * Metodo: GET
     * URL: http://localhost:8082/productos
     *
     * Respuesta: HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarProductos());
    }

    /**
     * ===========================================================
     * OBTENER PRODUCTO POR ID
     * ===========================================================
     *
     * Metodo: GET
     * URL: http://localhost:8082/productos/1
     *
     * Respuesta: HTTP 200 OK
     * Si no existe: HTTP 404 NOT FOUND
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    /**
     * ===========================================================
     * ACTUALIZAR PRODUCTO
     * ===========================================================
     *
     * Metodo: PUT
     * URL: http://localhost:8082/productos/1
     *
     * POSTMAN
     * Body -> JSON
     *
     * {
     *   "nombre": "Pizza Napolitana Familiar",
     *   "precio": 12990,
     *   "categoria": "Comida rápida"
     * }
     *
     * Respuesta: HTTP 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    /**
     * ===========================================================
     * ELIMINAR PRODUCTO
     * ===========================================================
     *
     * Metodo: DELETE
     * URL: http://localhost:8082/productos/1
     *
     * Respuesta: HTTP 204 NO CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
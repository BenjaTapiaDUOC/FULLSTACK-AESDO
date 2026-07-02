package com.tuapp.msrestaurantes.controller;

import com.tuapp.msrestaurantes.dto.EstadoRestauranteDTO;
import com.tuapp.msrestaurantes.dto.RestauranteRequestDTO;
import com.tuapp.msrestaurantes.dto.RestauranteResponseDTO;
import com.tuapp.msrestaurantes.service.RestauranteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===========================================================
 * CONTROLADOR DE RESTAURANTES
 * ===========================================================
 *
 * Este controlador expone los servicios REST del microservicio
 * de restaurantes.
 *
 * Todas las peticiones realizadas desde Postman llegan primero
 * a esta clase, la cual se encarga de recibir la solicitud,
 * validar los datos y delegar la lógica de negocio al
 * RestauranteService.
 *
 * Base URL del microservicio:
 *
 * http://localhost:8086/restaurantes
 *
 * Endpoints disponibles:
 *
 * POST    /restaurantes
 * GET     /restaurantes
 * GET     /restaurantes/activos
 * GET     /restaurantes/{id}
 * PUT     /restaurantes/{id}
 * PATCH   /restaurantes/{id}/estado
 * DELETE  /restaurantes/{id}
 *
 * Todos los endpoints devuelven respuestas HTTP utilizando
 * ResponseEntity y pueden probarse fácilmente desde Postman.
 */

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

    private final RestauranteService service;

    public RestauranteController(RestauranteService service) {
        this.service = service;
    }

    /**
     * ===========================================================
     * CREAR RESTAURANTE
     * ===========================================================
     *
     * Metodo HTTP:
     * POST
     *
     * URL:
     * http://localhost:8086/restaurantes
     *
     * POSTMAN
     *
     * Body -> raw -> JSON
     *
     * {
     *   "nombre":"La Trattoria",
     *   "direccion":"Av. Siempre Viva 123",
     *   "categoria":"Italiana",
     *   "horario":"09:00 - 22:00",
     *   "activo":true
     * }
     *
     * Respuesta:
     *
     * HTTP 201 CREATED
     */

    @PostMapping
    public ResponseEntity<RestauranteResponseDTO> crear(@Valid @RequestBody RestauranteRequestDTO dto) {

        RestauranteResponseDTO restaurante = service.crearRestaurante(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(restaurante);
    }

    /**
     * ===========================================================
     * LISTAR RESTAURANTES
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     * http://localhost:8086/restaurantes
     *
     * POSTMAN
     *
     * Seleccionar GET
     *
     * Presionar SEND
     *
     * Respuesta:
     *
     * HTTP 200 OK
     */

    @GetMapping
    public ResponseEntity<List<RestauranteResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarRestaurantes());

    }

    /**
     * ===========================================================
     * LISTAR RESTAURANTES ACTIVOS
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     * http://localhost:8086/restaurantes/activos
     *
     * Respuesta:
     *
     * HTTP 200 OK
     */

    @GetMapping("/activos")
    public ResponseEntity<List<RestauranteResponseDTO>> listarActivos() {

        return ResponseEntity.ok(service.listarActivos());

    }

    /**
     * ===========================================================
     * OBTENER RESTAURANTE POR ID
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     *
     * http://localhost:8086/restaurantes/1
     *
     * Respuesta:
     *
     * HTTP 200 OK
     *
     * Si no existe:
     *
     * HTTP 404 NOT FOUND
     */

    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> obtener(@PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    /**
     * ===========================================================
     * ACTUALIZAR RESTAURANTE
     * ===========================================================
     *
     * Metodo:
     *
     * PUT
     *
     * URL:
     *
     * http://localhost:8086/restaurantes/1
     *
     * POSTMAN
     *
     * Body -> JSON
     *
     * {
     *   "nombre":"La Trattoria",
     *   "direccion":"Av. Nueva 456",
     *   "categoria":"Italiana",
     *   "horario":"10:00 - 23:00",
     *   "activo":true
     * }
     *
     * Respuesta:
     *
     * HTTP 200 OK
     */

    @PutMapping("/{id}")
    public ResponseEntity<RestauranteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RestauranteRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));

    }

    /**
     * ===========================================================
     * CAMBIAR ESTADO DEL RESTAURANTE
     * ===========================================================
     *
     * Metodo:
     *
     * PATCH
     *
     * URL:
     *
     * http://localhost:8086/restaurantes/1/estado
     *
     * POSTMAN
     *
     * Body -> JSON
     *
     * {
     *   "activo": false
     * }
     *
     * Respuesta:
     *
     * HTTP 200 OK
     */

    @PatchMapping("/{id}/estado")
    public ResponseEntity<RestauranteResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoRestauranteDTO dto) {

        return ResponseEntity.ok(service.cambiarEstado(id, dto.getActivo()));

    }

    /**
     * ===========================================================
     * ELIMINAR RESTAURANTE
     * ===========================================================
     *
     * Metodo:
     *
     * DELETE
     *
     * URL:
     *
     * http://localhost:8086/restaurantes/1
     *
     * POSTMAN
     *
     * Seleccionar DELETE
     *
     * Presionar SEND
     *
     * Respuesta:
     *
     * HTTP 204 NO CONTENT
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();

    }

}

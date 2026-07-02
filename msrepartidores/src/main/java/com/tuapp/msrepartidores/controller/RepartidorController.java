package com.tuapp.msrepartidores.controller;

import com.tuapp.msrepartidores.dto.CambioEstadoRequestDTO;
import com.tuapp.msrepartidores.dto.RepartidorRequestDTO;
import com.tuapp.msrepartidores.dto.RepartidorResponseDTO;
import com.tuapp.msrepartidores.service.RepartidorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===========================================================
 * CONTROLADOR DE REPARTIDORES
 * ===========================================================
 *
 * Este controlador expone los servicios REST del microservicio
 * de repartidores.
 *
 * Todas las peticiones realizadas desde Postman (o desde otro
 * microservicio como msdelivery) llegan primero a esta clase,
 * la cual se encarga de recibir la solicitud, validar los
 * datos y delegar la lógica de negocio al RepartidorService.
 *
 * Base URL del microservicio:
 *
 * http://localhost:8088/repartidores
 *
 * Endpoints disponibles:
 *
 * POST    /repartidores
 * GET     /repartidores
 * GET     /repartidores/disponibles
 * GET     /repartidores/{id}
 * PUT     /repartidores/{id}
 * PATCH   /repartidores/{id}/estado
 * DELETE  /repartidores/{id}
 *
 * Todos los endpoints devuelven respuestas HTTP utilizando
 * ResponseEntity y pueden probarse fácilmente desde Postman.
 */

@RestController
@RequestMapping("/repartidores")
public class RepartidorController {

    private final RepartidorService service;

    public RepartidorController(RepartidorService service) {
        this.service = service;
    }

    /**
     * ===========================================================
     * CREAR REPARTIDOR
     * ===========================================================
     *
     * Método HTTP:
     * POST
     *
     * URL:
     * http://localhost:8088/repartidores
     *
     * POSTMAN
     *
     * Body -> raw -> JSON
     *
     * {
     *   "nombre":"Cristobal Soto",
     *   "vehiculo":"Moto"
     * }
     *
     * Respuesta:
     *
     * HTTP 201 CREATED
     *
     * El repartidor se crea automáticamente en estado
     * DISPONIBLE.
     */

    @PostMapping
    public ResponseEntity<RepartidorResponseDTO> crear(@Valid @RequestBody RepartidorRequestDTO dto) {

        RepartidorResponseDTO repartidor = service.crearRepartidor(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(repartidor);
    }

    /**
     * ===========================================================
     * LISTAR REPARTIDORES
     * ===========================================================
     *
     * Método:
     * GET
     *
     * URL:
     * http://localhost:8088/repartidores
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
    public ResponseEntity<List<RepartidorResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarRepartidores());

    }

    /**
     * ===========================================================
     * LISTAR REPARTIDORES DISPONIBLES
     * ===========================================================
     *
     * Método:
     * GET
     *
     * URL:
     * http://localhost:8088/repartidores/disponibles
     *
     * Respuesta:
     *
     * HTTP 200 OK
     *
     * Retorna únicamente los repartidores en estado
     * DISPONIBLE. Pensado para ser consumido por msdelivery.
     */

    @GetMapping("/disponibles")
    public ResponseEntity<List<RepartidorResponseDTO>> listarDisponibles() {

        return ResponseEntity.ok(service.listarDisponibles());

    }

    /**
     * ===========================================================
     * OBTENER REPARTIDOR POR ID
     * ===========================================================
     *
     * Método:
     * GET
     *
     * URL:
     *
     * http://localhost:8088/repartidores/1
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
    public ResponseEntity<RepartidorResponseDTO> obtener(@PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    /**
     * ===========================================================
     * ACTUALIZAR REPARTIDOR
     * ===========================================================
     *
     * Método:
     *
     * PUT
     *
     * URL:
     *
     * http://localhost:8088/repartidores/1
     *
     * POSTMAN
     *
     * Body -> JSON
     *
     * {
     *   "nombre":"Cristobal Soto",
     *   "vehiculo":"Bicicleta"
     * }
     *
     * Respuesta:
     *
     * HTTP 200 OK
     *
     * NOTA: este endpoint no modifica el estado del
     * repartidor. Para ello utilizar
     * PATCH /repartidores/{id}/estado
     */

    @PutMapping("/{id}")
    public ResponseEntity<RepartidorResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RepartidorRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));

    }

    /**
     * ===========================================================
     * CAMBIAR ESTADO DEL REPARTIDOR
     * ===========================================================
     *
     * Método:
     *
     * PATCH
     *
     * URL:
     *
     * http://localhost:8088/repartidores/1/estado
     *
     * POSTMAN
     *
     * Body -> JSON
     *
     * {
     *   "estado":"EN_RUTA"
     * }
     *
     * Valores permitidos: DISPONIBLE, EN_RUTA, INACTIVO.
     *
     * Respuesta:
     *
     * HTTP 200 OK
     */

    @PatchMapping("/{id}/estado")
    public ResponseEntity<RepartidorResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambioEstadoRequestDTO dto) {

        return ResponseEntity.ok(service.cambiarEstado(id, dto));

    }

    /**
     * ===========================================================
     * ELIMINAR REPARTIDOR
     * ===========================================================
     *
     * Método:
     *
     * DELETE
     *
     * URL:
     *
     * http://localhost:8088/repartidores/1
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
     *
     * Si el repartidor se encuentra EN_RUTA:
     *
     * HTTP 400 BAD REQUEST
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();

    }

}

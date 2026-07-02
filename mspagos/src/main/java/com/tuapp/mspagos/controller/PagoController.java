package com.tuapp.mspagos.controller;

import com.tuapp.mspagos.dto.PagoRequestDTO;
import com.tuapp.mspagos.dto.PagoResponseDTO;
import com.tuapp.mspagos.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===========================================================
 * CONTROLADOR DE PAGOS
 * ===========================================================
 *
 * Este controlador expone los servicios REST del microservicio
 * de pagos.
 *
 * Todas las peticiones realizadas desde Postman llegan primero
 * a esta clase, la cual se encarga de recibir la solicitud,
 * validar los datos y delegar la lógica de negocio al
 * PagoService.
 *
 * Base URL del microservicio:
 *
 * http://localhost:8084/pagos
 *
 * Endpoints disponibles:
 *
 * POST    /pagos
 * GET     /pagos
 * GET     /pagos/{id}
 * PUT     /pagos/{id}
 * DELETE  /pagos/{id}
 *
 * Todos los endpoints devuelven respuestas HTTP utilizando
 * ResponseEntity y pueden probarse fácilmente desde Postman.
 */

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    /**
     * ===========================================================
     * CREAR / PROCESAR PAGO
     * ===========================================================
     *
     * Metodo HTTP:
     * POST
     *
     * URL:
     * http://localhost:8084/pagos
     *
     * POSTMAN
     *
     * Body -> raw -> JSON
     *
     * {
     *   "pedidoId":1,
     *   "monto":15000,
     *   "metodoPago":"TARJETA"
     * }
     *
     * Respuesta:
     *
     * HTTP 201 CREATED
     */

    @PostMapping
    public ResponseEntity<PagoResponseDTO> crear(@Valid @RequestBody PagoRequestDTO dto) {

        PagoResponseDTO pago = service.crearPago(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(pago);
    }

    /**
     * ===========================================================
     * LISTAR PAGOS
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     * http://localhost:8084/pagos
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
    public ResponseEntity<List<PagoResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarPagos());

    }

    /**
     * ===========================================================
     * OBTENER PAGO POR ID
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     *
     * http://localhost:8084/pagos/1
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
    public ResponseEntity<PagoResponseDTO> obtener(@PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    /**
     * ===========================================================
     * ACTUALIZAR PAGO
     * ===========================================================
     *
     * Metodo:
     *
     * PUT
     *
     * URL:
     *
     * http://localhost:8084/pagos/1
     *
     * POSTMAN
     *
     * Body -> JSON
     *
     * {
     *   "pedidoId":1,
     *   "monto":20000,
     *   "metodoPago":"EFECTIVO"
     * }
     *
     * Respuesta:
     *
     * HTTP 200 OK
     *
     * Si el pago ya fue aprobado:
     *
     * HTTP 400 BAD REQUEST
     */

    @PutMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PagoRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));

    }

    /**
     * ===========================================================
     * ELIMINAR PAGO
     * ===========================================================
     *
     * Metodo:
     *
     * DELETE
     *
     * URL:
     *
     * http://localhost:8084/pagos/1
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

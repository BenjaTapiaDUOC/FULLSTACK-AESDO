package com.tuapp.mspromociones.controller;

import com.tuapp.mspromociones.dto.PromocionRequestDTO;
import com.tuapp.mspromociones.dto.PromocionResponseDTO;
import com.tuapp.mspromociones.service.PromocionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===========================================================
 * CONTROLADOR DE PROMOCIONES
 * ===========================================================
 *
 * Este controlador expone los servicios REST del microservicio
 * de promociones.
 *
 * Todas las peticiones realizadas desde Postman (o desde otro
 * microservicio como mspedidos) llegan primero a esta clase,
 * la cual se encarga de recibir la solicitud, validar los
 * datos y delegar la lógica de negocio al PromocionService.
 *
 * Base URL del microservicio:
 *
 * http://localhost:8087/promociones
 *
 * Endpoints disponibles:
 *
 * POST    /promociones
 * GET     /promociones
 * GET     /promociones/{id}
 * PUT     /promociones/{id}
 * DELETE  /promociones/{id}
 * GET     /promociones/validar/{codigo}
 * PUT     /promociones/aplicar/{codigo}
 *
 * Todos los endpoints devuelven respuestas HTTP utilizando
 * ResponseEntity y pueden probarse fácilmente desde Postman.
 */

@RestController
@RequestMapping("/promociones")
public class PromocionController {

    private final PromocionService service;

    public PromocionController(PromocionService service) {
        this.service = service;
    }

    /**
     * ===========================================================
     * CREAR PROMOCION
     * ===========================================================
     *
     * Metodo HTTP:
     * POST
     *
     * URL:
     * http://localhost:8087/promociones
     *
     * POSTMAN
     *
     * Body -> raw -> JSON
     *
     * {
     *   "codigo":"VERANO2026",
     *   "porcentajeDescuento":15,
     *   "fechaInicio":"2026-01-01",
     *   "fechaFin":"2026-03-31"
     * }
     *
     * Respuesta:
     *
     * HTTP 201 CREATED
     */

    @PostMapping
    public ResponseEntity<PromocionResponseDTO> crear(@Valid @RequestBody PromocionRequestDTO dto) {

        PromocionResponseDTO promocion = service.crearPromocion(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(promocion);
    }

    /**
     * ===========================================================
     * LISTAR PROMOCIONES
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     * http://localhost:8087/promociones
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
    public ResponseEntity<List<PromocionResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarPromociones());

    }

    /**
     * ===========================================================
     * OBTENER PROMOCION POR ID
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     *
     * http://localhost:8087/promociones/1
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
    public ResponseEntity<PromocionResponseDTO> obtener(@PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    /**
     * ===========================================================
     * ACTUALIZAR PROMOCION
     * ===========================================================
     *
     * Metodo:
     *
     * PUT
     *
     * URL:
     *
     * http://localhost:8087/promociones/1
     *
     * POSTMAN
     *
     * Body -> JSON
     *
     * {
     *   "codigo":"VERANO2026",
     *   "porcentajeDescuento":20,
     *   "fechaInicio":"2026-01-01",
     *   "fechaFin":"2026-04-30",
     *   "activo":true
     * }
     *
     * Respuesta:
     *
     * HTTP 200 OK
     *
     * Si no existe:
     *
     * HTTP 404 NOT FOUND
     *
     * Si el código pertenece a otra promoción:
     *
     * HTTP 400 BAD REQUEST
     */

    @PutMapping("/{id}")
    public ResponseEntity<PromocionResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PromocionRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));

    }

    /**
     * ===========================================================
     * ELIMINAR PROMOCION
     * ===========================================================
     *
     * Metodo:
     *
     * DELETE
     *
     * URL:
     *
     * http://localhost:8087/promociones/1
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
     * Si no existe:
     *
     * HTTP 404 NOT FOUND
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();

    }

    /**
     * ===========================================================
     * VALIDAR CUPÓN
     * ===========================================================
     *
     * Metodo:
     *
     * GET
     *
     * URL:
     *
     * http://localhost:8087/promociones/validar/VERANO2026
     *
     * Este endpoint es utilizado por mspedidos para verificar
     * si un cupón puede ser aplicado a un pedido, sin
     * modificar su estado.
     *
     * Respuesta:
     *
     * HTTP 200 OK
     *
     * Si el cupón no existe:
     *
     * HTTP 404 NOT FOUND
     *
     * Si el cupón está vencido, ya usado o aún no vigente:
     *
     * HTTP 400 BAD REQUEST
     */

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<PromocionResponseDTO> validar(@PathVariable String codigo) {

        return ResponseEntity.ok(service.validarCupon(codigo));

    }

    /**
     * ===========================================================
     * APLICAR CUPÓN
     * ===========================================================
     *
     * Metodo:
     *
     * PUT
     *
     * URL:
     *
     * http://localhost:8087/promociones/aplicar/VERANO2026
     *
     * Este endpoint valida el cupón y lo marca como
     * utilizado (activo = false), de manera que no pueda
     * volver a aplicarse en otro pedido.
     *
     * Respuesta:
     *
     * HTTP 200 OK
     *
     * Si el cupón no existe:
     *
     * HTTP 404 NOT FOUND
     *
     * Si el cupón está vencido, ya usado o aún no vigente:
     *
     * HTTP 400 BAD REQUEST
     */

    @PutMapping("/aplicar/{codigo}")
    public ResponseEntity<PromocionResponseDTO> aplicar(@PathVariable String codigo) {

        return ResponseEntity.ok(service.aplicarCupon(codigo));

    }

}

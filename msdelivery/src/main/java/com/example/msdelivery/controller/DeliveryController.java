package com.example.msdelivery.controller;

import com.example.msdelivery.dto.DeliveryRequestDTO;
import com.example.msdelivery.dto.DeliveryResponseDTO;
import com.example.msdelivery.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===========================================================
 * CONTROLADOR DE DELIVERY
 * ===========================================================
 *
 * Este controlador expone los servicios REST del microservicio
 * de delivery.
 *
 * Todas las peticiones realizadas desde Postman llegan primero
 * a esta clase, la cual se encarga de recibir la solicitud,
 * validar los datos y delegar la lógica de negocio al
 * DeliveryService.
 *
 * Al crear o actualizar un delivery, este microservicio se
 * comunica con mspedidos (WebClient) para validar que el
 * pedido exista.
 *
 * Base URL del microservicio:
 *
 * http://localhost:8085/delivery
 *
 * Endpoints disponibles:
 *
 * POST    /delivery
 * GET     /delivery
 * GET     /delivery/{id}
 * PUT     /delivery/{id}
 * DELETE  /delivery/{id}
 *
 * Todos los endpoints devuelven respuestas HTTP utilizando
 * ResponseEntity y pueden probarse fácilmente desde Postman.
 */

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    private final DeliveryService service;

    public DeliveryController(DeliveryService service) {
        this.service = service;
    }

    /**
     * ===========================================================
     * CREAR DELIVERY
     * ===========================================================
     *
     * Metodo HTTP:
     * POST
     *
     * URL:
     * http://localhost:8085/delivery
     *
     * POSTMAN
     *
     * Body -> raw -> JSON
     *
     * {
     *   "pedidoId": 1,
     *   "direccionEntrega": "Av. Siempre Viva 123",
     *   "repartidor": "Juan Pérez",
     *   "estado": "PENDIENTE"
     * }
     *
     * Respuesta:
     *
     * HTTP 201 CREATED
     *
     * Nota: el pedidoId debe existir previamente en
     * mspedidos (http://localhost:8083/pedidos), de lo
     * contrario se responde HTTP 400. Si mspedidos no
     * responde, se retorna HTTP 503.
     */

    @PostMapping
    public ResponseEntity<DeliveryResponseDTO> crear(@Valid @RequestBody DeliveryRequestDTO dto) {

        DeliveryResponseDTO delivery = service.crearDelivery(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(delivery);
    }

    /**
     * ===========================================================
     * LISTAR DELIVERIES
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     * http://localhost:8085/delivery
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
    public ResponseEntity<List<DeliveryResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarDeliveries());

    }

    /**
     * ===========================================================
     * OBTENER DELIVERY POR ID
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     *
     * http://localhost:8085/delivery/1
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
    public ResponseEntity<DeliveryResponseDTO> obtener(@PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    /**
     * ===========================================================
     * ACTUALIZAR DELIVERY
     * ===========================================================
     *
     * Metodo:
     *
     * PUT
     *
     * URL:
     *
     * http://localhost:8085/delivery/1
     *
     * POSTMAN
     *
     * Body -> JSON
     *
     * {
     *   "pedidoId": 1,
     *   "direccionEntrega": "Av. Nueva 456",
     *   "repartidor": "Pedro Soto",
     *   "estado": "EN_CAMINO"
     * }
     *
     * Respuesta:
     *
     * HTTP 200 OK
     */

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody DeliveryRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));

    }

    /**
     * ===========================================================
     * ELIMINAR DELIVERY
     * ===========================================================
     *
     * Metodo:
     *
     * DELETE
     *
     * URL:
     *
     * http://localhost:8085/delivery/1
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

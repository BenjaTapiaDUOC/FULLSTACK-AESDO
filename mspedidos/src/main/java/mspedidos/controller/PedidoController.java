package mspedidos.controller;

import jakarta.validation.Valid;
import mspedidos.dto.PedidoRequestDTO;
import mspedidos.dto.PedidoResponseDTO;
import mspedidos.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===========================================================
 * CONTROLADOR DE PEDIDOS
 * ===========================================================
 *
 * Este controlador expone los servicios REST del microservicio
 * de pedidos.
 *
 * Todas las peticiones realizadas desde Postman llegan primero
 * a esta clase, la cual se encarga de recibir la solicitud,
 * validar los datos y delegar la lógica de negocio al
 * PedidoService.
 *
 * Al crear un pedido, este microservicio se comunica con
 * msusuarios (WebClient) para validar que el usuario exista.
 *
 * Base URL del microservicio:
 *
 * http://localhost:8083/pedidos
 *
 * Endpoints disponibles:
 *
 * POST    /pedidos
 * GET     /pedidos
 * GET     /pedidos/{id}
 * GET     /pedidos/usuario/{usuarioId}
 * PUT     /pedidos/{id}/estado
 * DELETE  /pedidos/{id}
 *
 * Todos los endpoints devuelven respuestas HTTP utilizando
 * ResponseEntity y pueden probarse fácilmente desde Postman.
 */

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    /**
     * ===========================================================
     * CREAR PEDIDO
     * ===========================================================
     *
     * Metodo HTTP:
     * POST
     *
     * URL:
     * http://localhost:8083/pedidos
     *
     * POSTMAN
     *
     * Body -> raw -> JSON
     *
     * {
     *   "usuarioId": 1,
     *   "detalles": [
     *     { "productoId": 10, "cantidad": 2, "precio": 5990.0 },
     *     { "productoId": 15, "cantidad": 1, "precio": 12990.0 }
     *   ]
     * }
     *
     * Respuesta:
     *
     * HTTP 201 CREATED
     *
     * Nota: el usuarioId debe existir previamente en
     * msusuarios (http://localhost:8081/usuarios), de lo
     * contrario se responde HTTP 400.
     */

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crear(@Valid @RequestBody PedidoRequestDTO dto) {

        PedidoResponseDTO pedido = service.crear(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    /**
     * ===========================================================
     * LISTAR PEDIDOS
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     * http://localhost:8083/pedidos
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
    public ResponseEntity<List<PedidoResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarPedidos());

    }

    /**
     * ===========================================================
     * OBTENER PEDIDO POR ID
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     *
     * http://localhost:8083/pedidos/1
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
    public ResponseEntity<PedidoResponseDTO> obtener(@PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    /**
     * ===========================================================
     * LISTAR PEDIDOS DE UN USUARIO
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     *
     * http://localhost:8083/pedidos/usuario/1
     *
     * Respuesta:
     *
     * HTTP 200 OK
     */

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {

        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));

    }

    /**
     * ===========================================================
     * ACTUALIZAR ESTADO DEL PEDIDO
     * ===========================================================
     *
     * Metodo:
     *
     * PUT
     *
     * URL:
     *
     * http://localhost:8083/pedidos/1/estado?estado=ENVIADO
     *
     * POSTMAN
     *
     * Params -> Key: estado -> Value: uno de
     * PENDIENTE, EN_PROCESO, ENVIADO, ENTREGADO, CANCELADO
     *
     * Respuesta:
     *
     * HTTP 200 OK
     */

    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {

        return ResponseEntity.ok(service.actualizarEstado(id, estado));

    }

    /**
     * ===========================================================
     * ELIMINAR PEDIDO
     * ===========================================================
     *
     * Metodo:
     *
     * DELETE
     *
     * URL:
     *
     * http://localhost:8083/pedidos/1
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

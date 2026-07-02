package com.tuapp.msusuarios.controller;

import com.tuapp.msusuarios.dto.UsuarioRequestDTO;
import com.tuapp.msusuarios.dto.UsuarioResponseDTO;
import com.tuapp.msusuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ===========================================================
 * CONTROLADOR DE USUARIOS
 * ===========================================================
 *
 * Este controlador expone los servicios REST del microservicio
 * de usuarios.
 *
 * Todas las peticiones realizadas desde Postman llegan primero
 * a esta clase, la cual se encarga de recibir la solicitud,
 * validar los datos y delegar la lógica de negocio al
 * UsuarioService.
 *
 * Base URL del microservicio:
 *
 * http://localhost:8081/usuarios
 *
 * Endpoints disponibles:
 *
 * POST    /usuarios
 * GET     /usuarios
 * GET     /usuarios/{id}
 * PUT     /usuarios/{id}
 * DELETE  /usuarios/{id}
 *
 * Todos los endpoints devuelven respuestas HTTP utilizando
 * ResponseEntity y pueden probarse fácilmente desde Postman.
 */

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    /**
     * ===========================================================
     * CREAR USUARIO
     * ===========================================================
     *
     * Metodo HTTP:
     * POST
     *
     * URL:
     * http://localhost:8081/usuarios
     *
     * POSTMAN
     *
     * Body -> raw -> JSON
     *
     * {
     *   "nombre":"Benjamin",
     *   "email":"benjamin@gmail.com",
     *   "password":"12345678"
     * }
     *
     * Respuesta:
     *
     * HTTP 201 CREATED
     */

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto) {

        UsuarioResponseDTO usuario = service.crearUsuario(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    /**
     * ===========================================================
     * LISTAR USUARIOS
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     * http://localhost:8081/usuarios
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
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {

        return ResponseEntity.ok(service.listarUsuarios());

    }

    /**
     * ===========================================================
     * OBTENER USUARIO POR ID
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     *
     * http://localhost:8081/usuarios/1
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
    public ResponseEntity<UsuarioResponseDTO> obtener(@PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    /**
     * ===========================================================
     * ACTUALIZAR USUARIO
     * ===========================================================
     *
     * Metodo:
     *
     * PUT
     *
     * URL:
     *
     * http://localhost:8081/usuarios/1
     *
     * POSTMAN
     *
     * Body -> JSON
     *
     * {
     *   "nombre":"Benjamin",
     *   "email":"nuevo@gmail.com",
     *   "password":"12345678"
     * }
     *
     * Respuesta:
     *
     * HTTP 200 OK
     */

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {

        return ResponseEntity.ok(service.actualizar(id, dto));

    }

    /**
     * ===========================================================
     * ELIMINAR USUARIO
     * ===========================================================
     *
     * Metodo:
     *
     * DELETE
     *
     * URL:
     *
     * http://localhost:8081/usuarios/1
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
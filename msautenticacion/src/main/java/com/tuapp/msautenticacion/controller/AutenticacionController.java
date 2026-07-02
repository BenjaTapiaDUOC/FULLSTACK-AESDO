package com.tuapp.msautenticacion.controller;

import com.tuapp.msautenticacion.dto.LoginRequestDTO;
import com.tuapp.msautenticacion.dto.LoginResponseDTO;
import com.tuapp.msautenticacion.dto.RegistroRequestDTO;
import com.tuapp.msautenticacion.dto.RolResponseDTO;
import com.tuapp.msautenticacion.dto.UsuarioResponseDTO;
import com.tuapp.msautenticacion.service.AutenticacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ===========================================================
 * CONTROLADOR DE AUTENTICACIÓN
 * ===========================================================
 *
 * Este controlador expone los servicios REST del microservicio
 * de autenticación.
 *
 * Todas las peticiones realizadas desde Postman llegan primero
 * a esta clase, la cual se encarga de recibir la solicitud,
 * validar los datos y delegar la lógica de negocio al
 * AutenticacionService.
 *
 * Base URL del microservicio:
 *
 * http://localhost:8090/autenticacion
 *
 * Endpoints disponibles:
 *
 * POST    /autenticacion/registro
 * POST    /autenticacion/login
 * GET     /autenticacion/roles
 * GET     /autenticacion/usuarios/{id}
 * GET     /autenticacion/validar
 *
 * Todos los endpoints devuelven respuestas HTTP utilizando
 * ResponseEntity y pueden probarse fácilmente desde Postman.
 */

@RestController
@RequestMapping("/autenticacion")
public class AutenticacionController {

    private final AutenticacionService service;

    public AutenticacionController(AutenticacionService service) {
        this.service = service;
    }

    /**
     * ===========================================================
     * REGISTRAR USUARIO
     * ===========================================================
     *
     * Metodo HTTP:
     * POST
     *
     * URL:
     * http://localhost:8090/autenticacion/registro
     *
     * POSTMAN
     *
     * Body -> raw -> JSON
     *
     * {
     *   "nombre":"Benjamin",
     *   "email":"benjamin@gmail.com",
     *   "password":"12345678",
     *   "rol":"CLIENTE"
     * }
     *
     * Respuesta:
     *
     * HTTP 201 CREATED
     */

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO dto) {

        UsuarioResponseDTO usuario = service.registrar(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);

    }

    /**
     * ===========================================================
     * LOGIN
     * ===========================================================
     *
     * Metodo HTTP:
     * POST
     *
     * URL:
     * http://localhost:8090/autenticacion/login
     *
     * POSTMAN
     *
     * Body -> raw -> JSON
     *
     * {
     *   "email":"benjamin@gmail.com",
     *   "password":"12345678"
     * }
     *
     * Respuesta:
     *
     * HTTP 200 OK (incluye el token JWT)
     */

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {

        return ResponseEntity.ok(service.login(dto));

    }

    /**
     * ===========================================================
     * LISTAR ROLES
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     * http://localhost:8090/autenticacion/roles
     *
     * Respuesta:
     *
     * HTTP 200 OK
     */

    @GetMapping("/roles")
    public ResponseEntity<List<RolResponseDTO>> listarRoles() {

        return ResponseEntity.ok(service.listarRoles());

    }

    /**
     * ===========================================================
     * OBTENER USUARIO DE AUTENTICACIÓN POR ID
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     * http://localhost:8090/autenticacion/usuarios/1
     *
     * Respuesta:
     *
     * HTTP 200 OK
     *
     * Si no existe:
     *
     * HTTP 404 NOT FOUND
     */

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuario(@PathVariable Long id) {

        return ResponseEntity.ok(service.obtenerPorId(id));

    }

    /**
     * ===========================================================
     * VALIDAR TOKEN
     * ===========================================================
     *
     * Metodo:
     * GET
     *
     * URL:
     * http://localhost:8090/autenticacion/validar
     *
     * POSTMAN
     *
     * Headers -> Authorization -> Bearer {token}
     *
     * Respuesta:
     *
     * HTTP 200 OK
     *
     * Si el token es inválido o expiró:
     *
     * HTTP 401 UNAUTHORIZED
     */

    @GetMapping("/validar")
    public ResponseEntity<Map<String, Object>> validar(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        return ResponseEntity.ok(service.validarToken(authorization));

    }

}

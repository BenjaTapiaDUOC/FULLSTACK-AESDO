package com.tuapp.msusuarios.service;

import com.tuapp.msusuarios.dto.UsuarioRequestDTO;
import com.tuapp.msusuarios.dto.UsuarioResponseDTO;
import com.tuapp.msusuarios.exception.BadRequestException;
import com.tuapp.msusuarios.exception.UsuarioNotFoundException;
import com.tuapp.msusuarios.model.Usuario;
import com.tuapp.msusuarios.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - UsuarioService
 * ===========================================================
 *
 * Se mockea el UsuarioRepository para no depender de MySQL.
 * UsuarioService no tiene otras dependencias externas
 * (no llama a otro microservicio), por lo que es el caso
 * más simple de testear.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioRequestDTO requestValido;

    @BeforeEach
    void setUp() {
        requestValido = new UsuarioRequestDTO();
        requestValido.setNombre("Benjamin");
        requestValido.setEmail("benjamin@gmail.com");
        requestValido.setPassword("12345678");
    }

    // ===========================================================
    // TEST 1: crearUsuario() con correo nuevo debe guardar
    // correctamente y devolver el DTO con el id asignado.
    // ===========================================================
    @Test
    void crearUsuario_conCorreoNuevo_debeCrearUsuarioCorrectamente() {

        // GIVEN: el correo no existe todavia, y al guardar el
        // repository devuelve el mismo usuario con un id generado.
        when(repository.existsByEmail("benjamin@gmail.com")).thenReturn(false);
        when(repository.save(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario u = invocacion.getArgument(0);
            u.setId(1L);
            return u;
        });

        // WHEN
        UsuarioResponseDTO respuesta = usuarioService.crearUsuario(requestValido);

        // THEN
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("Benjamin", respuesta.getNombre());
        assertEquals("benjamin@gmail.com", respuesta.getEmail());

        verify(repository, times(1)).existsByEmail("benjamin@gmail.com");
        verify(repository, times(1)).save(any(Usuario.class));
    }

    // ===========================================================
    // TEST 2: crearUsuario() con un correo ya registrado debe
    // lanzar BadRequestException y NO debe intentar guardar.
    // ===========================================================
    @Test
    void crearUsuario_conCorreoDuplicado_debeLanzarBadRequestException() {

        // GIVEN: el correo ya existe en la base de datos.
        when(repository.existsByEmail("benjamin@gmail.com")).thenReturn(true);

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> usuarioService.crearUsuario(requestValido)
        );

        assertEquals("Ya existe un usuario registrado con ese correo.", ex.getMessage());

        // La regla de negocio debe cortar el flujo antes de guardar.
        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 3: obtenerPorId() con un id que no existe debe
    // lanzar UsuarioNotFoundException.
    // ===========================================================
    @Test
    void obtenerPorId_conIdInexistente_debeLanzarUsuarioNotFoundException() {

        // GIVEN
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                UsuarioNotFoundException.class,
                () -> usuarioService.obtenerPorId(99L)
        );
    }

    // ===========================================================
    // TEST 4: actualizar() con un correo que ya pertenece a
    // OTRO usuario debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void actualizar_conCorreoYaUsadoPorOtroUsuario_debeLanzarBadRequestException() {

        // GIVEN: el usuario 1 existe, con su correo actual.
        Usuario usuarioExistente = new Usuario(1L, "Benjamin", "benjamin@gmail.com", "12345678");
        when(repository.findById(1L)).thenReturn(Optional.of(usuarioExistente));

        // El DTO intenta cambiar el correo a uno que YA pertenece
        // a otro usuario distinto.
        UsuarioRequestDTO dtoConCorreoAjeno = new UsuarioRequestDTO();
        dtoConCorreoAjeno.setNombre("Benjamin");
        dtoConCorreoAjeno.setEmail("otro@gmail.com");
        dtoConCorreoAjeno.setPassword("12345678");

        when(repository.existsByEmail("otro@gmail.com")).thenReturn(true);

        // WHEN + THEN
        assertThrows(
                BadRequestException.class,
                () -> usuarioService.actualizar(1L, dtoConCorreoAjeno)
        );

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 5: eliminar() con un id que no existe debe lanzar
    // UsuarioNotFoundException y no debe llamar a deleteById().
    // ===========================================================
    @Test
    void eliminar_conIdInexistente_debeLanzarUsuarioNotFoundException() {

        // GIVEN
        when(repository.existsById(50L)).thenReturn(false);

        // WHEN + THEN
        assertThrows(
                UsuarioNotFoundException.class,
                () -> usuarioService.eliminar(50L)
        );

        verify(repository, never()).deleteById(any());
    }
}

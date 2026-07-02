package com.tuapp.msnotificaciones.service;

import com.tuapp.msnotificaciones.client.UsuarioClient;
import com.tuapp.msnotificaciones.dto.NotificacionRequestDTO;
import com.tuapp.msnotificaciones.dto.NotificacionResponseDTO;
import com.tuapp.msnotificaciones.exception.BadRequestException;
import com.tuapp.msnotificaciones.exception.NotificacionNotFoundException;
import com.tuapp.msnotificaciones.model.Notificacion;
import com.tuapp.msnotificaciones.repository.NotificacionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - NotificacionService
 * ===========================================================
 *
 * @ExtendWith(MockitoExtension.class) le dice a JUnit que
 * active Mockito en esta clase, para que las anotaciones
 * @Mock y @InjectMocks funcionen automáticamente.
 *
 * No se levanta el contexto de Spring (no es @SpringBootTest),
 * por lo que estas pruebas son rápidas: no tocan MySQL ni
 * hacen llamadas HTTP reales a msusuarios.
 */
@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    // --- DEPENDENCIAS FALSAS (MOCKS) ---
    @Mock
    private NotificacionRepository repository;

    @Mock
    private UsuarioClient usuarioClient;

    // --- CLASE REAL BAJO PRUEBA ---
    @InjectMocks
    private NotificacionService notificacionService;

    private NotificacionRequestDTO notificacionRequestValida;

    @BeforeEach
    void setUp() {
        // Se ejecuta antes de CADA test. Preparamos un DTO válido
        // reutilizable para no repetir código en cada método.
        notificacionRequestValida = new NotificacionRequestDTO();
        notificacionRequestValida.setUsuarioId(1L);
        notificacionRequestValida.setTipo("PAGO_APROBADO");
        notificacionRequestValida.setMensaje("Tu pago fue aprobado exitosamente.");
        notificacionRequestValida.setOrigen("PAGOS");
        notificacionRequestValida.setReferenciaId(10L);
    }

    // ===========================================================
    // TEST 1: crearNotificacion() con datos válidos debe
    // guardar la notificación correctamente.
    // ===========================================================
    @Test
    void crearNotificacion_conDatosValidos_debeGuardarNotificacion() {

        // GIVEN: el usuario existe, no hay duplicado y el repository
        // devuelve la notificación con un id al guardar.
        when(usuarioClient.existeUsuario(1L)).thenReturn(true);
        when(repository.existsByUsuarioIdAndOrigenAndTipoAndReferenciaId(
                1L, "PAGOS", "PAGO_APROBADO", 10L)).thenReturn(false);
        when(repository.save(any(Notificacion.class))).thenAnswer(invocacion -> {
            Notificacion n = invocacion.getArgument(0);
            n.setId(1L);
            return n;
        });

        // WHEN
        NotificacionResponseDTO respuesta = notificacionService.crearNotificacion(notificacionRequestValida);

        // THEN
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("PAGOS", respuesta.getOrigen());
        assertFalse(respuesta.isLeida());

        verify(usuarioClient, times(1)).existeUsuario(1L);
        verify(repository, times(1)).save(any(Notificacion.class));
    }

    // ===========================================================
    // TEST 2: crearNotificacion() con un origen no permitido debe
    // lanzar BadRequestException y no debe consultar msusuarios.
    // ===========================================================
    @Test
    void crearNotificacion_conOrigenInvalido_debeLanzarBadRequestException() {

        // GIVEN: un origen que no está en la lista permitida
        notificacionRequestValida.setOrigen("MARKETING");

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> notificacionService.crearNotificacion(notificacionRequestValida)
        );

        assertEquals("El origen debe ser uno de los siguientes: PAGOS, PEDIDOS, DELIVERY.", ex.getMessage());

        // La validación de origen corta el flujo antes de llamar a msusuarios
        verify(usuarioClient, never()).existeUsuario(any());
        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 3: crearNotificacion() con un usuario inexistente en
    // msusuarios debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void crearNotificacion_conUsuarioInexistente_debeLanzarBadRequestException() {

        // GIVEN: msusuarios responde que el usuario no existe
        when(usuarioClient.existeUsuario(1L)).thenReturn(false);

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> notificacionService.crearNotificacion(notificacionRequestValida)
        );

        assertEquals("El usuario indicado no existe en msusuarios.", ex.getMessage());

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 4: crearNotificacion() con un evento ya notificado
    // (mismo usuario, origen, tipo y referenciaId) debe lanzar
    // BadRequestException.
    // ===========================================================
    @Test
    void crearNotificacion_conEventoDuplicado_debeLanzarBadRequestException() {

        // GIVEN: el usuario existe pero el evento ya fue notificado antes
        when(usuarioClient.existeUsuario(1L)).thenReturn(true);
        when(repository.existsByUsuarioIdAndOrigenAndTipoAndReferenciaId(
                1L, "PAGOS", "PAGO_APROBADO", 10L)).thenReturn(true);

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> notificacionService.crearNotificacion(notificacionRequestValida)
        );

        assertEquals("Ya existe una notificación registrada para este evento.", ex.getMessage());

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 5: marcarComoLeida() con una notificación ya leída debe
    // lanzar BadRequestException.
    // ===========================================================
    @Test
    void marcarComoLeida_conNotificacionYaLeida_debeLanzarBadRequestException() {

        // GIVEN: una notificación existente que ya fue marcada como leída
        Notificacion notificacion = new Notificacion(
                1L, 1L, "PAGO_APROBADO", "Tu pago fue aprobado.",
                "PAGOS", 10L, LocalDateTime.now(), true
        );

        when(repository.findById(1L)).thenReturn(Optional.of(notificacion));

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> notificacionService.marcarComoLeida(1L)
        );

        assertEquals("La notificación ya fue marcada como leída anteriormente.", ex.getMessage());

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 6: marcarComoLeida() con un id inexistente debe lanzar
    // NotificacionNotFoundException.
    // ===========================================================
    @Test
    void marcarComoLeida_conNotificacionInexistente_debeLanzarNotFoundException() {

        // GIVEN: el repositorio no encuentra la notificación
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                NotificacionNotFoundException.class,
                () -> notificacionService.marcarComoLeida(99L)
        );

        verify(repository, never()).save(any());
    }
}

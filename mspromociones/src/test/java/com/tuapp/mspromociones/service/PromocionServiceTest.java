package com.tuapp.mspromociones.service;

import com.tuapp.mspromociones.dto.PromocionRequestDTO;
import com.tuapp.mspromociones.dto.PromocionResponseDTO;
import com.tuapp.mspromociones.exception.BadRequestException;
import com.tuapp.mspromociones.exception.PromocionNotFoundException;
import com.tuapp.mspromociones.model.Promocion;
import com.tuapp.mspromociones.repository.PromocionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - PromocionService
 * ===========================================================
 *
 * @ExtendWith(MockitoExtension.class) le dice a JUnit que
 * active Mockito en esta clase, para que las anotaciones
 * @Mock y @InjectMocks funcionen automáticamente.
 *
 * No se levanta el contexto de Spring (no es @SpringBootTest),
 * por lo que estas pruebas son rápidas: no tocan MySQL.
 */
@ExtendWith(MockitoExtension.class)
class PromocionServiceTest {

    // --- DEPENDENCIAS FALSAS (MOCKS) ---
    @Mock
    private PromocionRepository repository;

    // --- CLASE REAL BAJO PRUEBA ---
    @InjectMocks
    private PromocionService promocionService;

    private PromocionRequestDTO promocionRequestValida;

    @BeforeEach
    void setUp() {
        // Se ejecuta antes de CADA test. Preparamos un DTO válido
        // reutilizable para no repetir código en cada método.
        promocionRequestValida = new PromocionRequestDTO();
        promocionRequestValida.setCodigo("VERANO2026");
        promocionRequestValida.setPorcentajeDescuento(15.0);
        promocionRequestValida.setFechaInicio(LocalDate.now().minusDays(1));
        promocionRequestValida.setFechaFin(LocalDate.now().plusDays(30));
        promocionRequestValida.setActivo(true);
    }

    // ===========================================================
    // TEST 1: crearPromocion() con datos válidos debe guardar
    // la promoción correctamente.
    // ===========================================================
    @Test
    void crearPromocion_conDatosValidos_debeGuardarPromocion() {

        // GIVEN: el código no existe todavía y el repository devuelve
        // la promoción con un id al guardar.
        when(repository.existsByCodigo("VERANO2026")).thenReturn(false);
        when(repository.save(any(Promocion.class))).thenAnswer(invocacion -> {
            Promocion p = invocacion.getArgument(0);
            p.setId(1L);
            return p;
        });

        // WHEN
        PromocionResponseDTO respuesta = promocionService.crearPromocion(promocionRequestValida);

        // THEN
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("VERANO2026", respuesta.getCodigo());
        assertTrue(respuesta.getActivo());

        verify(repository, times(1)).save(any(Promocion.class));
    }

    // ===========================================================
    // TEST 2: crearPromocion() con un código ya existente debe
    // lanzar BadRequestException.
    // ===========================================================
    @Test
    void crearPromocion_conCodigoDuplicado_debeLanzarBadRequestException() {

        // GIVEN: el código ya está registrado
        when(repository.existsByCodigo("VERANO2026")).thenReturn(true);

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> promocionService.crearPromocion(promocionRequestValida)
        );

        assertEquals("Ya existe una promoción registrada con ese código.", ex.getMessage());

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 3: crearPromocion() con fechaFin anterior a fechaInicio
    // debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void crearPromocion_conFechasIncoherentes_debeLanzarBadRequestException() {

        // GIVEN: la fecha de fin es anterior a la fecha de inicio
        promocionRequestValida.setFechaInicio(LocalDate.now());
        promocionRequestValida.setFechaFin(LocalDate.now().minusDays(5));

        when(repository.existsByCodigo("VERANO2026")).thenReturn(false);

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> promocionService.crearPromocion(promocionRequestValida)
        );

        assertEquals("La fecha de fin no puede ser anterior a la fecha de inicio.", ex.getMessage());

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 4: validarCupon() con un código inexistente debe lanzar
    // PromocionNotFoundException.
    // ===========================================================
    @Test
    void validarCupon_conCodigoInexistente_debeLanzarNotFoundException() {

        // GIVEN: no existe ninguna promoción con ese código
        when(repository.findByCodigo("NOEXISTE")).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                PromocionNotFoundException.class,
                () -> promocionService.validarCupon("NOEXISTE")
        );
    }

    // ===========================================================
    // TEST 5: validarCupon() con un cupón vencido debe lanzar
    // BadRequestException.
    // ===========================================================
    @Test
    void validarCupon_conCuponVencido_debeLanzarBadRequestException() {

        // GIVEN: una promoción cuya vigencia ya terminó
        Promocion promocionVencida = new Promocion(
                1L, "INVIERNO2025", 10.0,
                LocalDate.now().minusDays(60),
                LocalDate.now().minusDays(1),
                true
        );

        when(repository.findByCodigo("INVIERNO2025")).thenReturn(Optional.of(promocionVencida));

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> promocionService.validarCupon("INVIERNO2025")
        );

        assertEquals("El cupón se encuentra vencido.", ex.getMessage());
    }

    // ===========================================================
    // TEST 6: aplicarCupon() con un cupón vigente y activo debe
    // marcarlo como utilizado (activo = false).
    // ===========================================================
    @Test
    void aplicarCupon_conCuponVigente_debeMarcarComoUtilizado() {

        // GIVEN: una promoción activa y dentro de su rango de vigencia
        Promocion promocionVigente = new Promocion(
                1L, "VERANO2026", 15.0,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30),
                true
        );

        when(repository.findByCodigo("VERANO2026")).thenReturn(Optional.of(promocionVigente));
        when(repository.save(any(Promocion.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        // WHEN
        PromocionResponseDTO respuesta = promocionService.aplicarCupon("VERANO2026");

        // THEN
        assertFalse(respuesta.getActivo());
        verify(repository, times(1)).save(any(Promocion.class));
    }

    // ===========================================================
    // TEST 7: aplicarCupon() con un cupón ya utilizado (inactivo)
    // debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void aplicarCupon_conCuponYaUtilizado_debeLanzarBadRequestException() {

        // GIVEN: una promoción que ya fue utilizada anteriormente
        Promocion promocionInactiva = new Promocion(
                1L, "VERANO2026", 15.0,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(30),
                false
        );

        when(repository.findByCodigo("VERANO2026")).thenReturn(Optional.of(promocionInactiva));

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> promocionService.aplicarCupon("VERANO2026")
        );

        assertEquals("El cupón ya fue utilizado o se encuentra inactivo.", ex.getMessage());

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 8: listarPromociones() con datos debe retornar la
    // lista completa mapeada a DTO.
    // ===========================================================
    @Test
    void listarPromociones_conDatos_debeRetornarListaCompleta() {

        Promocion p1 = new Promocion(1L, "VERANO2026", 15.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), true);
        Promocion p2 = new Promocion(2L, "INVIERNO2026", 10.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), true);

        when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<PromocionResponseDTO> respuesta = promocionService.listarPromociones();

        assertEquals(2, respuesta.size());
        assertEquals("VERANO2026", respuesta.get(0).getCodigo());
        assertEquals("INVIERNO2026", respuesta.get(1).getCodigo());

        verify(repository).findAll();
    }

    // ===========================================================
    // TEST 9: listarPromociones() sin datos debe retornar una
    // lista vacía.
    // ===========================================================
    @Test
    void listarPromociones_sinDatos_debeRetornarListaVacia() {

        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<PromocionResponseDTO> respuesta = promocionService.listarPromociones();

        assertTrue(respuesta.isEmpty());

        verify(repository).findAll();
    }

    // ===========================================================
    // TEST 10: obtenerPorId() con un id existente debe retornar
    // la promoción correspondiente.
    // ===========================================================
    @Test
    void obtenerPorId_conIdExistente_debeRetornarPromocion() {

        Promocion promocion = new Promocion(1L, "VERANO2026", 15.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), true);

        when(repository.findById(1L)).thenReturn(Optional.of(promocion));

        PromocionResponseDTO respuesta = promocionService.obtenerPorId(1L);

        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("VERANO2026", respuesta.getCodigo());

        verify(repository).findById(1L);
    }

    // ===========================================================
    // TEST 11: obtenerPorId() con un id inexistente debe lanzar
    // PromocionNotFoundException.
    // ===========================================================
    @Test
    void obtenerPorId_conIdInexistente_debeLanzarPromocionNotFoundException() {

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                PromocionNotFoundException.class,
                () -> promocionService.obtenerPorId(99L)
        );
    }

    // ===========================================================
    // TEST 12: actualizar() con datos válidos debe actualizar
    // correctamente la promoción existente.
    // ===========================================================
    @Test
    void actualizar_conDatosValidos_debeActualizarPromocion() {

        Promocion promocionExistente = new Promocion(1L, "VERANO2026", 15.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), true);

        PromocionRequestDTO dto = new PromocionRequestDTO();
        dto.setCodigo("VERANO2026");
        dto.setPorcentajeDescuento(20.0);
        dto.setFechaInicio(LocalDate.now().minusDays(1));
        dto.setFechaFin(LocalDate.now().plusDays(60));
        dto.setActivo(true);

        when(repository.findById(1L)).thenReturn(Optional.of(promocionExistente));
        when(repository.save(any(Promocion.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        PromocionResponseDTO respuesta = promocionService.actualizar(1L, dto);

        assertEquals(20.0, respuesta.getPorcentajeDescuento());
        assertEquals(dto.getFechaFin(), respuesta.getFechaFin());

        verify(repository).save(any(Promocion.class));
    }

    // ===========================================================
    // TEST 13: actualizar() con un id inexistente debe lanzar
    // PromocionNotFoundException.
    // ===========================================================
    @Test
    void actualizar_conIdInexistente_debeLanzarPromocionNotFoundException() {

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                PromocionNotFoundException.class,
                () -> promocionService.actualizar(99L, promocionRequestValida)
        );

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 14: actualizar() con un código que ya pertenece a OTRA
    // promoción debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void actualizar_conCodigoYaUsadoPorOtraPromocion_debeLanzarBadRequestException() {

        Promocion promocionExistente = new Promocion(1L, "VERANO2026", 15.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), true);

        PromocionRequestDTO dtoConCodigoAjeno = new PromocionRequestDTO();
        dtoConCodigoAjeno.setCodigo("INVIERNO2026");
        dtoConCodigoAjeno.setPorcentajeDescuento(20.0);
        dtoConCodigoAjeno.setFechaInicio(LocalDate.now().minusDays(1));
        dtoConCodigoAjeno.setFechaFin(LocalDate.now().plusDays(30));

        when(repository.findById(1L)).thenReturn(Optional.of(promocionExistente));
        when(repository.existsByCodigo("INVIERNO2026")).thenReturn(true);

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> promocionService.actualizar(1L, dtoConCodigoAjeno)
        );

        assertEquals("El código ya pertenece a otra promoción.", ex.getMessage());

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 15: actualizar() con el mismo código no debe validar
    // duplicado (no debe llamar a existsByCodigo).
    // ===========================================================
    @Test
    void actualizar_conMismoCodigo_noDebeValidarDuplicado() {

        Promocion promocionExistente = new Promocion(1L, "VERANO2026", 15.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), true);

        when(repository.findById(1L)).thenReturn(Optional.of(promocionExistente));
        when(repository.save(any(Promocion.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        promocionService.actualizar(1L, promocionRequestValida);

        verify(repository, never()).existsByCodigo(any());
        verify(repository).save(any(Promocion.class));
    }

    // ===========================================================
    // TEST 16: actualizar() con fechas incoherentes debe lanzar
    // BadRequestException.
    // ===========================================================
    @Test
    void actualizar_conFechasIncoherentes_debeLanzarBadRequestException() {

        Promocion promocionExistente = new Promocion(1L, "VERANO2026", 15.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), true);

        promocionRequestValida.setFechaInicio(LocalDate.now());
        promocionRequestValida.setFechaFin(LocalDate.now().minusDays(5));

        when(repository.findById(1L)).thenReturn(Optional.of(promocionExistente));

        assertThrows(
                BadRequestException.class,
                () -> promocionService.actualizar(1L, promocionRequestValida)
        );

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 17: eliminar() con un id existente debe eliminar la
    // promoción correctamente.
    // ===========================================================
    @Test
    void eliminar_conIdExistente_debeEliminarPromocion() {

        when(repository.existsById(1L)).thenReturn(true);

        promocionService.eliminar(1L);

        verify(repository).deleteById(1L);
    }

    // ===========================================================
    // TEST 18: eliminar() con un id inexistente debe lanzar
    // PromocionNotFoundException y no debe intentar eliminar.
    // ===========================================================
    @Test
    void eliminar_conIdInexistente_debeLanzarPromocionNotFoundException() {

        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(
                PromocionNotFoundException.class,
                () -> promocionService.eliminar(99L)
        );

        verify(repository, never()).deleteById(any());
    }

    // ===========================================================
    // TEST 19: validarCupon() con un cupón vigente y activo debe
    // retornar el DTO sin modificar su estado.
    // ===========================================================
    @Test
    void validarCupon_conCuponVigente_debeRetornarPromocionSinModificar() {

        Promocion promocionVigente = new Promocion(1L, "VERANO2026", 15.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30), true);

        when(repository.findByCodigo("VERANO2026")).thenReturn(Optional.of(promocionVigente));

        PromocionResponseDTO respuesta = promocionService.validarCupon("VERANO2026");

        assertNotNull(respuesta);
        assertTrue(respuesta.getActivo());

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 20: validarCupon() con un cupón que aún no está
    // vigente debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void validarCupon_conCuponAunNoVigente_debeLanzarBadRequestException() {

        Promocion promocionFutura = new Promocion(1L, "PRIMAVERA2027", 10.0,
                LocalDate.now().plusDays(10), LocalDate.now().plusDays(40), true);

        when(repository.findByCodigo("PRIMAVERA2027")).thenReturn(Optional.of(promocionFutura));

        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> promocionService.validarCupon("PRIMAVERA2027")
        );

        assertEquals("El cupón aún no se encuentra vigente.", ex.getMessage());
    }

    // ===========================================================
    // TEST 21: aplicarCupon() con un código inexistente debe
    // lanzar PromocionNotFoundException y no debe guardar.
    // ===========================================================
    @Test
    void aplicarCupon_conCodigoInexistente_debeLanzarPromocionNotFoundException() {

        when(repository.findByCodigo("NOEXISTE")).thenReturn(Optional.empty());

        assertThrows(
                PromocionNotFoundException.class,
                () -> promocionService.aplicarCupon("NOEXISTE")
        );

        verify(repository, never()).save(any());
    }
}

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
}

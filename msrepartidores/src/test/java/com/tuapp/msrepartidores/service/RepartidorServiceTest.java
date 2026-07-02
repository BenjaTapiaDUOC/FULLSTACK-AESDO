package com.tuapp.msrepartidores.service;

import com.tuapp.msrepartidores.dto.CambioEstadoRequestDTO;
import com.tuapp.msrepartidores.dto.RepartidorRequestDTO;
import com.tuapp.msrepartidores.dto.RepartidorResponseDTO;
import com.tuapp.msrepartidores.exception.BadRequestException;
import com.tuapp.msrepartidores.exception.RepartidorNotFoundException;
import com.tuapp.msrepartidores.model.EstadoRepartidor;
import com.tuapp.msrepartidores.model.Repartidor;
import com.tuapp.msrepartidores.repository.RepartidorRepository;

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
 * PRUEBAS UNITARIAS - RepartidorService
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
class RepartidorServiceTest {

    // --- DEPENDENCIAS FALSAS (MOCKS) ---
    @Mock
    private RepartidorRepository repository;

    // --- CLASE REAL BAJO PRUEBA ---
    @InjectMocks
    private RepartidorService repartidorService;

    private RepartidorRequestDTO repartidorRequestValido;

    @BeforeEach
    void setUp() {
        // Se ejecuta antes de CADA test. Preparamos un DTO válido
        // reutilizable para no repetir código en cada método.
        repartidorRequestValido = new RepartidorRequestDTO();
        repartidorRequestValido.setNombre("Cristobal Soto");
        repartidorRequestValido.setVehiculo("Moto");
    }

    // ===========================================================
    // TEST 1: crearRepartidor() con datos válidos debe guardar
    // el repartidor con estado inicial DISPONIBLE.
    // ===========================================================
    @Test
    void crearRepartidor_conDatosValidos_debeGuardarConEstadoDisponible() {

        // GIVEN: el repository, al guardar, devuelve el mismo repartidor con un id.
        when(repository.save(any(Repartidor.class))).thenAnswer(invocacion -> {
            Repartidor r = invocacion.getArgument(0);
            r.setId(1L);
            return r;
        });

        // WHEN
        RepartidorResponseDTO respuesta = repartidorService.crearRepartidor(repartidorRequestValido);

        // THEN
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("Cristobal Soto", respuesta.getNombre());
        assertEquals(EstadoRepartidor.DISPONIBLE, respuesta.getEstado());

        verify(repository, times(1)).save(any(Repartidor.class));
    }

    // ===========================================================
    // TEST 2: obtenerPorId() con un id inexistente debe lanzar
    // RepartidorNotFoundException.
    // ===========================================================
    @Test
    void obtenerPorId_conRepartidorInexistente_debeLanzarNotFoundException() {

        // GIVEN: el repositorio no encuentra el repartidor
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                RepartidorNotFoundException.class,
                () -> repartidorService.obtenerPorId(99L)
        );
    }

    // ===========================================================
    // TEST 3: cambiarEstado() con un repartidor existente debe
    // actualizar y devolver el nuevo estado.
    // ===========================================================
    @Test
    void cambiarEstado_conRepartidorExistente_debeActualizarEstado() {

        // GIVEN: un repartidor DISPONIBLE que será asignado a una ruta
        Repartidor repartidor = new Repartidor(1L, "Cristobal Soto", "Moto", EstadoRepartidor.DISPONIBLE);

        CambioEstadoRequestDTO cambioEstado = new CambioEstadoRequestDTO();
        cambioEstado.setEstado(EstadoRepartidor.EN_RUTA);

        when(repository.findById(1L)).thenReturn(Optional.of(repartidor));
        when(repository.save(any(Repartidor.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        // WHEN
        RepartidorResponseDTO respuesta = repartidorService.cambiarEstado(1L, cambioEstado);

        // THEN
        assertEquals(EstadoRepartidor.EN_RUTA, respuesta.getEstado());
        verify(repository, times(1)).save(any(Repartidor.class));
    }

    // ===========================================================
    // TEST 4: eliminar() un repartidor que se encuentra EN_RUTA
    // debe lanzar BadRequestException y no debe eliminarlo.
    // ===========================================================
    @Test
    void eliminar_conRepartidorEnRuta_debeLanzarBadRequestException() {

        // GIVEN: un repartidor que actualmente está EN_RUTA
        Repartidor repartidor = new Repartidor(1L, "Cristobal Soto", "Moto", EstadoRepartidor.EN_RUTA);

        when(repository.findById(1L)).thenReturn(Optional.of(repartidor));

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> repartidorService.eliminar(1L)
        );

        assertEquals("No se puede eliminar un repartidor que se encuentra en ruta.", ex.getMessage());

        verify(repository, never()).deleteById(any());
    }

    // ===========================================================
    // TEST 5: eliminar() un repartidor DISPONIBLE debe eliminarlo
    // correctamente.
    // ===========================================================
    @Test
    void eliminar_conRepartidorDisponible_debeEliminarCorrectamente() {

        // GIVEN: un repartidor que sí puede ser eliminado
        Repartidor repartidor = new Repartidor(1L, "Cristobal Soto", "Moto", EstadoRepartidor.DISPONIBLE);

        when(repository.findById(1L)).thenReturn(Optional.of(repartidor));

        // WHEN
        repartidorService.eliminar(1L);

        // THEN
        verify(repository, times(1)).deleteById(1L);
    }
}

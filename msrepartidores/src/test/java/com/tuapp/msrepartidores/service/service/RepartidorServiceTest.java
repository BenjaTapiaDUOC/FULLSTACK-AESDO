package com.tuapp.msrepartidores.service.service;

import com.tuapp.msrepartidores.dto.CambioEstadoRequestDTO;
import com.tuapp.msrepartidores.dto.RepartidorRequestDTO;
import com.tuapp.msrepartidores.dto.RepartidorResponseDTO;
import com.tuapp.msrepartidores.exception.BadRequestException;
import com.tuapp.msrepartidores.exception.RepartidorNotFoundException;
import com.tuapp.msrepartidores.model.EstadoRepartidor;
import com.tuapp.msrepartidores.model.Repartidor;
import com.tuapp.msrepartidores.repository.RepartidorRepository;

import com.tuapp.msrepartidores.service.RepartidorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
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

    // ===========================================================
    // TEST 6 (NUEVO): eliminar() con un repartidor inexistente
    // debe lanzar RepartidorNotFoundException.
    // ===========================================================
    @Test
    void eliminar_conRepartidorInexistente_debeLanzarNotFoundException() {

        // GIVEN
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                RepartidorNotFoundException.class,
                () -> repartidorService.eliminar(99L)
        );

        verify(repository, never()).deleteById(any());
    }

    // ===========================================================
    // TEST 7 (NUEVO): listarRepartidores() con datos debe mapear
    // correctamente cada Repartidor a su DTO.
    // ===========================================================
    @Test
    void listarRepartidores_conDatos_debeRetornarListaMapeada() {

        // GIVEN
        Repartidor r1 = new Repartidor(1L, "Cristobal Soto", "Moto", EstadoRepartidor.DISPONIBLE);
        Repartidor r2 = new Repartidor(2L, "Ana Rivas", "Bicicleta", EstadoRepartidor.INACTIVO);

        when(repository.findAll()).thenReturn(List.of(r1, r2));

        // WHEN
        List<RepartidorResponseDTO> resultado = repartidorService.listarRepartidores();

        // THEN
        assertEquals(2, resultado.size());
        assertEquals("Cristobal Soto", resultado.get(0).getNombre());
        assertEquals(EstadoRepartidor.INACTIVO, resultado.get(1).getEstado());
    }

    // ===========================================================
    // TEST 8 (NUEVO): listarRepartidores() sin registros debe
    // retornar una lista vacía (no null).
    // ===========================================================
    @Test
    void listarRepartidores_sinRegistros_debeRetornarListaVacia() {

        // GIVEN
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // WHEN
        List<RepartidorResponseDTO> resultado = repartidorService.listarRepartidores();

        // THEN
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ===========================================================
    // TEST 9 (NUEVO): listarDisponibles() debe consultar el
    // repositorio filtrando específicamente por DISPONIBLE.
    // ===========================================================
    @Test
    void listarDisponibles_debeFiltrarPorEstadoDisponible() {

        // GIVEN
        Repartidor disponible = new Repartidor(1L, "Cristobal Soto", "Moto", EstadoRepartidor.DISPONIBLE);

        when(repository.findByEstado(EstadoRepartidor.DISPONIBLE)).thenReturn(List.of(disponible));

        // WHEN
        List<RepartidorResponseDTO> resultado = repartidorService.listarDisponibles();

        // THEN
        assertEquals(1, resultado.size());
        assertEquals(EstadoRepartidor.DISPONIBLE, resultado.get(0).getEstado());
        verify(repository, times(1)).findByEstado(EstadoRepartidor.DISPONIBLE);
    }

    // ===========================================================
    // TEST 10 (NUEVO): obtenerPorId() con un repartidor existente
    // debe retornar el DTO correctamente mapeado.
    // ===========================================================
    @Test
    void obtenerPorId_conRepartidorExistente_debeRetornarDTO() {

        // GIVEN
        Repartidor repartidor = new Repartidor(5L, "Marco Vera", "Auto", EstadoRepartidor.DISPONIBLE);
        when(repository.findById(5L)).thenReturn(Optional.of(repartidor));

        // WHEN
        RepartidorResponseDTO resultado = repartidorService.obtenerPorId(5L);

        // THEN
        assertEquals(5L, resultado.getId());
        assertEquals("Marco Vera", resultado.getNombre());
        assertEquals("Auto", resultado.getVehiculo());
    }

    // ===========================================================
    // TEST 11 (NUEVO): actualizar() con un repartidor existente
    // debe actualizar nombre y vehículo, sin tocar el estado.
    // ===========================================================
    @Test
    void actualizar_conRepartidorExistente_debeActualizarNombreYVehiculo() {

        // GIVEN
        Repartidor repartidor = new Repartidor(1L, "Nombre viejo", "Bicicleta", EstadoRepartidor.EN_RUTA);

        RepartidorRequestDTO dtoActualizado = new RepartidorRequestDTO();
        dtoActualizado.setNombre("Nombre nuevo");
        dtoActualizado.setVehiculo("Moto");

        when(repository.findById(1L)).thenReturn(Optional.of(repartidor));
        when(repository.save(any(Repartidor.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        RepartidorResponseDTO resultado = repartidorService.actualizar(1L, dtoActualizado);

        // THEN
        assertEquals("Nombre nuevo", resultado.getNombre());
        assertEquals("Moto", resultado.getVehiculo());
        // El estado no debe modificarse por este método
        assertEquals(EstadoRepartidor.EN_RUTA, resultado.getEstado());

        verify(repository, times(1)).save(any(Repartidor.class));
    }

    // ===========================================================
    // TEST 12 (NUEVO): actualizar() sobre un repartidor inexistente
    // debe lanzar RepartidorNotFoundException.
    // ===========================================================
    @Test
    void actualizar_conRepartidorInexistente_debeLanzarNotFoundException() {

        // GIVEN
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                RepartidorNotFoundException.class,
                () -> repartidorService.actualizar(99L, repartidorRequestValido)
        );

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 13 (NUEVO): cambiarEstado() sobre un repartidor
    // inexistente debe lanzar RepartidorNotFoundException.
    // ===========================================================
    @Test
    void cambiarEstado_conRepartidorInexistente_debeLanzarNotFoundException() {

        // GIVEN
        CambioEstadoRequestDTO cambioEstado = new CambioEstadoRequestDTO();
        cambioEstado.setEstado(EstadoRepartidor.INACTIVO);

        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                RepartidorNotFoundException.class,
                () -> repartidorService.cambiarEstado(99L, cambioEstado)
        );

        verify(repository, never()).save(any());
    }
}

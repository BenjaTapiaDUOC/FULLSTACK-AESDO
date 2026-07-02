package com.tuapp.msrestaurantes.service;

import com.tuapp.msrestaurantes.dto.RestauranteRequestDTO;
import com.tuapp.msrestaurantes.dto.RestauranteResponseDTO;
import com.tuapp.msrestaurantes.exception.BadRequestException;
import com.tuapp.msrestaurantes.exception.RestauranteNotFoundException;
import com.tuapp.msrestaurantes.model.Restaurante;
import com.tuapp.msrestaurantes.repository.RestauranteRepository;

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
 * PRUEBAS UNITARIAS - RestauranteService
 * ===========================================================
 *
 * Se mockea RestauranteRepository. Este servicio tiene una
 * regla de negocio propia que vale la pena probar aparte:
 * si "activo" viene null en la creacion, debe asumirse true
 * por defecto.
 */
@ExtendWith(MockitoExtension.class)
class RestauranteServiceTest {

    @Mock
    private RestauranteRepository repository;

    @InjectMocks
    private RestauranteService restauranteService;

    private RestauranteRequestDTO requestSinActivo;

    @BeforeEach
    void setUp() {
        requestSinActivo = new RestauranteRequestDTO();
        requestSinActivo.setNombre("La Trattoria");
        requestSinActivo.setDireccion("Av. Siempre Viva 123");
        requestSinActivo.setCategoria("Italiana");
        requestSinActivo.setHorario("09:00 - 22:00");
        // activo queda en null a proposito, para probar el default
    }

    // ===========================================================
    // TEST 1: crearRestaurante() sin especificar "activo" debe
    // asignar TRUE por defecto (regla de negocio del servicio).
    // ===========================================================
    @Test
    void crearRestaurante_sinEspecificarActivo_debeAsignarActivoTruePorDefecto() {

        // GIVEN
        when(repository.existsByNombre("La Trattoria")).thenReturn(false);
        when(repository.save(any(Restaurante.class))).thenAnswer(invocacion -> {
            Restaurante r = invocacion.getArgument(0);
            r.setId(1L);
            return r;
        });

        // WHEN
        RestauranteResponseDTO respuesta = restauranteService.crearRestaurante(requestSinActivo);

        // THEN: aunque el DTO llego con activo = null, el guardado
        // debe quedar en TRUE.
        assertNotNull(respuesta);
        assertTrue(respuesta.getActivo());
    }

    // ===========================================================
    // TEST 2: crearRestaurante() con un nombre ya registrado
    // debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void crearRestaurante_conNombreDuplicado_debeLanzarBadRequestException() {

        // GIVEN
        when(repository.existsByNombre("La Trattoria")).thenReturn(true);

        // WHEN + THEN
        assertThrows(
                BadRequestException.class,
                () -> restauranteService.crearRestaurante(requestSinActivo)
        );

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 3: cambiarEstado() debe actualizar el campo activo
    // del restaurante y guardarlo.
    // ===========================================================
    @Test
    void cambiarEstado_debeActualizarActivoCorrectamente() {

        // GIVEN: un restaurante que actualmente esta activo.
        Restaurante restauranteActivo = new Restaurante(
                1L, "La Trattoria", "Av. Siempre Viva 123", "Italiana", "09:00 - 22:00", true);

        when(repository.findById(1L)).thenReturn(Optional.of(restauranteActivo));
        when(repository.save(any(Restaurante.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN: lo desactivamos.
        RestauranteResponseDTO respuesta = restauranteService.cambiarEstado(1L, false);

        // THEN
        assertFalse(respuesta.getActivo());
        verify(repository, times(1)).save(any(Restaurante.class));
    }

    // ===========================================================
    // TEST 4: obtenerPorId() con un id inexistente debe lanzar
    // RestauranteNotFoundException.
    // ===========================================================
    @Test
    void obtenerPorId_conIdInexistente_debeLanzarRestauranteNotFoundException() {

        // GIVEN
        when(repository.findById(88L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                RestauranteNotFoundException.class,
                () -> restauranteService.obtenerPorId(88L)
        );
    }

    // ===========================================================
    // TEST 5: listarActivos() debe delegar en
    // findByActivoTrue() y devolver solo los restaurantes
    // activos, mapeados a DTO.
    // ===========================================================
    @Test
    void listarActivos_debeRetornarSoloRestaurantesActivos() {

        // GIVEN
        Restaurante activo = new Restaurante(
                1L, "La Trattoria", "Av. Siempre Viva 123", "Italiana", "09:00 - 22:00", true);

        when(repository.findByActivoTrue()).thenReturn(List.of(activo));

        // WHEN
        List<RestauranteResponseDTO> resultado = restauranteService.listarActivos();

        // THEN
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActivo());

        // Verificamos que se use la consulta especifica de
        // activos, y no el findAll() generico.
        verify(repository, times(1)).findByActivoTrue();
        verify(repository, never()).findAll();
    }

    // ===========================================================
    // TEST 6 (NUEVO): crearRestaurante() con "activo" explícito
    // en false debe respetar ese valor (no forzar el default).
    // ===========================================================
    @Test
    void crearRestaurante_conActivoExplicitoFalse_debeRespetarValorEnviado() {

        // GIVEN
        RestauranteRequestDTO dto = new RestauranteRequestDTO();
        dto.setNombre("Sushi Ken");
        dto.setDireccion("Av. Central 500");
        dto.setCategoria("Sushi");
        dto.setHorario("12:00 - 21:00");
        dto.setActivo(false);

        when(repository.existsByNombre("Sushi Ken")).thenReturn(false);
        when(repository.save(any(Restaurante.class))).thenAnswer(inv -> {
            Restaurante r = inv.getArgument(0);
            r.setId(2L);
            return r;
        });

        // WHEN
        RestauranteResponseDTO respuesta = restauranteService.crearRestaurante(dto);

        // THEN
        assertFalse(respuesta.getActivo());
    }

    // ===========================================================
    // TEST 7 (NUEVO): listarRestaurantes() con datos debe mapear
    // correctamente cada Restaurante a su DTO.
    // ===========================================================
    @Test
    void listarRestaurantes_conDatos_debeRetornarListaMapeada() {

        // GIVEN
        Restaurante r1 = new Restaurante(1L, "La Trattoria", "Dir 1", "Italiana", "09:00 - 22:00", true);
        Restaurante r2 = new Restaurante(2L, "Sushi Ken", "Dir 2", "Sushi", "12:00 - 21:00", false);

        when(repository.findAll()).thenReturn(List.of(r1, r2));

        // WHEN
        List<RestauranteResponseDTO> resultado = restauranteService.listarRestaurantes();

        // THEN
        assertEquals(2, resultado.size());
        assertEquals("La Trattoria", resultado.get(0).getNombre());
        assertFalse(resultado.get(1).getActivo());
    }

    // ===========================================================
    // TEST 8 (NUEVO): listarRestaurantes() sin registros debe
    // retornar una lista vacía (no null).
    // ===========================================================
    @Test
    void listarRestaurantes_sinRegistros_debeRetornarListaVacia() {

        // GIVEN
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // WHEN
        List<RestauranteResponseDTO> resultado = restauranteService.listarRestaurantes();

        // THEN
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ===========================================================
    // TEST 9 (NUEVO): obtenerPorId() con un restaurante existente
    // debe retornar el DTO correctamente mapeado.
    // ===========================================================
    @Test
    void obtenerPorId_conRestauranteExistente_debeRetornarDTO() {

        // GIVEN
        Restaurante restaurante = new Restaurante(5L, "Pizza Nostra", "Dir 5", "Italiana", "18:00 - 23:00", true);
        when(repository.findById(5L)).thenReturn(Optional.of(restaurante));

        // WHEN
        RestauranteResponseDTO resultado = restauranteService.obtenerPorId(5L);

        // THEN
        assertEquals(5L, resultado.getId());
        assertEquals("Pizza Nostra", resultado.getNombre());
    }

    // ===========================================================
    // TEST 10 (NUEVO): actualizar() manteniendo el mismo nombre
    // no debe validar duplicados y debe guardar los nuevos datos.
    // ===========================================================
    @Test
    void actualizar_conMismoNombre_debeActualizarSinValidarDuplicado() {

        // GIVEN
        Restaurante existente = new Restaurante(1L, "La Trattoria", "Dir vieja", "Italiana", "09:00 - 22:00", true);

        RestauranteRequestDTO dto = new RestauranteRequestDTO();
        dto.setNombre("La Trattoria"); // mismo nombre
        dto.setDireccion("Dir nueva 456");
        dto.setCategoria("Italiana");
        dto.setHorario("10:00 - 23:00");
        dto.setActivo(null); // debe mantener el valor actual (true)

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Restaurante.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        RestauranteResponseDTO resultado = restauranteService.actualizar(1L, dto);

        // THEN
        assertEquals("Dir nueva 456", resultado.getDireccion());
        assertTrue(resultado.getActivo()); // se mantuvo el valor anterior

        verify(repository, never()).existsByNombre(any());
        verify(repository, times(1)).save(any(Restaurante.class));
    }

    // ===========================================================
    // TEST 11 (NUEVO): actualizar() cambiando a un nombre nuevo
    // y disponible debe validar y guardar correctamente.
    // ===========================================================
    @Test
    void actualizar_conNombreNuevoDisponible_debeActualizarCorrectamente() {

        // GIVEN
        Restaurante existente = new Restaurante(1L, "La Trattoria", "Dir vieja", "Italiana", "09:00 - 22:00", true);

        RestauranteRequestDTO dto = new RestauranteRequestDTO();
        dto.setNombre("La Trattoria Nueva");
        dto.setDireccion("Dir nueva 456");
        dto.setCategoria("Italiana");
        dto.setHorario("10:00 - 23:00");
        dto.setActivo(false);

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.existsByNombre("La Trattoria Nueva")).thenReturn(false);
        when(repository.save(any(Restaurante.class))).thenAnswer(inv -> inv.getArgument(0));

        // WHEN
        RestauranteResponseDTO resultado = restauranteService.actualizar(1L, dto);

        // THEN
        assertEquals("La Trattoria Nueva", resultado.getNombre());
        assertFalse(resultado.getActivo());

        verify(repository, times(1)).existsByNombre("La Trattoria Nueva");
        verify(repository, times(1)).save(any(Restaurante.class));
    }

    // ===========================================================
    // TEST 12 (NUEVO): actualizar() cambiando a un nombre que ya
    // pertenece a otro restaurante debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void actualizar_conNombreYaUsadoPorOtroRestaurante_debeLanzarBadRequestException() {

        // GIVEN
        Restaurante existente = new Restaurante(1L, "La Trattoria", "Dir vieja", "Italiana", "09:00 - 22:00", true);

        RestauranteRequestDTO dto = new RestauranteRequestDTO();
        dto.setNombre("Sushi Ken");
        dto.setDireccion("Dir nueva 456");
        dto.setCategoria("Italiana");
        dto.setHorario("10:00 - 23:00");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.existsByNombre("Sushi Ken")).thenReturn(true);

        // WHEN + THEN
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> restauranteService.actualizar(1L, dto)
        );

        assertEquals("El nombre ya pertenece a otro restaurante.", ex.getMessage());
        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 13 (NUEVO): actualizar() sobre un restaurante
    // inexistente debe lanzar RestauranteNotFoundException.
    // ===========================================================
    @Test
    void actualizar_conRestauranteInexistente_debeLanzarNotFoundException() {

        // GIVEN
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                RestauranteNotFoundException.class,
                () -> restauranteService.actualizar(99L, requestSinActivo)
        );

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 14 (NUEVO): cambiarEstado() sobre un restaurante
    // inexistente debe lanzar RestauranteNotFoundException.
    // ===========================================================
    @Test
    void cambiarEstado_conRestauranteInexistente_debeLanzarNotFoundException() {

        // GIVEN
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                RestauranteNotFoundException.class,
                () -> restauranteService.cambiarEstado(99L, true)
        );

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 15 (NUEVO): eliminar() con un restaurante existente
    // debe eliminarlo correctamente.
    // ===========================================================
    @Test
    void eliminar_conRestauranteExistente_debeEliminarCorrectamente() {

        // GIVEN
        when(repository.existsById(1L)).thenReturn(true);

        // WHEN
        restauranteService.eliminar(1L);

        // THEN
        verify(repository, times(1)).deleteById(1L);
    }

    // ===========================================================
    // TEST 16 (NUEVO): eliminar() con un restaurante inexistente
    // debe lanzar RestauranteNotFoundException.
    // ===========================================================
    @Test
    void eliminar_conRestauranteInexistente_debeLanzarNotFoundException() {

        // GIVEN
        when(repository.existsById(99L)).thenReturn(false);

        // WHEN + THEN
        assertThrows(
                RestauranteNotFoundException.class,
                () -> restauranteService.eliminar(99L)
        );

        verify(repository, never()).deleteById(any());
    }
}

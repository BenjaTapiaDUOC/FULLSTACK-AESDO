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
}

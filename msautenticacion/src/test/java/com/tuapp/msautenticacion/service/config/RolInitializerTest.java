package com.tuapp.msautenticacion.service.config;

import com.tuapp.msautenticacion.config.RolInitializer;
import com.tuapp.msautenticacion.model.Rol;
import com.tuapp.msautenticacion.repository.RolRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - RolInitializer
 * ===========================================================
 *
 * Verifica que al ejecutar run() se creen unicamente los
 * roles que aun no existen en la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class RolInitializerTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolInitializer rolInitializer;

    // ===========================================================
    // TEST 1: run() con ningun rol existente debe crear los 3
    // roles base del sistema.
    // ===========================================================
    @Test
    void run_sinRolesExistentes_debeCrearLosTresRoles() throws Exception {

        when(rolRepository.existsByNombre(anyString())).thenReturn(false);

        rolInitializer.run();

        verify(rolRepository, times(1)).save(argThat(rol -> rol.getNombre().equals("ADMIN")));
        verify(rolRepository, times(1)).save(argThat(rol -> rol.getNombre().equals("CLIENTE")));
        verify(rolRepository, times(1)).save(argThat(rol -> rol.getNombre().equals("REPARTIDOR")));
    }

    // ===========================================================
    // TEST 2: run() con todos los roles ya existentes no debe
    // volver a guardarlos.
    // ===========================================================
    @Test
    void run_conRolesYaExistentes_noDebeVolverACrearlos() throws Exception {

        when(rolRepository.existsByNombre(anyString())).thenReturn(true);

        rolInitializer.run();

        verify(rolRepository, never()).save(any(Rol.class));
    }

    // ===========================================================
    // TEST 3: run() con solo algunos roles existentes debe
    // crear unicamente los faltantes.
    // ===========================================================
    @Test
    void run_conAlgunosRolesExistentes_debeCrearSoloLosFaltantes() throws Exception {

        when(rolRepository.existsByNombre("ADMIN")).thenReturn(true);
        when(rolRepository.existsByNombre("CLIENTE")).thenReturn(false);
        when(rolRepository.existsByNombre("REPARTIDOR")).thenReturn(true);

        rolInitializer.run();

        verify(rolRepository, never()).save(argThat(rol -> rol.getNombre().equals("ADMIN")));
        verify(rolRepository, times(1)).save(argThat(rol -> rol.getNombre().equals("CLIENTE")));
        verify(rolRepository, never()).save(argThat(rol -> rol.getNombre().equals("REPARTIDOR")));
    }

}

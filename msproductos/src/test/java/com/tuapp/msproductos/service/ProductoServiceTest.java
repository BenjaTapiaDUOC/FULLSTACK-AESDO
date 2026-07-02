package com.tuapp.msproductos.service;

import com.tuapp.msproductos.dto.ProductoRequestDTO;
import com.tuapp.msproductos.dto.ProductoResponseDTO;
import com.tuapp.msproductos.exception.BadRequestException;
import com.tuapp.msproductos.exception.ProductoNotFoundException;
import com.tuapp.msproductos.model.Producto;
import com.tuapp.msproductos.repository.ProductoRepository;

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
 * PRUEBAS UNITARIAS - ProductoService
 * ===========================================================
 *
 * Se mockea ProductoRepository. Estructura identica a
 * UsuarioServiceTest, ya que ProductoService sigue el mismo
 * patron CRUD + validacion de nombre duplicado.
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService productoService;

    private ProductoRequestDTO requestValido;

    @BeforeEach
    void setUp() {
        requestValido = new ProductoRequestDTO();
        requestValido.setNombre("Pizza Napolitana");
        requestValido.setPrecio(8990.0);
        requestValido.setCategoria("Comida rapida");
    }

    // ===========================================================
    // TEST 1: crearProducto() con nombre nuevo debe guardar
    // correctamente.
    // ===========================================================
    @Test
    void crearProducto_conNombreNuevo_debeCrearProductoCorrectamente() {

        // GIVEN
        when(repository.existsByNombreIgnoreCase("Pizza Napolitana")).thenReturn(false);
        when(repository.save(any(Producto.class))).thenAnswer(invocacion -> {
            Producto p = invocacion.getArgument(0);
            p.setId(1L);
            return p;
        });

        // WHEN
        ProductoResponseDTO respuesta = productoService.crearProducto(requestValido);

        // THEN
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals(8990.0, respuesta.getPrecio());
        verify(repository, times(1)).save(any(Producto.class));
    }

    // ===========================================================
    // TEST 2: crearProducto() con un nombre que ya existe
    // (ignorando mayusculas/minusculas) debe lanzar
    // BadRequestException.
    // ===========================================================
    @Test
    void crearProducto_conNombreDuplicado_debeLanzarBadRequestException() {

        // GIVEN
        when(repository.existsByNombreIgnoreCase("Pizza Napolitana")).thenReturn(true);

        // WHEN + THEN
        assertThrows(
                BadRequestException.class,
                () -> productoService.crearProducto(requestValido)
        );

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 3: obtenerPorId() con un id inexistente debe lanzar
    // ProductoNotFoundException.
    // ===========================================================
    @Test
    void obtenerPorId_conIdInexistente_debeLanzarProductoNotFoundException() {

        // GIVEN
        when(repository.findById(77L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                ProductoNotFoundException.class,
                () -> productoService.obtenerPorId(77L)
        );
    }

    // ===========================================================
    // TEST 4: actualizar() con un nombre que ya pertenece a
    // OTRO producto debe lanzar BadRequestException.
    // ===========================================================
    @Test
    void actualizar_conNombreYaUsadoPorOtroProducto_debeLanzarBadRequestException() {

        // GIVEN: el producto 1 existe, con su nombre actual.
        Producto productoExistente = new Producto(1L, "Pizza Napolitana", 8990.0, "Comida rapida");
        when(repository.findById(1L)).thenReturn(Optional.of(productoExistente));

        // Se intenta renombrar a un nombre que YA usa otro producto.
        ProductoRequestDTO dtoConNombreAjeno = new ProductoRequestDTO();
        dtoConNombreAjeno.setNombre("Hamburguesa Clasica");
        dtoConNombreAjeno.setPrecio(6990.0);
        dtoConNombreAjeno.setCategoria("Comida rapida");

        when(repository.existsByNombreIgnoreCase("Hamburguesa Clasica")).thenReturn(true);

        // WHEN + THEN
        assertThrows(
                BadRequestException.class,
                () -> productoService.actualizar(1L, dtoConNombreAjeno)
        );

        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 5: eliminar() con un id inexistente debe lanzar
    // ProductoNotFoundException.
    // ===========================================================
    @Test
    void eliminar_conIdInexistente_debeLanzarProductoNotFoundException() {

        // GIVEN
        when(repository.existsById(40L)).thenReturn(false);

        // WHEN + THEN
        assertThrows(
                ProductoNotFoundException.class,
                () -> productoService.eliminar(40L)
        );

        verify(repository, never()).deleteById(any());
    }
}

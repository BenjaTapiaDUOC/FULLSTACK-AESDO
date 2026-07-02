package mspedidos.service;

import mspedidos.client.UsuarioClient;
import mspedidos.dto.DetalleRequestDTO;
import mspedidos.dto.PedidoRequestDTO;
import mspedidos.dto.PedidoResponseDTO;
import mspedidos.exception.BadRequestException;
import mspedidos.exception.PedidoNotFoundException;
import mspedidos.model.DetallePedido;
import mspedidos.model.Pedido;
import mspedidos.repository.PedidoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - PedidoService
 * ===========================================================
 *
 * @ExtendWith(MockitoExtension.class) le dice a JUnit que
 * active Mockito en esta clase, para que las anotaciones
 * @Mock y @InjectMocks funcionen automáticamente.
 *
 * No se levanta el contexto de Spring (no es @SpringBootTest),
 * por lo que estas pruebas son rápidas: no tocan MySQL ni
 * hacen llamadas HTTP reales.
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    // --- DEPENDENCIAS FALSAS (MOCKS) ---
    // Mockito crea versiones "de mentira" de estas clases.
    // Nosotros les decimos qué deben responder en cada test.
    @Mock
    private PedidoRepository repository;

    @Mock
    private UsuarioClient usuarioClient;

    // --- CLASE REAL BAJO PRUEBA ---
    // @InjectMocks crea un PedidoService real, pero le inyecta
    // los mocks de arriba en vez de las implementaciones reales.
    @InjectMocks
    private PedidoService pedidoService;

    private PedidoRequestDTO pedidoRequestValido;

    @BeforeEach
    void setUp() {
        // Se ejecuta antes de CADA test. Preparamos un DTO válido
        // reutilizable para no repetir código en cada método.
        DetalleRequestDTO detalle = new DetalleRequestDTO();
        detalle.setProductoId(10L);
        detalle.setCantidad(2);
        detalle.setPrecio(5990.0);

        pedidoRequestValido = new PedidoRequestDTO();
        pedidoRequestValido.setUsuarioId(1L);
        pedidoRequestValido.setDetalles(List.of(detalle));
    }

    // ===========================================================
    // TEST 1: crear() con datos válidos debe guardar y calcular
    // el total correctamente.
    // ===========================================================
    @Test
    void crear_conDatosValidos_debeCalcularTotalYGuardarPedido() {

        // GIVEN: el usuario existe (usuarioClient no lanza excepción)
        // y el repository, al guardar, devuelve el mismo pedido con un id.
        when(repository.save(any(Pedido.class))).thenAnswer(invocacion -> {
            Pedido p = invocacion.getArgument(0);
            p.setId(1L);
            return p;
        });

        // WHEN: ejecutamos el método real que queremos probar.
        PedidoResponseDTO respuesta = pedidoService.crear(pedidoRequestValido);

        // THEN: verificamos el resultado.
        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("PENDIENTE", respuesta.getEstado());
        // total esperado = precio * cantidad = 5990.0 * 2 = 11980.0
        assertEquals(11980.0, respuesta.getTotal());

        // Verificamos que efectivamente se llamó a usuarioClient
        // para validar que el usuario existe, y que se guardó en el repo.
        verify(usuarioClient, times(1)).obtenerUsuario(1L);
        verify(repository, times(1)).save(any(Pedido.class));
    }

    // ===========================================================
    // TEST 2: crear() sin detalles debe lanzar BadRequestException
    // y NO debe intentar guardar nada ni consultar a msusuarios.
    // ===========================================================
    @Test
    void crear_sinDetalles_debeLanzarBadRequestException() {

        // GIVEN: un pedido sin detalles (lista vacía)
        pedidoRequestValido.setDetalles(List.of());

        // WHEN + THEN: esperamos que lance la excepción de negocio
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> pedidoService.crear(pedidoRequestValido)
        );

        assertEquals("El pedido debe tener al menos un producto.", ex.getMessage());

        // Verificamos que la validación cortó el flujo ANTES de
        // llamar a msusuarios o guardar en la base de datos.
        verify(usuarioClient, never()).obtenerUsuario(any());
        verify(repository, never()).save(any());
    }

    // ===========================================================
    // TEST 3: actualizarEstado() con un estado inválido debe
    // lanzar BadRequestException.
    // ===========================================================
    @Test
    void actualizarEstado_conEstadoInvalido_debeLanzarBadRequestException() {

        assertThrows(
                BadRequestException.class,
                () -> pedidoService.actualizarEstado(1L, "ESTADO_QUE_NO_EXISTE")
        );

        // Como el estado es inválido, nunca debería llegar a
        // buscar el pedido en el repositorio.
        verify(repository, never()).findById(any());
    }

    // ===========================================================
    // TEST 4: actualizarEstado() con un id que no existe debe
    // lanzar PedidoNotFoundException.
    // ===========================================================
    @Test
    void actualizarEstado_conPedidoInexistente_debeLanzarNotFoundException() {

        // GIVEN: el repositorio no encuentra el pedido
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN
        assertThrows(
                PedidoNotFoundException.class,
                () -> pedidoService.actualizarEstado(99L, "ENVIADO")
        );
    }

    // ===========================================================
    // TEST 5: obtenerPorId() con un pedido existente debe
    // devolver el DTO correctamente mapeado.
    // ===========================================================
    @Test
    void obtenerPorId_conPedidoExistente_debeRetornarDTO() {

        // GIVEN: construimos un Pedido "real" en memoria (no en BD)
        Pedido pedido = new Pedido();
        pedido.setId(5L);
        pedido.setUsuarioId(2L);
        pedido.setEstado("PENDIENTE");
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setTotal(0.0);

        DetallePedido detalle = new DetallePedido();
        detalle.setId(1L);
        detalle.setProductoId(10L);
        detalle.setCantidad(1);
        detalle.setPrecio(1000.0);
        pedido.agregarDetalle(detalle);

        when(repository.findById(5L)).thenReturn(Optional.of(pedido));

        // WHEN
        PedidoResponseDTO respuesta = pedidoService.obtenerPorId(5L);

        // THEN
        assertEquals(5L, respuesta.getId());
        assertEquals(1, respuesta.getDetalles().size());
        assertEquals(1000.0, respuesta.getDetalles().get(0).getSubtotal());
    }
}

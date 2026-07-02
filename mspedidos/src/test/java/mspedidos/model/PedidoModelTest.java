package mspedidos.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ===========================================================
 * PRUEBAS UNITARIAS - Pedido / DetallePedido (métodos custom)
 * ===========================================================
 *
 * Lombok genera getters/setters automáticamente (ya cubiertos
 * indirectamente por los tests de servicio y controller). Acá
 * se prueban puntualmente los ÚNICOS métodos escritos a mano:
 *
 * - Pedido.agregarDetalle()
 * - DetallePedido.getSubtotal() (con sus 2 ramas)
 */
class PedidoModelTest {

    // ===========================================================
    // TEST 1: agregarDetalle() debe agregar el detalle a la lista
    // y sincronizar la relación bidireccional (detalle -> pedido).
    // ===========================================================
    @Test
    void agregarDetalle_debeAgregarDetalleYSincronizarRelacion() {

        Pedido pedido = new Pedido();
        DetallePedido detalle = new DetallePedido();

        pedido.agregarDetalle(detalle);

        assertEquals(1, pedido.getDetalles().size());
        assertSame(detalle, pedido.getDetalles().get(0));
        assertSame(pedido, detalle.getPedido());
    }

    // ===========================================================
    // TEST 2: getSubtotal() con precio y cantidad definidos debe
    // calcular precio * cantidad.
    // ===========================================================
    @Test
    void getSubtotal_conPrecioYCantidad_debeCalcularCorrectamente() {

        DetallePedido detalle = new DetallePedido();
        detalle.setPrecio(1000.0);
        detalle.setCantidad(3);

        assertEquals(3000.0, detalle.getSubtotal());
    }

    // ===========================================================
    // TEST 3: getSubtotal() con precio o cantidad nulos debe
    // retornar 0.0 en vez de lanzar NullPointerException.
    // ===========================================================
    @Test
    void getSubtotal_conPrecioOCantidadNulos_debeRetornarCero() {

        DetallePedido sinPrecio = new DetallePedido();
        sinPrecio.setCantidad(2);
        assertEquals(0.0, sinPrecio.getSubtotal());

        DetallePedido sinCantidad = new DetallePedido();
        sinCantidad.setPrecio(500.0);
        assertEquals(0.0, sinCantidad.getSubtotal());

        DetallePedido sinNada = new DetallePedido();
        assertEquals(0.0, sinNada.getSubtotal());
    }

}

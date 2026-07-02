package mspedidos.exception;

/**
 * ===========================================================
 * PEDIDO NOT FOUND EXCEPTION
 * ===========================================================
 *
 * Esta excepción se lanza cuando el pedido solicitado
 * no existe en la base de datos.
 *
 * El GlobalExceptionHandler la captura y responde
 * automáticamente con un HTTP 404.
 */

public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(String mensaje) {
        super(mensaje);
    }

}

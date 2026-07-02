package mspedidos.exception;

/**
 * ===========================================================
 * PEDIDO EXCEPTION
 * ===========================================================
 *
 * Excepción genérica del dominio de pedidos.
 *
 * Se utiliza principalmente cuando ocurre un problema de
 * comunicación con otro microservicio (por ejemplo,
 * msusuarios no responde).
 *
 * El GlobalExceptionHandler la captura y responde con
 * HTTP 503 SERVICE UNAVAILABLE.
 */

public class PedidoException extends RuntimeException {

    public PedidoException(String mensaje) {
        super(mensaje);
    }

}

package com.tuapp.mspagos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ===========================================================
 * ENTIDAD PAGO
 * ===========================================================
 *
 * Esta clase representa la tabla "pagos" en la base de datos.
 *
 * Cada atributo corresponde a una columna.
 *
 * Spring Data JPA utilizará esta entidad para realizar las
 * operaciones CRUD automáticamente.
 */

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    /**
     * Identificador único del pago.
     * Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador del pedido asociado a este pago.
     *
     * Este campo relaciona el pago con el microservicio
     * de Pedidos (mspedidos) a través de comunicación remota.
     */
    @Column(nullable = false)
    private Long pedidoId;

    /**
     * Monto del pago.
     *
     * Debe ser mayor a cero para poder ser procesado.
     */
    @Column(nullable = false)
    private Double monto;

    /**
     * Metodo de pago utilizado.
     *
     * Ejemplos: TARJETA, EFECTIVO, TRANSFERENCIA.
     */
    @Column(nullable = false)
    private String metodoPago;

    /**
     * Estado del pago.
     *
     * Valores posibles: PENDIENTE, APROBADO, RECHAZADO.
     *
     * Este valor es calculado por la lógica de negocio,
     * no lo entrega el cliente.
     */
    @Column(nullable = false)
    private String estado;

    /**
     * Fecha y hora en que se registró el pago.
     *
     * Se asigna automáticamente en el momento de la creación.
     */
    @Column(nullable = false)
    private LocalDateTime fechaPago;

}

package mspedidos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ===========================================================
 * ENTIDAD PEDIDO
 * ===========================================================
 *
 * Esta clase representa la tabla "pedidos" en la base de datos.
 *
 * Cada atributo corresponde a una columna.
 *
 * Un pedido pertenece a un usuario (usuarioId), el cual es
 * validado contra el microservicio msusuarios antes de
 * persistir el pedido.
 *
 * Spring Data JPA utilizará esta entidad para realizar las
 * operaciones CRUD automáticamente.
 */

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    /**
     * Identificador único del pedido.
     * Se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador del usuario que realiza el pedido.
     *
     * Este dato se valida en tiempo real contra el
     * microservicio msusuarios mediante WebClient.
     */
    @Column(nullable = false)
    private Long usuarioId;

    /**
     * Monto total del pedido.
     *
     * Se calcula automáticamente en base a la suma de
     * (precio * cantidad) de cada detalle asociado.
     */
    @Column(nullable = false)
    private Double total;

    /**
     * Estado actual del pedido.
     *
     * Valores permitidos:
     * PENDIENTE, EN_PROCESO, ENVIADO, ENTREGADO, CANCELADO
     */
    @Column(nullable = false)
    private String estado;

    /**
     * Fecha y hora en que se registró el pedido.
     */
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Detalles (líneas) del pedido.
     *
     * Relación 1:N con DetallePedido.
     *
     * mappedBy indica que la relación es administrada por el
     * atributo "pedido" dentro de DetallePedido (dueño de la FK).
     *
     * cascade = ALL: al guardar/eliminar un pedido, se
     * guardan/eliminan también sus detalles.
     *
     * orphanRemoval = true: si un detalle se quita de la lista,
     * se elimina también de la base de datos.
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();

    /**
     * Metodo de utilidad para mantener sincronizada la relación
     * bidireccional entre Pedido y DetallePedido.
     */
    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        this.detalles.add(detalle);
    }

}

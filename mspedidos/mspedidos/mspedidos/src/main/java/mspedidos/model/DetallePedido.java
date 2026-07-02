package mspedidos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * ===========================================================
 * ENTIDAD DETALLE PEDIDO
 * ===========================================================
 *
 * Esta clase representa la tabla "detalle_pedidos" en la base
 * de datos.
 *
 * Cada registro corresponde a un producto dentro de un pedido.
 *
 * Es el lado dueño de la relación con Pedido (contiene la
 * columna pedido_id como clave foránea).
 */

@Entity
@Table(name = "detalle_pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {

    /**
     * Identificador único del detalle.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificador del producto asociado a esta línea del
     * pedido. Este proyecto no incluye un microservicio de
     * productos, por lo que se almacena como referencia simple.
     */
    @Column(nullable = false)
    private Long productoId;

    /**
     * Cantidad de unidades solicitadas del producto.
     */
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Precio unitario del producto al momento de generar
     * el pedido.
     */
    @Column(nullable = false)
    private Double precio;

    /**
     * Pedido al que pertenece este detalle.
     *
     * Lado dueño de la relación (contiene la FK pedido_id).
     *
     * @JsonIgnore evita loops infinitos de serialización
     * (Pedido -> detalles -> pedido -> detalles ...).
     *
     * @ToString.Exclude y @EqualsAndHashCode.Exclude evitan que
     * Lombok genere toString()/equals()/hashCode() recursivos
     * entre Pedido y DetallePedido (StackOverflowError).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pedido pedido;

    /**
     * Calcula el subtotal de esta línea (precio * cantidad).
     * No se persiste, es solo un valor derivado.
     */
    @Transient
    public Double getSubtotal() {
        if (precio == null || cantidad == null) {
            return 0.0;
        }
        return precio * cantidad;
    }

}

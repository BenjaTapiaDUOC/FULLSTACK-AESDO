package mspedidos.seeder;

import mspedidos.model.DetallePedido;
import mspedidos.model.Pedido;
import mspedidos.repository.PedidoRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * ===========================================================
 * SEEDER DE PEDIDOS (DATAFAKER)
 * ===========================================================
 *
 * Al levantar el microservicio, esta clase verifica si la
 * tabla "pedidos" está vacía. Si lo está, genera datos de
 * prueba (pedidos con sus respectivos detalles) utilizando la
 * librería Datafaker, para facilitar las pruebas manuales y
 * la demostración de los endpoints.
 *
 * Si ya existen registros, no hace nada (evita duplicar datos
 * en cada reinicio del servicio).
 */

@Component
public class PedidoSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PedidoSeeder.class);

    private static final int CANTIDAD_PEDIDOS = 15;

    private static final List<String> ESTADOS = List.of(
            "PENDIENTE", "EN_PROCESO", "ENVIADO", "ENTREGADO", "CANCELADO"
    );

    private final PedidoRepository pedidoRepository;
    private final Faker faker = new Faker(new Locale("es"));

    public PedidoSeeder(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public void run(String... args) {

        if (pedidoRepository.count() > 0) {
            logger.info("La tabla de pedidos ya contiene datos. Se omite la carga de datos falsos.");
            return;
        }

        logger.info("Generando {} pedidos de prueba con Datafaker...", CANTIDAD_PEDIDOS);

        for (int i = 0; i < CANTIDAD_PEDIDOS; i++) {

            Long usuarioId = (long) (i + 1);
            String estado = ESTADOS.get(faker.random().nextInt(ESTADOS.size()));
            LocalDateTime fechaCreacion = LocalDateTime.now().minusDays(faker.number().numberBetween(0, 30));

            Pedido pedido = new Pedido();
            pedido.setUsuarioId(usuarioId);
            pedido.setEstado(estado);
            pedido.setFechaCreacion(fechaCreacion);

            int cantidadDetalles = faker.number().numberBetween(1, 5);
            double total = 0.0;

            for (int j = 0; j < cantidadDetalles; j++) {

                Long productoId = (long) faker.number().numberBetween(1, 30);
                Integer cantidad = faker.number().numberBetween(1, 6);
                Double precio = faker.number().randomDouble(0, 1000, 20000);

                DetallePedido detalle = new DetallePedido();
                detalle.setProductoId(productoId);
                detalle.setCantidad(cantidad);
                detalle.setPrecio(precio);

                pedido.agregarDetalle(detalle);

                total += precio * cantidad;
            }

            pedido.setTotal(total);

            pedidoRepository.save(pedido);
        }

        logger.info("Carga de pedidos de prueba finalizada.");
    }

}

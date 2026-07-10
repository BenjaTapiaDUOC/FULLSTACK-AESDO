package com.example.msdelivery.seeder;

import com.example.msdelivery.model.Delivery;
import com.example.msdelivery.repository.DeliveryRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * ===========================================================
 * SEEDER DE DELIVERY (DATAFAKER)
 * ===========================================================
 *
 * Al levantar el microservicio, esta clase verifica si la
 * tabla "deliveries" está vacía. Si lo está, genera datos de
 * prueba (pedidoId, dirección, repartidor y estado) utilizando
 * la librería Datafaker, para facilitar las pruebas manuales
 * y la demostración de los endpoints.
 *
 * Si ya existen registros, no hace nada (evita duplicar datos
 * en cada reinicio del servicio).
 */

@Component
public class DeliverySeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DeliverySeeder.class);

    private static final int CANTIDAD_DELIVERIES = 15;

    private static final List<String> ESTADOS = List.of("PENDIENTE", "EN_CAMINO", "ENTREGADO");

    private final DeliveryRepository deliveryRepository;
    private final Faker faker = new Faker(new Locale("es"));

    public DeliverySeeder(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    public void run(String... args) {

        if (deliveryRepository.count() > 0) {
            logger.info("La tabla de deliveries ya contiene datos. Se omite la carga de datos falsos.");
            return;
        }

        logger.info("Generando {} deliveries de prueba con Datafaker...", CANTIDAD_DELIVERIES);

        for (int i = 0; i < CANTIDAD_DELIVERIES; i++) {

            Long pedidoId = (long) (i + 1);
            String direccionEntrega = faker.address().fullAddress();
            String repartidor = faker.name().fullName();
            String estado = ESTADOS.get(faker.random().nextInt(ESTADOS.size()));

            Delivery delivery = new Delivery();
            delivery.setPedidoId(pedidoId);
            delivery.setDireccionEntrega(direccionEntrega);
            delivery.setRepartidor(repartidor);
            delivery.setEstado(estado);

            deliveryRepository.save(delivery);
        }

        logger.info("Carga de deliveries de prueba finalizada.");
    }

}

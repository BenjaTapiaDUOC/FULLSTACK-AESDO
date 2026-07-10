package com.tuapp.mspagos.seeder;

import com.tuapp.mspagos.model.Pago;
import com.tuapp.mspagos.repository.PagoRepository;
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
 * SEEDER DE PAGOS (DATAFAKER)
 * ===========================================================
 *
 * Al levantar el microservicio, esta clase verifica si la
 * tabla "pagos" está vacía. Si lo está, genera datos de
 * prueba utilizando la librería Datafaker, para facilitar las
 * pruebas manuales y la demostración de los endpoints.
 *
 * Si ya existen registros, no hace nada (evita duplicar datos
 * en cada reinicio del servicio).
 */

@Component
public class PagoSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PagoSeeder.class);

    private static final int CANTIDAD_PAGOS = 15;

    private static final List<String> METODOS_PAGO = List.of("TARJETA", "EFECTIVO", "TRANSFERENCIA");

    private static final List<String> ESTADOS = List.of("PENDIENTE", "APROBADO", "RECHAZADO");

    private final PagoRepository pagoRepository;
    private final Faker faker = new Faker(new Locale("es"));

    public PagoSeeder(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Override
    public void run(String... args) {

        if (pagoRepository.count() > 0) {
            logger.info("La tabla de pagos ya contiene datos. Se omite la carga de datos falsos.");
            return;
        }

        logger.info("Generando {} pagos de prueba con Datafaker...", CANTIDAD_PAGOS);

        for (int i = 0; i < CANTIDAD_PAGOS; i++) {

            Long pedidoId = (long) (i + 1);
            Double monto = faker.number().randomDouble(2, 5000, 150000);
            String metodoPago = METODOS_PAGO.get(faker.random().nextInt(METODOS_PAGO.size()));
            String estado = ESTADOS.get(faker.random().nextInt(ESTADOS.size()));
            LocalDateTime fechaPago = LocalDateTime.now().minusDays(faker.number().numberBetween(0, 30));

            Pago pago = new Pago();
            pago.setPedidoId(pedidoId);
            pago.setMonto(monto);
            pago.setMetodoPago(metodoPago);
            pago.setEstado(estado);
            pago.setFechaPago(fechaPago);

            pagoRepository.save(pago);
        }

        logger.info("Carga de pagos de prueba finalizada.");
    }

}

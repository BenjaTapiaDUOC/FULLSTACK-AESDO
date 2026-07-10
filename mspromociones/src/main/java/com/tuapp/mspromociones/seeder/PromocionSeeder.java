package com.tuapp.mspromociones.seeder;

import com.tuapp.mspromociones.model.Promocion;
import com.tuapp.mspromociones.repository.PromocionRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Locale;

/**
 * ===========================================================
 * SEEDER DE PROMOCIONES (DATAFAKER)
 * ===========================================================
 *
 * Al levantar el microservicio, esta clase verifica si la
 * tabla "promociones" está vacía. Si lo está, genera datos de
 * prueba utilizando la librería Datafaker, para facilitar las
 * pruebas manuales y la demostración de los endpoints.
 *
 * Si ya existen registros, no hace nada (evita duplicar datos
 * en cada reinicio del servicio).
 */

@Component
public class PromocionSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(PromocionSeeder.class);

    private static final int CANTIDAD_PROMOCIONES = 12;

    private final PromocionRepository promocionRepository;
    private final Faker faker = new Faker(new Locale("es"));

    public PromocionSeeder(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    @Override
    public void run(String... args) {

        if (promocionRepository.count() > 0) {
            logger.info("La tabla de promociones ya contiene datos. Se omite la carga de datos falsos.");
            return;
        }

        logger.info("Generando {} promociones de prueba con Datafaker...", CANTIDAD_PROMOCIONES);

        for (int i = 0; i < CANTIDAD_PROMOCIONES; i++) {

            String codigo = (faker.letterify("????") + faker.numerify("####")).toUpperCase();
            Double porcentajeDescuento = (double) faker.number().numberBetween(5, 51);
            LocalDate fechaInicio = LocalDate.now().minusDays(faker.number().numberBetween(0, 15));
            LocalDate fechaFin = fechaInicio.plusDays(faker.number().numberBetween(15, 60));
            Boolean activo = faker.bool().bool();

            Promocion promocion = new Promocion();
            promocion.setCodigo(codigo);
            promocion.setPorcentajeDescuento(porcentajeDescuento);
            promocion.setFechaInicio(fechaInicio);
            promocion.setFechaFin(fechaFin);
            promocion.setActivo(activo);

            promocionRepository.save(promocion);
        }

        logger.info("Carga de promociones de prueba finalizada.");
    }

}

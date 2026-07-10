package com.tuapp.msrepartidores.seeder;

import com.tuapp.msrepartidores.model.EstadoRepartidor;
import com.tuapp.msrepartidores.model.Repartidor;
import com.tuapp.msrepartidores.repository.RepartidorRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * ===========================================================
 * SEEDER DE REPARTIDORES (DATAFAKER)
 * ===========================================================
 *
 * Al levantar el microservicio, esta clase verifica si la
 * tabla "repartidores" está vacía. Si lo está, genera datos
 * de prueba utilizando la librería Datafaker, para facilitar
 * las pruebas manuales y la demostración de los endpoints.
 *
 * Si ya existen registros, no hace nada (evita duplicar datos
 * en cada reinicio del servicio).
 */

@Component
public class RepartidorSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RepartidorSeeder.class);

    private static final int CANTIDAD_REPARTIDORES = 12;

    private static final List<String> VEHICULOS = List.of("Moto", "Bicicleta", "Auto", "Camioneta");

    private static final List<EstadoRepartidor> ESTADOS = List.of(EstadoRepartidor.values());

    private final RepartidorRepository repartidorRepository;
    private final Faker faker = new Faker(new Locale("es"));

    public RepartidorSeeder(RepartidorRepository repartidorRepository) {
        this.repartidorRepository = repartidorRepository;
    }

    @Override
    public void run(String... args) {

        if (repartidorRepository.count() > 0) {
            logger.info("La tabla de repartidores ya contiene datos. Se omite la carga de datos falsos.");
            return;
        }

        logger.info("Generando {} repartidores de prueba con Datafaker...", CANTIDAD_REPARTIDORES);

        for (int i = 0; i < CANTIDAD_REPARTIDORES; i++) {

            String nombre = faker.name().fullName();
            String vehiculo = VEHICULOS.get(faker.random().nextInt(VEHICULOS.size()));
            EstadoRepartidor estado = ESTADOS.get(faker.random().nextInt(ESTADOS.size()));

            Repartidor repartidor = new Repartidor();
            repartidor.setNombre(nombre);
            repartidor.setVehiculo(vehiculo);
            repartidor.setEstado(estado);

            repartidorRepository.save(repartidor);
        }

        logger.info("Carga de repartidores de prueba finalizada.");
    }

}

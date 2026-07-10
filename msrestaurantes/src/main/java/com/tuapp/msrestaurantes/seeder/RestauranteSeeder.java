package com.tuapp.msrestaurantes.seeder;

import com.tuapp.msrestaurantes.model.Restaurante;
import com.tuapp.msrestaurantes.repository.RestauranteRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * ===========================================================
 * SEEDER DE RESTAURANTES (DATAFAKER)
 * ===========================================================
 *
 * Al levantar el microservicio, esta clase verifica si la
 * tabla "restaurantes" está vacía. Si lo está, genera datos
 * de prueba utilizando la librería Datafaker, para facilitar
 * las pruebas manuales y la demostración de los endpoints.
 *
 * Si ya existen registros, no hace nada (evita duplicar datos
 * en cada reinicio del servicio).
 */

@Component
public class RestauranteSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RestauranteSeeder.class);

    private static final int CANTIDAD_RESTAURANTES = 8;

    private static final List<String> CATEGORIAS = List.of(
            "Italiana", "Comida rápida", "Vegana", "Sushi", "Parrilla", "Vegetariana"
    );

    private final RestauranteRepository restauranteRepository;
    private final Faker faker = new Faker(new Locale("es"));

    public RestauranteSeeder(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    @Override
    public void run(String... args) {

        if (restauranteRepository.count() > 0) {
            logger.info("La tabla de restaurantes ya contiene datos. Se omite la carga de datos falsos.");
            return;
        }

        logger.info("Generando {} restaurantes de prueba con Datafaker...", CANTIDAD_RESTAURANTES);

        for (int i = 0; i < CANTIDAD_RESTAURANTES; i++) {

            String nombre = faker.company().name() + " " + (i + 1);
            String direccion = faker.address().fullAddress();
            String categoria = CATEGORIAS.get(faker.random().nextInt(CATEGORIAS.size()));
            String horario = "09:00 - 22:00";
            Boolean activo = faker.random().nextInt(10) > 1;

            Restaurante restaurante = new Restaurante();
            restaurante.setNombre(nombre);
            restaurante.setDireccion(direccion);
            restaurante.setCategoria(categoria);
            restaurante.setHorario(horario);
            restaurante.setActivo(activo);

            restauranteRepository.save(restaurante);
        }

        logger.info("Carga de restaurantes de prueba finalizada.");
    }

}

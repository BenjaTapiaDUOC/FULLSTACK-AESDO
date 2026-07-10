package com.tuapp.msproductos.seeder;

import com.tuapp.msproductos.model.Producto;
import com.tuapp.msproductos.repository.ProductoRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * ===========================================================
 * SEEDER DE PRODUCTOS (DATAFAKER)
 * ===========================================================
 *
 * Al levantar el microservicio, esta clase verifica si la
 * tabla "productos" está vacía. Si lo está, genera datos de
 * prueba utilizando la librería Datafaker, para facilitar las
 * pruebas manuales y la demostración de los endpoints.
 *
 * Si ya existen registros, no hace nada (evita duplicar datos
 * en cada reinicio del servicio).
 */

@Component
public class ProductoSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ProductoSeeder.class);

    private static final int CANTIDAD_PRODUCTOS = 20;

    private static final List<String> CATEGORIAS = List.of(
            "Comida rápida", "Bebidas", "Postres", "Comida saludable", "Snacks"
    );

    private final ProductoRepository productoRepository;
    private final Faker faker = new Faker(new Locale("es"));

    public ProductoSeeder(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {

        if (productoRepository.count() > 0) {
            logger.info("La tabla de productos ya contiene datos. Se omite la carga de datos falsos.");
            return;
        }

        logger.info("Generando {} productos de prueba con Datafaker...", CANTIDAD_PRODUCTOS);

        for (int i = 0; i < CANTIDAD_PRODUCTOS; i++) {

            String nombre = faker.food().dish() + " " + (i + 1);
            Double precio = faker.number().randomDouble(0, 1500, 25000);
            String categoria = CATEGORIAS.get(faker.random().nextInt(CATEGORIAS.size()));
            Long restauranteId = (long) faker.number().numberBetween(1, 8);

            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setPrecio(precio);
            producto.setCategoria(categoria);
            producto.setRestauranteId(restauranteId);

            productoRepository.save(producto);
        }

        logger.info("Carga de productos de prueba finalizada.");
    }

}

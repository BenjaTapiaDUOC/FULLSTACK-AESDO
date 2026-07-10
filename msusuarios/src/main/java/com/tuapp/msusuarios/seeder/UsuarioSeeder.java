package com.tuapp.msusuarios.seeder;

import com.tuapp.msusuarios.model.Usuario;
import com.tuapp.msusuarios.repository.UsuarioRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * ===========================================================
 * SEEDER DE USUARIOS (DATAFAKER)
 * ===========================================================
 *
 * Al levantar el microservicio, esta clase verifica si la
 * tabla "usuarios" está vacía. Si lo está, genera datos de
 * prueba (nombre, email y password) utilizando la librería
 * Datafaker, para facilitar las pruebas manuales y la
 * demostración de los endpoints sin tener que cargar los
 * datos a mano.
 *
 * Si ya existen registros, no hace nada (evita duplicar
 * datos en cada reinicio del servicio).
 */

@Component
public class UsuarioSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioSeeder.class);

    private static final int CANTIDAD_USUARIOS = 15;

    private final UsuarioRepository usuarioRepository;
    private final Faker faker = new Faker(new Locale("es"));

    public UsuarioSeeder(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {

        if (usuarioRepository.count() > 0) {
            logger.info("La tabla de usuarios ya contiene datos. Se omite la carga de datos falsos.");
            return;
        }

        logger.info("Generando {} usuarios de prueba con Datafaker...", CANTIDAD_USUARIOS);

        for (int i = 0; i < CANTIDAD_USUARIOS; i++) {

            String nombre = faker.name().fullName();
            String email = faker.internet().emailAddress(
                    faker.name().username() + i
            );
            String password = faker.internet().password(8, 16, true, true, true);

            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setPassword(password);

            usuarioRepository.save(usuario);
        }

        logger.info("Carga de usuarios de prueba finalizada.");
    }

}

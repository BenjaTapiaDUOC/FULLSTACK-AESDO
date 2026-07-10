package com.tuapp.msautenticacion.seeder;

import com.tuapp.msautenticacion.model.Rol;
import com.tuapp.msautenticacion.model.Usuario;
import com.tuapp.msautenticacion.repository.RolRepository;
import com.tuapp.msautenticacion.repository.UsuarioRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * ===========================================================
 * SEEDER DE USUARIOS DE AUTENTICACIÓN (DATAFAKER)
 * ===========================================================
 *
 * Al levantar el microservicio, esta clase verifica si la
 * tabla "usuarios_auth" está vacía. Si lo está, genera datos
 * de prueba (nombre, email, password y rol) utilizando la
 * librería Datafaker, para facilitar las pruebas manuales de
 * login sin tener que registrar usuarios a mano.
 *
 * Se ejecuta después de RolInitializer (@Order superior) para
 * asegurar que los roles base ya existan; de todas formas, si
 * por algún motivo un rol no existe, este seeder lo crea de
 * forma defensiva antes de asignarlo.
 *
 * Si ya existen registros, no hace nada (evita duplicar datos
 * en cada reinicio del servicio).
 */

@Component
@Order(2)
public class UsuarioAuthSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioAuthSeeder.class);

    private static final int CANTIDAD_USUARIOS = 15;

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final Faker faker = new Faker(new Locale("es"));

    public UsuarioAuthSeeder(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) {

        if (usuarioRepository.count() > 0) {
            logger.info("La tabla de usuarios de autenticación ya contiene datos. Se omite la carga de datos falsos.");
            return;
        }

        logger.info("Generando {} usuarios de autenticación de prueba con Datafaker...", CANTIDAD_USUARIOS);

        List<String> nombresRoles = List.of("ADMIN", "CLIENTE", "REPARTIDOR");

        for (int i = 0; i < CANTIDAD_USUARIOS; i++) {

            String nombreRol = nombresRoles.get(faker.random().nextInt(nombresRoles.size()));
            Rol rol = obtenerORolCrear(nombreRol);

            String nombre = faker.name().fullName();
            String email = faker.internet().emailAddress(
                    faker.name().username() + i
            );
            String password = faker.internet().password(8, 16, true, true, true);
            Long usuarioId = (long) (i + 1);

            Usuario usuario = new Usuario();
            usuario.setUsuarioId(usuarioId);
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setPassword(password);
            usuario.setRol(rol);

            usuarioRepository.save(usuario);
        }

        logger.info("Carga de usuarios de autenticación de prueba finalizada.");
    }

    /**
     * Obtiene un rol por su nombre. Si no existe (por ejemplo,
     * porque RolInitializer aún no se ha ejecutado), lo crea
     * de forma defensiva para no romper la carga de datos.
     */
    private Rol obtenerORolCrear(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> rolRepository.save(new Rol(null, nombre)));
    }

}

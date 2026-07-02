package com.tuapp.msautenticacion.config;

import com.tuapp.msautenticacion.model.Rol;
import com.tuapp.msautenticacion.repository.RolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * ===========================================================
 * INICIALIZADOR DE ROLES
 * ===========================================================
 *
 * Al levantar el microservicio, esta clase verifica que
 * existan los 3 roles del dominio (ADMIN, CLIENTE,
 * REPARTIDOR). Si no existen, los crea automáticamente.
 *
 * De esta forma el endpoint de registro siempre tiene
 * roles válidos contra los cuales validar.
 */

@Component
public class RolInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RolInitializer.class);

    private final RolRepository rolRepository;

    public RolInitializer(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(String... args) {

        logger.info("Verificando existencia de roles base del sistema...");

        crearRolSiNoExiste("ADMIN");
        crearRolSiNoExiste("CLIENTE");
        crearRolSiNoExiste("REPARTIDOR");

    }

    private void crearRolSiNoExiste(String nombre) {

        if (!rolRepository.existsByNombre(nombre)) {

            rolRepository.save(new Rol(null, nombre));

            logger.info("Rol '{}' creado automáticamente.", nombre);

        }

    }

}

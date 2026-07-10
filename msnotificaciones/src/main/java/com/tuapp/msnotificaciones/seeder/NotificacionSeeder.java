package com.tuapp.msnotificaciones.seeder;

import com.tuapp.msnotificaciones.model.Notificacion;
import com.tuapp.msnotificaciones.repository.NotificacionRepository;
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
 * SEEDER DE NOTIFICACIONES (DATAFAKER)
 * ===========================================================
 *
 * Al levantar el microservicio, esta clase verifica si la
 * tabla "notificaciones" está vacía. Si lo está, genera datos
 * de prueba utilizando la librería Datafaker, para facilitar
 * las pruebas manuales y la demostración de los endpoints.
 *
 * Si ya existen registros, no hace nada (evita duplicar datos
 * en cada reinicio del servicio).
 */

@Component
public class NotificacionSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionSeeder.class);

    private static final int CANTIDAD_NOTIFICACIONES = 20;

    private static final List<String> TIPOS = List.of(
            "PAGO_APROBADO", "PEDIDO_ENVIADO", "DELIVERY_ASIGNADO", "PROMOCION_APLICADA"
    );

    private static final List<String> ORIGENES = List.of("PAGOS", "PEDIDOS", "DELIVERY");

    private final NotificacionRepository notificacionRepository;
    private final Faker faker = new Faker(new Locale("es"));

    public NotificacionSeeder(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Override
    public void run(String... args) {

        if (notificacionRepository.count() > 0) {
            logger.info("La tabla de notificaciones ya contiene datos. Se omite la carga de datos falsos.");
            return;
        }

        logger.info("Generando {} notificaciones de prueba con Datafaker...", CANTIDAD_NOTIFICACIONES);

        for (int i = 0; i < CANTIDAD_NOTIFICACIONES; i++) {

            Long usuarioId = (long) faker.number().numberBetween(1, 15);
            String tipo = TIPOS.get(faker.random().nextInt(TIPOS.size()));
            String mensaje = faker.lorem().sentence(10);
            String origen = ORIGENES.get(faker.random().nextInt(ORIGENES.size()));
            Long referenciaId = (long) faker.number().numberBetween(1, 100);
            LocalDateTime fechaEnvio = LocalDateTime.now().minusDays(faker.number().numberBetween(0, 30));
            boolean leida = faker.bool().bool();

            Notificacion notificacion = new Notificacion();
            notificacion.setUsuarioId(usuarioId);
            notificacion.setTipo(tipo);
            notificacion.setMensaje(mensaje);
            notificacion.setOrigen(origen);
            notificacion.setReferenciaId(referenciaId);
            notificacion.setFechaEnvio(fechaEnvio);
            notificacion.setLeida(leida);

            notificacionRepository.save(notificacion);
        }

        logger.info("Carga de notificaciones de prueba finalizada.");
    }

}

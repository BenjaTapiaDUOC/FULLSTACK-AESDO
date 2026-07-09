package com.tuapp.mseureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * ===========================================================
 * MSEUREKA - SERVIDOR DE DESCUBRIMIENTO DE SERVICIOS
 * ===========================================================
 *
 * Este microservicio actúa como el "directorio telefónico"
 * del sistema: cada uno de los 10 microservicios de negocio
 * y el msgateway se registran aquí al arrancar, indicando su
 * nombre lógico (spring.application.name), host y puerto.
 *
 * Gracias a esto, el gateway y los clientes WebClient ya no
 * necesitan conocer direcciones fijas (localhost:8081, etc.);
 * les basta con el nombre lógico del servicio (ej: msusuarios)
 * y Eureka resuelve dónde está corriendo realmente.
 *
 * Panel web disponible en: http://localhost:8761
 */

@SpringBootApplication
@EnableEurekaServer
public class MseurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MseurekaApplication.class, args);
    }

}

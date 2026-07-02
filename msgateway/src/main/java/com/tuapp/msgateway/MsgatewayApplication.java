package com.tuapp.msgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ===========================================================
 * API GATEWAY - PUNTO DE ENTRADA UNICO DEL SISTEMA
 * ===========================================================
 *
 * Este microservicio no tiene logica de negocio propia: su unica
 * responsabilidad es recibir todas las peticiones del cliente y
 * enrutarlas (proxy) hacia el microservicio correspondiente,
 * segun las reglas definidas en application.yml.
 *
 * Ventajas de tener este Gateway delante de los 10 microservicios:
 *  - El cliente (frontend, Postman, etc.) solo necesita conocer
 *    UNA URL base (la del gateway), no los 10 puertos distintos.
 *  - Permite centralizar filtros comunes (logging, headers, etc.).
 *  - Facilita el despliegue: si un microservicio cambia de puerto
 *    o de host, solo se actualiza la ruta en el gateway.
 */
@SpringBootApplication
public class MsgatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsgatewayApplication.class, args);
    }

}

# 🍔 [Nombre del proyecto] — Arquitectura de Microservicios

## Descripción del proyecto
[Contexto/dominio: delivery de comida, gestión de pedidos, pagos, etc.
2-4 líneas explicando el problema que resuelve el sistema.]

## Integrantes
- Nombre Apellido — [rol/microservicios a cargo]
- Nombre Apellido — [rol/microservicios a cargo]

## Arquitectura general
[Diagrama simple o descripción: cliente → Gateway (puerto 8080) → 10 microservicios]

## Microservicios implementados

| Microservicio | Puerto | Responsabilidad | Ruta Gateway |
|---|---|---|---|
| msusuarios | 8081 | Gestión de usuarios | /usuarios/** |
| msproductos | 8082 | Catálogo de productos | /productos/** |
| mspedidos | 8083 | Gestión de pedidos y detalles | /pedidos/** |
| mspagos | 8084 | Procesamiento de pagos | /pagos/** |
| msdelivery | 8085 | Seguimiento de entregas | /delivery/** |
| msrestaurantes | 8086 | Gestión de restaurantes | /restaurantes/** |
| mspromociones | 8087 | Promociones y descuentos | /promociones/** |
| msrepartidores | 8088 | Gestión de repartidores | /repartidores/** |
| msnotificaciones | 8089 | Envío de notificaciones | /notificaciones/** |
| msautenticacion | 8090 | Login/registro/JWT | /autenticacion/** |
| *msgateway* | *8080* | Punto de entrada único | — |

## Documentación Swagger
- msusuarios: http://localhost:8081/swagger-ui/index.html
- mspedidos: http://localhost:8083/swagger-ui/index.html
- [... resto de servicios]
- (Cuando esté el despliegue remoto: URLs de Railway/Render aquí)

## Cómo ejecutar en local
1. Clonar el repo
2. Requisitos: JDK 17+, Maven, MySQL local (Laragon u otro)
3. Por cada microservicio: mvn spring-boot:run (o correr el jar)
4. El gateway queda expuesto en http://localhost:8080

## Cómo ejecutar en remoto
[Completar cuando esté el despliegue: variables de entorno necesarias,
URL pública, perfil prod activado con SPRING_PROFILES_ACTIVE=prod]

## Pruebas unitarias
- Framework: JUnit 5 + Mockito
- Cobertura: Jacoco configurado en cada microservicio (mvn test genera reporte en /target/site/jacoco)
- Cobertura actual: [completar con el % real una vez corran el reporte]

## Gestión del proyecto
- Tablero Trello: [link]
- Commits distribuidos entre ambos integrantes (ver historial en main)

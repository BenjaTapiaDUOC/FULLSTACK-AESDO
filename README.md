# 🍔 AESDO — Arquitectura de Microservicios

## Descripción del proyecto
Este proyecto es una plataforma de **delivery de comida** basada en arquitectura de 
microservicios, que permite a un usuario registrarse, autenticarse, explorar restaurantes 
y sus productos, generar pedidos, procesar pagos, aplicar promociones y hacer seguimiento 
de la entrega hasta que un repartidor la complete.

El sistema está compuesto por 10 microservicios independientes (usuarios, autenticación, 
restaurantes, productos, pedidos, pagos, promociones, delivery, repartidores y 
notificaciones), comunicados entre sí vía REST y centralizados a través de un API Gateway 
(msgateway), que expone un único punto de entrada al cliente.

Cada microservicio gestiona su propio dominio y valida información contra otros servicios 
cuando corresponde (por ejemplo, mspedidos valida la existencia del usuario contra 
msusuarios, y msproductos valida que el restaurante asociado exista y esté activo antes 
de crear un producto).

## Integrantes
- Benjamin Tapia — [rol/microservicios a cargo]
- Esteban Ramirez — [rol/microservicios a cargo]

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
- msproductos: http://localhost:8082/swagger-ui/index.html
- mspedidos: http://localhost:8083/swagger-ui/index.html
- mspagos: http://localhost:8084/swagger-ui/index.html
- msdelivery: http://localhost:8085/swagger-ui/index.html
- msrestaurantes: http://localhost:8086/swagger-ui/index.html
- mspromociones: http://localhost:8087/swagger-ui/index.html
- msrepartidores: http://localhost:8088/swagger-ui/index.html
- msnotificaciones: http://localhost:8089/swagger-ui/index.html
- msautenticacion: http://localhost:8090/swagger-ui/index.html
- (Cuando esté el despliegue remoto: URLs de Railway/Render aquí)

## Cómo ejecutar en local
1. Clonar el repo
2. Requisitos: JDK 21, Maven, MySQL local (Laragon u otro)
3. Por cada microservicio: mvn spring-boot:run (o correr el jar)
4. El gateway queda expuesto en http://localhost:8080

## Cómo ejecutar en remoto
[Completar cuando esté el despliegue: variables de entorno necesarias,
URL pública, perfil prod activado con SPRING_PROFILES_ACTIVE=prod]

## Pruebas unitarias
- Framework: JUnit 5 + Mockito
- Cobertura: Jacoco configurado en cada microservicio (mvn test genera reporte en /target/site/jacoco)
- Cobertura actual: 
[MSUSUARIOS: TEST 96%]
[MSPRODUCTOS: TEST 84%]
[MSPEDIDOS: TEST 99%]
[MSPAGOS: TEST 95%]
[MSDELIVERY: TEST 99%]
[MSRESTAURANTES: TEST ]
[MSPROMOCIONES: TEST 98%]
[MSREPARTIDORES: TEST 92%]
[MSNOTIFICACIONES: TEST 99%]
[MSAUTENTICACION: TEST AUTENTICACION 94%]

## Gestión del proyecto
- Tablero Trello: [https://trello.com/b/DWTveL6A/fullstack]
- Commits distribuidos entre ambos integrantes (ver historial en main)

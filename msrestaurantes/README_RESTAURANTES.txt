/**
 * ===========================================================
 * MICROSERVICIO: MSRESTAURANTES
 * ===========================================================
 *
 * Puerto:
 * 8086
 *
 * Base de datos (MySQL / Laragon):
 * msrestaurantes
 *
 * Antes de levantar el servicio, crea la base de datos:
 * CREATE DATABASE msrestaurantes;
 *
 * spring.jpa.hibernate.ddl-auto=update se encarga de crear
 * la tabla "restaurantes" automáticamente.
 */


/**
 * ===========================================================
 * ENDPOINT: CREAR RESTAURANTE
 * ===========================================================
 *
 * Descripción:
 * Permite registrar un nuevo restaurante en la base de datos.
 *
 * Método HTTP:
 * POST
 *
 * URL:
 * http://localhost:8086/restaurantes
 *
 * Cómo probar en Postman:
 *
 * 1. Seleccionar el método POST.
 * 2. Escribir la URL:
 *    http://localhost:8086/restaurantes
 * 3. Ir a la pestaña Body.
 * 4. Seleccionar raw.
 * 5. Elegir formato JSON.
 * 6. Enviar un JSON como el siguiente:
 *
 * {
 *   "nombre": "La Trattoria",
 *   "direccion": "Av. Siempre Viva 123",
 *   "categoria": "Italiana",
 *   "horario": "09:00 - 22:00",
 *   "activo": true
 * }
 *
 * Nota:
 * El campo "activo" es opcional. Si no se envía, el
 * restaurante se crea como ACTIVO (true) por defecto.
 *
 * Respuesta esperada:
 * HTTP 201 Created
 *
 * Si el nombre ya existe:
 * HTTP 400 Bad Request
 *
 * Si faltan datos obligatorios (nombre, dirección, categoría u horario):
 * HTTP 400 Bad Request
 */


/**
 * ===========================================================
 * ENDPOINT: LISTAR TODOS LOS RESTAURANTES
 * ===========================================================
 *
 * Descripción:
 * Devuelve el listado completo de restaurantes registrados.
 *
 * Método HTTP:
 * GET
 *
 * URL:
 * http://localhost:8086/restaurantes
 *
 * Cómo probar en Postman:
 *
 * 1. Seleccionar GET.
 * 2. Presionar Send.
 *
 * Respuesta esperada:
 * HTTP 200 OK
 */


/**
 * ===========================================================
 * ENDPOINT: LISTAR SOLO RESTAURANTES ACTIVOS
 * ===========================================================
 *
 * Descripción:
 * Devuelve únicamente los restaurantes cuyo estado es
 * ACTIVO (activo = true).
 *
 * Este endpoint está pensado para ser consumido por otros
 * microservicios (por ejemplo msproductos) que necesitan
 * trabajar solo con restaurantes disponibles.
 *
 * Método HTTP:
 * GET
 *
 * URL:
 * http://localhost:8086/restaurantes/activos
 *
 * Cómo probar en Postman:
 *
 * 1. Seleccionar GET.
 * 2. Presionar Send.
 *
 * Respuesta esperada:
 * HTTP 200 OK
 */


/**
 * ===========================================================
 * ENDPOINT: BUSCAR RESTAURANTE POR ID
 * ===========================================================
 *
 * Descripción:
 * Busca un restaurante utilizando su identificador.
 *
 * Es el endpoint clave que msproductos debe consumir vía
 * WebClient para validar que el restaurante exista y esté
 * activo antes de crear un producto.
 *
 * Método HTTP:
 * GET
 *
 * URL:
 * http://localhost:8086/restaurantes/1
 *
 * Cómo probar en Postman:
 *
 * 1. Seleccionar GET.
 * 2. Cambiar el número por un ID existente.
 * 3. Presionar Send.
 *
 * Respuesta esperada:
 * HTTP 200 OK
 *
 * {
 *   "id": 1,
 *   "nombre": "La Trattoria",
 *   "direccion": "Av. Siempre Viva 123",
 *   "categoria": "Italiana",
 *   "horario": "09:00 - 22:00",
 *   "activo": true
 * }
 *
 * Si el restaurante no existe:
 * HTTP 404 Not Found
 */


/**
 * ===========================================================
 * ENDPOINT: ACTUALIZAR RESTAURANTE
 * ===========================================================
 *
 * Descripción:
 * Actualiza la información de un restaurante existente.
 *
 * Método HTTP:
 * PUT
 *
 * URL:
 * http://localhost:8086/restaurantes/1
 *
 * Cómo probar en Postman:
 *
 * 1. Seleccionar PUT.
 * 2. Escribir la URL.
 * 3. Ir a Body.
 * 4. Seleccionar raw.
 * 5. Elegir JSON.
 * 6. Enviar:
 *
 * {
 *   "nombre": "La Trattoria",
 *   "direccion": "Av. Nueva 456",
 *   "categoria": "Italiana",
 *   "horario": "10:00 - 23:00",
 *   "activo": true
 * }
 *
 * Respuesta esperada:
 * HTTP 200 OK
 *
 * Si el restaurante no existe:
 * HTTP 404 Not Found
 *
 * Si el nombre pertenece a otro restaurante:
 * HTTP 400 Bad Request
 */


/**
 * ===========================================================
 * ENDPOINT: CAMBIAR ESTADO (ACTIVAR / DESACTIVAR)
 * ===========================================================
 *
 * Descripción:
 * Activa o desactiva un restaurante sin necesidad de
 * enviar todos sus datos nuevamente.
 *
 * Regla de negocio relacionada:
 * Un restaurante inactivo no debería poder recibir nuevos
 * productos. Esa validación se realiza en msproductos
 * consultando este microservicio.
 *
 * Método HTTP:
 * PATCH
 *
 * URL:
 * http://localhost:8086/restaurantes/1/estado
 *
 * Cómo probar en Postman:
 *
 * 1. Seleccionar PATCH.
 * 2. Escribir la URL.
 * 3. Ir a Body.
 * 4. Seleccionar raw.
 * 5. Elegir JSON.
 * 6. Enviar:
 *
 * {
 *   "activo": false
 * }
 *
 * Respuesta esperada:
 * HTTP 200 OK
 *
 * Si el restaurante no existe:
 * HTTP 404 Not Found
 *
 * Si no se envía el campo "activo":
 * HTTP 400 Bad Request
 */


/**
 * ===========================================================
 * ENDPOINT: ELIMINAR RESTAURANTE
 * ===========================================================
 *
 * Descripción:
 * Elimina un restaurante de la base de datos utilizando su ID.
 *
 * Método HTTP:
 * DELETE
 *
 * URL:
 * http://localhost:8086/restaurantes/1
 *
 * Cómo probar en Postman:
 *
 * 1. Seleccionar DELETE.
 * 2. Escribir la URL.
 * 3. Presionar Send.
 *
 * Respuesta esperada:
 * HTTP 204 No Content
 *
 * Si el restaurante no existe:
 * HTTP 404 Not Found
 */


/**
 * ===========================================================
 * RESUMEN DE ENDPOINTS
 * ===========================================================
 *
 * POST    http://localhost:8086/restaurantes            -> Crear restaurante
 * GET     http://localhost:8086/restaurantes            -> Listar todos
 * GET     http://localhost:8086/restaurantes/activos     -> Listar solo activos
 * GET     http://localhost:8086/restaurantes/{id}         -> Buscar por ID
 * PUT     http://localhost:8086/restaurantes/{id}         -> Actualizar
 * PATCH   http://localhost:8086/restaurantes/{id}/estado  -> Activar/Desactivar
 * DELETE  http://localhost:8086/restaurantes/{id}         -> Eliminar
 */


/**
 * ===========================================================
 * FORMATO DE ERRORES
 * ===========================================================
 *
 * Todas las respuestas de error siguen el mismo formato,
 * generado por el GlobalExceptionHandler:
 *
 * {
 *   "codigo": 404,
 *   "error": "NOT FOUND",
 *   "mensaje": "Restaurante no encontrado."
 * }
 *
 * Errores de validación (@NotBlank, @NotNull) devuelven
 * el nombre del campo junto con el mensaje correspondiente:
 *
 * {
 *   "nombre": "El nombre es obligatorio"
 * }
 */

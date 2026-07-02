/**
 * ===========================================================
 * MICROSERVICIO: MSAUTENTICACION
 * ===========================================================
 *
 * Puerto: 8090
 * Base URL: http://localhost:8090/autenticacion
 *
 * Este microservicio administra el login y el registro de
 * credenciales del sistema, generando un JWT que incluye el
 * rol del usuario (ADMIN, CLIENTE o REPARTIDOR).
 *
 * IMPORTANTE - DEPENDENCIA CON MSUSUARIOS:
 *
 * El endpoint de registro se comunica mediante WebClient con
 * el microservicio msusuarios (http://localhost:8081/usuarios)
 * para crear el usuario "maestro" (nombre, email, password).
 *
 * Por lo tanto, ANTES de probar /autenticacion/registro,
 * msusuarios debe estar levantado en el puerto 8081.
 *
 * Los demás endpoints (login, roles, usuarios/{id}, validar)
 * NO requieren que msusuarios esté encendido, ya que trabajan
 * con la información guardada localmente en msautenticacion.
 *
 * Antes de levantar el proyecto, crear la base de datos:
 *
 * CREATE DATABASE msautenticacion;
 *
 * Al iniciar la aplicación se crean automáticamente los 3
 * roles del sistema (ADMIN, CLIENTE, REPARTIDOR) si no
 * existen todavía.
 */


/**
 * ===========================================================
 * ENDPOINT: REGISTRAR USUARIO
 * ===========================================================
 *
 * Descripción:
 * Registra un nuevo usuario: valida el rol, crea el usuario
 * maestro en msusuarios (vía WebClient) y guarda localmente
 * la credencial (email/password) junto al rol asignado.
 *
 * Método HTTP:
 * POST
 *
 * URL:
 * http://localhost:8090/autenticacion/registro
 *
 * Cómo probar en Postman:
 *
 * 1. Seleccionar el método POST.
 * 2. Escribir la URL:
 *    http://localhost:8090/autenticacion/registro
 * 3. Ir a la pestaña Body.
 * 4. Seleccionar raw.
 * 5. Elegir formato JSON.
 * 6. Enviar un JSON como el siguiente:
 *
 * {
 *   "nombre": "Benjamin",
 *   "email": "benjamin@gmail.com",
 *   "password": "12345678",
 *   "rol": "CLIENTE"
 * }
 *
 * Roles permitidos: ADMIN, CLIENTE, REPARTIDOR
 *
 * Respuesta esperada:
 * HTTP 201 Created
 *
 * {
 *   "id": 1,
 *   "usuarioId": 1,
 *   "nombre": "Benjamin",
 *   "email": "benjamin@gmail.com",
 *   "rol": "CLIENTE"
 * }
 *
 * Si el rol enviado no existe:
 * HTTP 400 Bad Request
 *
 * Si el correo ya existe (localmente o en msusuarios):
 * HTTP 400 Bad Request
 *
 * Si faltan datos:
 * HTTP 400 Bad Request
 *
 * Si msusuarios no está levantado:
 * HTTP 503 Service Unavailable
 */


/**
 * ===========================================================
 * ENDPOINT: LOGIN
 * ===========================================================
 *
 * Descripción:
 * Valida las credenciales del usuario y, si son correctas,
 * genera un token JWT que incluye el rol asignado.
 *
 * Método HTTP:
 * POST
 *
 * URL:
 * http://localhost:8090/autenticacion/login
 *
 * Cómo probar en Postman:
 *
 * 1. Seleccionar el método POST.
 * 2. Escribir la URL:
 *    http://localhost:8090/autenticacion/login
 * 3. Ir a la pestaña Body.
 * 4. Seleccionar raw.
 * 5. Elegir formato JSON.
 * 6. Enviar un JSON como el siguiente:
 *
 * {
 *   "email": "benjamin@gmail.com",
 *   "password": "12345678"
 * }
 *
 * Respuesta esperada:
 * HTTP 200 OK
 *
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9...",
 *   "tipo": "Bearer",
 *   "expiraEnMs": 3600000,
 *   "usuario": {
 *     "id": 1,
 *     "usuarioId": 1,
 *     "nombre": "Benjamin",
 *     "email": "benjamin@gmail.com",
 *     "rol": "CLIENTE"
 *   }
 * }
 *
 * Si el correo no existe o la contraseña es incorrecta:
 * HTTP 401 Unauthorized
 *
 * Si faltan datos:
 * HTTP 400 Bad Request
 */


/**
 * ===========================================================
 * ENDPOINT: LISTAR ROLES
 * ===========================================================
 *
 * Descripción:
 * Devuelve el listado de roles disponibles en el sistema.
 *
 * Método HTTP:
 * GET
 *
 * URL:
 * http://localhost:8090/autenticacion/roles
 *
 * Cómo probar en Postman:
 *
 * 1. Seleccionar GET.
 * 2. Presionar Send.
 *
 * Respuesta esperada:
 * HTTP 200 OK
 *
 * [
 *   { "id": 1, "nombre": "ADMIN" },
 *   { "id": 2, "nombre": "CLIENTE" },
 *   { "id": 3, "nombre": "REPARTIDOR" }
 * ]
 */


/**
 * ===========================================================
 * ENDPOINT: BUSCAR USUARIO DE AUTENTICACIÓN POR ID
 * ===========================================================
 *
 * Descripción:
 * Busca un registro de autenticación (credencial + rol)
 * utilizando su identificador local.
 *
 * Método HTTP:
 * GET
 *
 * URL:
 * http://localhost:8090/autenticacion/usuarios/1
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
 * Si el usuario no existe:
 * HTTP 404 Not Found
 */


/**
 * ===========================================================
 * ENDPOINT: VALIDAR TOKEN
 * ===========================================================
 *
 * Descripción:
 * Valida un token JWT y devuelve la información contenida
 * en él (id, usuarioId, nombre, email, rol, expiración).
 *
 * Útil para que otros microservicios (o el propio equipo
 * durante la defensa) verifiquen que un token es válido.
 *
 * Método HTTP:
 * GET
 *
 * URL:
 * http://localhost:8090/autenticacion/validar
 *
 * Cómo probar en Postman:
 *
 * 1. Seleccionar GET.
 * 2. Escribir la URL:
 *    http://localhost:8090/autenticacion/validar
 * 3. Ir a la pestaña Headers.
 * 4. Agregar el header:
 *    Key:   Authorization
 *    Value: Bearer {token obtenido en el login}
 * 5. Presionar Send.
 *
 * Respuesta esperada:
 * HTTP 200 OK
 *
 * {
 *   "valido": true,
 *   "id": 1,
 *   "usuarioId": 1,
 *   "nombre": "Benjamin",
 *   "email": "benjamin@gmail.com",
 *   "rol": "CLIENTE",
 *   "expira": "..."
 * }
 *
 * Si no se envía el header Authorization, el token está mal
 * formado o expiró:
 * HTTP 401 Unauthorized
 */

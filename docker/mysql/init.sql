-- ===========================================================
-- Script de inicializacion de MySQL para Docker Compose
-- Crea las bases de datos de cada uno de los 10 microservicios.
-- MySQL ejecuta automaticamente todo archivo .sql colocado en
-- /docker-entrypoint-initdb.d la primera vez que se crea el
-- volumen de datos del contenedor.
-- ===========================================================

CREATE DATABASE IF NOT EXISTS msusuarios       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS msproductos      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS mspedidos        CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS mspagos          CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS msdelivery       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS msrestaurantes   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS mspromociones    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS msrepartidores   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS msnotificaciones CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS msautenticacion  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

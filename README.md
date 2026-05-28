# \# TEA Gestión - Proyecto DAM

# 

# Aplicación web de gestión interna para una asociación TEA, desarrollada como proyecto final del ciclo formativo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma.

# 

# El proyecto está compuesto por:

# 

# \* Backend REST con Spring Boot.

# \* Frontend con Next.js, React, TypeScript y Tailwind CSS.

# \* Base de datos MariaDB.

# \* Autenticación mediante JWT.

# \* Control de acceso por roles.

# \* Documentación de API mediante Swagger/OpenAPI.

# \* Entorno de base de datos con Docker.

# 

# \## Estructura del proyecto

# 

# ```txt

# TFG\_DAM/

# ├── backend/

# ├── frontend/

# ├── docker-compose.yaml

# ├── .env.example

# └── README.md

# ```

# 

# \## Tecnologías principales

# 

# \### Backend

# 

# \* Java

# \* Spring Boot

# \* Spring Security

# \* JWT

# \* Spring Data JPA

# \* MariaDB

# \* Swagger/OpenAPI

# \* Maven

# 

# \### Frontend

# 

# \* Next.js

# \* React

# \* TypeScript

# \* Tailwind CSS

# \* ESLint

# 

# \### Infraestructura

# 

# \* Docker

# \* Docker Compose

# \* GitHub

# 

# \## Requisitos previos

# 

# Para ejecutar el proyecto en local es necesario tener instalado:

# 

# \* Java 21 o superior

# \* Maven Wrapper incluido en el proyecto

# \* Node.js

# \* npm

# \* Docker Desktop

# \* Git

# 

# \## Configuración

# 

# El proyecto incluye un archivo de ejemplo para variables de entorno:

# 

# ```txt

# .env.example

# ```

# 

# Para el entorno local de desarrollo se puede usar directamente este archivo con Docker Compose.

# 

# El frontend utiliza el archivo:

# 

# ```txt

# frontend/.env.local

# ```

# 

# con la siguiente variable:

# 

# ```env

# NEXT\_PUBLIC\_API\_URL=http://localhost:8080/api/v1

# ```

# 

# También se incluye un archivo de ejemplo:

# 

# ```txt

# frontend/.env.local.example

# ```

# 

# \## Arranque de la base de datos

# 

# Desde la raíz del proyecto:

# 

# ```bash

# docker compose --env-file .env.example up -d db

# ```

# 

# Comprobar que MariaDB está funcionando:

# 

# ```bash

# docker ps

# ```

# 

# Debe aparecer un contenedor de MariaDB en estado `healthy` y con el puerto `3306` publicado.

# 

# \## Arranque del backend

# 

# Desde la carpeta `backend`:

# 

# ```bash

# cd backend

# ./mvnw spring-boot:run

# ```

# 

# El backend queda disponible en:

# 

# ```txt

# http://localhost:8080

# ```

# 

# La documentación Swagger/OpenAPI se puede consultar en:

# 

# ```txt

# http://localhost:8080/swagger-ui/index.html

# ```

# 

# \## Arranque del frontend

# 

# Desde la carpeta `frontend`:

# 

# ```bash

# cd frontend

# npm install

# npm run dev

# ```

# 

# El frontend queda disponible en:

# 

# ```txt

# http://localhost:3000

# ```

# 

# \## Usuario de prueba

# 

# El entorno de desarrollo crea un usuario administrador inicial:

# 

# ```txt

# Email: admin@teagestion.local

# Contraseña: Admin1234

# Rol: ADMIN

# ```

# 

# \## Funcionalidades implementadas

# 

# \* Login con JWT.

# \* Dashboard de administración.

# \* Menú de navegación principal.

# \* Listado de socios.

# \* CRUD de socios.

# \* Listado de participantes.

# \* Listado de actividades.

# \* Listado de servicios.

# \* Listado de trabajadores.

# \* Datos demo en entorno de desarrollo.

# \* API documentada con Swagger.

# \* Seguridad por roles.

# 

# \## Roles previstos

# 

# La aplicación contempla distintos perfiles de usuario:

# 

# \* ADMIN

# \* ADMINISTRATIVA

# \* TRABAJADOR

# \* VOLUNTARIO

# \* SOCIO

# 

# Los permisos se gestionan desde la configuración de Spring Security mediante JWT y control de acceso por rutas.

# 

# \## Endpoints principales

# 

# Algunos endpoints principales del backend son:

# 

# ```txt

# POST /api/v1/auth/login

# GET  /api/v1/socios

# POST /api/v1/socios

# PUT  /api/v1/socios/{id}

# DELETE /api/v1/socios/{id}

# 

# GET /api/v1/participantes

# GET /api/v1/actividades

# GET /api/v1/servicios

# GET /api/v1/trabajadores

# ```

# 

# \## Comprobaciones del backend

# 

# Desde la carpeta `backend`:

# 

# ```bash

# cd backend

# ./mvnw clean test

# ```

# 

# Resultado esperado:

# 

# ```txt

# BUILD SUCCESS

# ```

# 

# \## Comprobaciones del frontend

# 

# Desde la carpeta `frontend`:

# 

# ```bash

# cd frontend

# npm run lint

# npm run build

# ```

# 

# Resultado esperado:

# 

# ```txt

# Compiled successfully

# ```

# 

# \## Flujo recomendado de ejecución local

# 

# Abrir tres terminales.

# 

# \### Terminal 1: base de datos

# 

# ```bash

# cd /h/TFG\_DAM/repo/TFG\_DAM

# docker compose --env-file .env.example up -d db

# ```

# 

# \### Terminal 2: backend

# 

# ```bash

# cd /h/TFG\_DAM/repo/TFG\_DAM/backend

# ./mvnw spring-boot:run

# ```

# 

# \### Terminal 3: frontend

# 

# ```bash

# cd /h/TFG\_DAM/repo/TFG\_DAM/frontend

# npm run dev

# ```

# 

# Después acceder a:

# 

# ```txt

# http://localhost:3000

# ```

# 

# \## Datos demo

# 

# En perfil `dev`, la aplicación carga datos iniciales para facilitar la demostración funcional:

# 

# \* Socios

# \* Participantes

# \* Actividades

# \* Servicios

# \* Trabajadores

# \* Usuario administrador inicial

# 

# Estos datos se cargan únicamente si no existen registros previos, para evitar duplicados en cada arranque.

# 

# \## Entrega

# 

# Para la entrega del proyecto no deben incluirse carpetas generadas ni dependencias instaladas.

# 

# No incluir:

# 

# ```txt

# node\_modules/

# .next/

# target/

# build/

# dist/

# .env

# .env.local

# backups de WordPress

# volcados .sql

# archivos comprimidos innecesarios

# ```

# 

# Sí incluir:

# 

# ```txt

# backend/

# frontend/

# docker-compose.yaml

# .env.example

# README.md

# colecciones Postman si forman parte de la documentación

# documentación técnica del proyecto

# ```

# 

# \## Estado actual del proyecto

# 

# El proyecto dispone de una base funcional completa:

# 

# \* Backend Spring Boot operativo.

# \* Base de datos MariaDB en Docker.

# \* Autenticación JWT funcionando.

# \* Swagger disponible.

# \* Frontend Next.js conectado al backend.

# \* Dashboard de administración.

# \* CRUD funcional de socios.

# \* Listados principales cargando desde la API.

# \* Datos demo para presentación.

# 

# \## Líneas futuras

# 

# Algunas mejoras previstas son:

# 

# \* CRUD completo de actividades.

# \* CRUD completo de servicios.

# \* Gestión avanzada de participantes.

# \* Gestión de usuarios y roles desde el frontend.

# \* Inscripción de socios o participantes en actividades.

# \* Asignación de servicios por trabajadores cualificados.

# \* Integración final con la web WordPress mediante subdominio o ruta `/intranet`.

# \* Mejora de accesibilidad y experiencia de usuario.

# \* Preparación de despliegue completo en servidor.




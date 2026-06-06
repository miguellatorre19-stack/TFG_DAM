# TEA Gestion - Proyecto DAM

Aplicacion web de gestion interna para una asociacion TEA. El proyecto esta organizado como monorepo de trabajo con backend Spring Boot, frontend Next.js y base de datos MariaDB en Docker.

## Estado Actual

Implementado actualmente:

- Backend REST con Spring Boot.
- Frontend con Next.js, React y TypeScript.
- Base de datos MariaDB mediante Docker Compose.
- Autenticacion con JWT.
- Control de acceso por roles en backend.
- Swagger/OpenAPI para documentar la API.
- Datos demo en perfiles de desarrollo.
- CRUD backend de socios, participantes, actividades, servicios y trabajadores.
- Inscripciones a actividades en backend.
- Frontend con login, dashboard y navegacion.
- Frontend con CRUD completo de socios.
- Frontend con listados de participantes, actividades, servicios y trabajadores.

Pendiente o parcial:

- CRUD frontend completo de participantes, actividades, servicios y trabajadores.
- Inscripcion a actividades desde frontend.
- Gestion de usuarios y roles desde panel de administracion.
- Zona privada diferenciada para socios/participantes.
- Modulo de noticias/comunicados.
- Dashboard con metricas reales.
- Integracion con WordPress.

## Estructura

```txt
TFG_DAM/
+-- backend/
+-- frontend/
+-- docs/
+-- docker-compose-dev.yaml
+-- docker-compose-prod.yaml
+-- .env.example
+-- README.md
```

## Puertos Y Perfiles

Convencion actual:

```txt
Frontend local:        http://localhost:3000
Backend local:         http://localhost:8080
API base:              http://localhost:8080/api/v1
Swagger:               http://localhost:8080/swagger-ui/index.html
MariaDB desde el host: localhost:3308
MariaDB en Docker:     db:3306
```

Perfiles Spring:

```txt
dev-local   -> backend ejecutado en local contra MariaDB en localhost:3308
dev-docker  -> backend ejecutado dentro de Docker contra MariaDB en db:3306
test        -> tests con H2 en memoria
```

## Requisitos

- Java 21 o superior.
- Maven o Maven Wrapper.
- Node.js y npm.
- Docker Desktop.
- Git.

Nota: si `mvnw.cmd` falla con `"powershell" no se reconoce`, usa Maven instalado directamente.

## Configuracion

Copia el archivo de ejemplo:

```cmd
copy .env.example .env
```

Variables principales:

```env
SPRING_PROFILES_ACTIVE=dev-local
SERVER_PORT=8080
FRONTEND_URL=http://localhost:3000

DB_NAME=association
DB_USER=association_user
DB_PASSWORD=association_password
DB_ROOT_PASSWORD=root_password
DB_HOST=127.0.0.1
DB_PORT=3308
DB_HOST_PORT=3308
DB_CONTAINER_PORT=3306

NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

## Arranque Local

Usa tres terminales.

### Terminal 1: Base De Datos

Desde la raiz del proyecto:

```cmd
docker compose --env-file .env -f docker-compose-dev.yaml up -d db
```

Comprueba que esta sana:

```cmd
docker ps
```

Debe aparecer:

```txt
association-dev-db   healthy   0.0.0.0:3308->3306/tcp
```

Si tienes un contenedor antiguo usando el mismo puerto, paralo primero:

```cmd
docker rm -f asociation-dev-db
```

### Terminal 2: Backend

Desde la raiz del proyecto:

```cmd
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev-local
```

Si `mvn` no esta en PATH pero tienes Maven instalado en `C:\maven`:

```cmd
cd backend
"C:\maven\apache-maven-3.9.11\bin\mvn.cmd" spring-boot:run -Dspring-boot.run.profiles=dev-local
```

El backend quedara disponible en:

```txt
http://localhost:8080
```

Swagger:

```txt
http://localhost:8080/swagger-ui/index.html
```

### Terminal 3: Frontend

Desde la raiz del proyecto:

```cmd
cd frontend
npm install
npm run dev
```

El frontend quedara disponible en:

```txt
http://localhost:3000
```

## Usuario Demo

Con perfil `dev-local` o `dev-docker`, se crea un usuario administrador inicial:

```txt
Email: admin@teagestion.local
Password: Admin1234
Rol: ADMIN
```

## Tests

Backend:

```cmd
mvn -f backend\pom.xml test
```

Alternativa con Maven absoluto:

```cmd
"C:\maven\apache-maven-3.9.11\bin\mvn.cmd" -f backend\pom.xml test
```

Frontend:

```cmd
cd frontend
npm run lint
npm run build
```

## Docker Completo

Para levantar base de datos y backend dentro de Docker:

```cmd
docker compose --env-file .env -f docker-compose-prod.yaml up --build
```

En este modo el backend usa el perfil `dev-docker` y conecta contra la base de datos por el host interno `db:3306`.

## Notas De Desarrollo

- Para desarrollo habitual, usa `docker-compose-dev.yaml` solo para la base de datos y ejecuta backend/frontend en local.
- No uses `localhost:3306` para la base de datos desde el backend local. El puerto del host es `3308`.
- Dentro de Docker, el backend no debe usar `localhost` para MariaDB; debe usar `db:3306`.
- CORS se configura con `FRONTEND_URL`.
- La API consumida por el frontend se configura con `NEXT_PUBLIC_API_URL`.

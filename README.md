# TEA Gestion - Proyecto DAM

Aplicacion web de gestion interna y area privada para una asociacion TEA. El proyecto se trabaja como monorepo con backend Spring Boot, frontend Next.js y base de datos MariaDB en Docker.

## Convencion Unica De Arranque

La configuracion queda simplificada a una sola forma oficial:

```txt
Frontend local:        http://localhost:3000
Backend local:         http://localhost:8080
API base:              http://localhost:8080/api/v1
Swagger:               http://localhost:8080/swagger-ui/index.html
MariaDB desde el host: 127.0.0.1:3308
MariaDB en Docker:     3306 dentro del contenedor
```

No se usan perfiles manuales de Spring para desarrollo local. No uses `dev`, `dev-local` ni `dev-docker`.

## Estructura

```txt
TFG_DAM/
+-- backend/
+-- frontend/
+-- docs/
+-- scripts/
+-- docker-compose.yaml
+-- .env
+-- README.md
```

## Requisitos

- Java 21 o superior.
- Maven o Maven Wrapper.
- Node.js y npm.
- Docker Desktop.
- Git.

Nota: si `mvnw.cmd` falla con `"powershell" no se reconoce`, usa Maven instalado directamente.

## Configuracion

Hay un unico `.env` en la raiz del proyecto:

```env
SERVER_PORT=8080
FRONTEND_URL=http://localhost:3000

DB_HOST=127.0.0.1
DB_PORT=3308
DB_NAME=association
DB_USER=association_user
DB_PASSWORD=association_password
DB_ROOT_PASSWORD=root_password

SEED_DATA_ENABLED=true
JWT_SECRET=dev_secret_key_for_tfg_dam_project_2026_change_me_please
JWT_EXPIRATION_MS=86400000

NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

La aplicacion tambien tiene valores por defecto equivalentes en `backend/src/main/resources/application.properties`, por lo que el backend local puede arrancar aunque no cargues el `.env` manualmente.

## Arranque Local

Usa tres terminales.

### Terminal 1: Base De Datos

Desde la raiz del proyecto:

```powershell
docker compose --env-file .env up -d db
```

Comprueba que esta sana:

```powershell
docker compose --env-file .env ps
```

Debe aparecer el contenedor:

```txt
association-db   healthy   0.0.0.0:3308->3306/tcp
```

Si queda un contenedor antiguo ocupando el puerto, paralo antes:

```powershell
docker rm -f association-dev-db
```

### Terminal 2: Backend

Desde la raiz del proyecto:

```powershell
& "C:\maven\apache-maven-3.9.11\bin\mvn.cmd" -f backend\pom.xml spring-boot:run
```

Alternativa si `mvn` esta en PATH:

```powershell
mvn -f backend\pom.xml spring-boot:run
```

Si venias de una configuracion antigua, limpia primero:

```powershell
& "C:\maven\apache-maven-3.9.11\bin\mvn.cmd" -f backend\pom.xml clean
```

### Terminal 3: Frontend

Desde la raiz del proyecto:

```powershell
cd frontend
npm install
npm run dev
```

## Usuario Demo

Si `SEED_DATA_ENABLED=true`, se crea un usuario administrador inicial:

```txt
Email: admin@teagestion.local
Password: Admin1234
Rol: ADMIN
```

## Funcionalidad Actual

- Backend REST con Spring Boot.
- Frontend con Next.js, React y TypeScript.
- Base de datos MariaDB mediante Docker Compose.
- Autenticacion con JWT.
- Control de acceso por roles.
- CRUD backend de socios, participantes, actividades, servicios y trabajadores.
- Creacion automatica de usuarios para socios, participantes y trabajadores.
- Area privada para socios/participantes.
- Consulta e inscripcion a actividades y servicios.
- Endpoint `GET /api/v1/me` para resolver el perfil autenticado.
- Swagger/OpenAPI para documentar la API.

## Tests

Backend:

```powershell
& "C:\maven\apache-maven-3.9.11\bin\mvn.cmd" -f backend\pom.xml test
```

Frontend:

```powershell
cd frontend
npm run lint
npm run build
```

## Reglas Para Evitar Confusiones

No usar:

```txt
SPRING_PROFILES_ACTIVE
DEV_DB_HOST
DEV_DB_PORT
DEV_DB_NAME
DEV_DB_USER
DEV_DB_PASSWORD
application-dev.properties
docker-compose-dev.yaml
docker-compose-prod.yaml
backend/.env
```

Usar solo:

```txt
.env
docker-compose.yaml
backend/src/main/resources/application.properties
```

Si aparece un error JDBC apuntando a `localhost:3306`, casi seguro queda una variable o archivo antiguo. Limpia `target` con Maven y comprueba que no exista `SPRING_PROFILES_ACTIVE` en la terminal.

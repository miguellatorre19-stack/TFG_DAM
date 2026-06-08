# Backend - TEA Gestion

API REST para la gestion interna y el area privada de una asociacion TEA.

## Stack

- Java 21
- Spring Boot
- Spring Security + JWT
- Spring Data JPA / Hibernate
- MariaDB
- Swagger / OpenAPI

## Requisitos

- Java 21 o superior
- Maven o Maven Wrapper
- MariaDB disponible

## Configuracion

Variables principales:

```env
SERVER_PORT=8080
FRONTEND_URL=http://localhost:3000

DB_HOST=127.0.0.1
DB_PORT=3308
DB_NAME=association
DB_USER=association_user
DB_PASSWORD=association_password

SEED_DATA_ENABLED=true
JWT_SECRET=dev_secret_key_for_tfg_dam_project_2026_change_me_please
JWT_EXPIRATION_MS=86400000
```

Tambien existen valores por defecto en `src/main/resources/application.properties`.

## Arranque

Con Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Con Maven instalado:

```powershell
mvn spring-boot:run
```

## Endpoints utiles

- API base: `http://localhost:8080/api/v1`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

## Tests

```powershell
mvn test
```

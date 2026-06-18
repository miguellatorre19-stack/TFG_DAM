# Integración con WordPress

## Enfoque

La integración con WordPress se plantea como una integración por separación de responsabilidades.

WordPress se utiliza como web pública de la asociación, destinada a mostrar información institucional, noticias, contacto y contenidos informativos.

La aplicación desarrollada en este proyecto funciona como área privada de gestión interna, separada técnicamente de WordPress y accesible mediante un enlace desde la web pública.

## Arquitectura propuesta

- WordPress: portal público de la asociación.
- Next.js: frontend del área privada.
- Spring Boot: API REST segura.
- MariaDB: base de datos de la aplicación de gestión.
- JWT: sistema de autenticación del área privada.

## Backup del sitio WordPress

El sitio WordPress existente se conserva como copia de seguridad en la ruta local:

```txt
H:/COPIA SEGURIDAD WEB ASPERGER

Dicha carpeta contiene el backup del sitio y la base de datos asociada.

Este backup no se incluye en el repositorio Git por contener archivos de terceros, datos propios del sitio y posibles credenciales o información sensible.

Flujo de usuario
El usuario accede a la web pública desarrollada en WordPress.
Desde el menú principal selecciona “Área privada” o “Intranet”.
WordPress redirige al frontend desarrollado en Next.js.
El usuario inicia sesión en la aplicación mediante JWT.
Según su rol, accede a las funcionalidades internas permitidas.
Equivalencia en desarrollo local

Durante el desarrollo local, la integración se representa de la siguiente forma:

WordPress restaurado en local o sitio público existente
        ↓
Enlace "Área privada"
        ↓
http://localhost:3000
        ↓
Frontend Next.js
        ↓
http://localhost:8080/api/v1
        ↓
Backend Spring Boot
        ↓
MariaDB en Docker

Posible despliegue en producción

En un entorno real se recomienda separar la web pública, la intranet y la API mediante dominios o subdominios:

https://dominio.com
        Web pública WordPress

https://intranet.dominio.com
        Frontend Next.js

https://api.dominio.com
        Backend Spring Boot

Ventajas de la integración separada
Separación clara entre portal público y aplicación de gestión.
Mayor seguridad, al no mezclar usuarios de WordPress con usuarios internos de la aplicación.
Mejor mantenibilidad del sistema.
Despliegue independiente de cada parte.
Posibilidad de escalar la intranet sin afectar a la web pública.
Evita modificar internamente WordPress mediante plugins complejos o código externo.
Decisiones técnicas

No se integra Next.js como plugin de WordPress.

No se comparte la base de datos de WordPress con la aplicación Spring Boot.

No se reutiliza el sistema de usuarios de WordPress para la intranet.

La autenticación del área privada se gestiona mediante JWT desde el backend Spring Boot.

Estado actual en el proyecto

La aplicación de gestión dispone actualmente de:

Login con JWT.
Dashboard de administración.
Listados de socios, participantes, actividades, servicios y trabajadores.
CRUD funcional de socios.
Datos demo para presentación.
Backend documentado con Swagger.
Base de datos MariaDB mediante Docker.

La integración con WordPress queda definida como acceso externo mediante enlace desde la web pública hacia el área privada de gestión.

Trabajo pendiente

Como mejora futura se plantea:

Restaurar el backup WordPress en un entorno local o servidor.
Añadir en el menú principal de WordPress un enlace llamado “Área privada” o “Intranet”.
Configurar dicho enlace hacia el frontend Next.js.
En producción, publicar el frontend en un subdominio independiente.
Documentar capturas de la web pública y del acceso a la intranet en la memoria del proyecto.
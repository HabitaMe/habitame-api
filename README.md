# habitame-api

API REST de **HabitaMe**, una plataforma de alquiler de habitaciones compartidas con
moderación administrativa de cada anuncio.

[![CI](https://github.com/HabitaMe/habitame-api/actions/workflows/ci.yml/badge.svg)](https://github.com/HabitaMe/habitame-api/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-21-b07219)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-6DB33F)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

**Demo:** [habitame.xyz](https://habitame.xyz) · **Documentación de la API:**
[api.habitame.xyz/swagger-ui.html](https://api.habitame.xyz/swagger-ui.html)

---

## Qué es

A diferencia de un tablón de anuncios, en HabitaMe **ningún anuncio es visible al publicarse**.
Cada propiedad y cada habitación entra en una cola de revisión y solo pasa a ser pública cuando
un administrador la aprueba. Esa decisión de producto es la que condiciona buena parte del
diseño de esta API.

Este repositorio es el backend. La plataforma completa son cuatro piezas: esta API, una app
móvil en Flutter, un panel de administración en Angular y una landing con SSR, desplegadas
detrás de un mismo dominio.

- **80 endpoints** repartidos en 16 controladores, documentados con OpenAPI
- **14 tablas** en PostgreSQL, normalizadas a 3FN, versionadas con Flyway
- **Cobertura mínima del 70 %** verificada con JaCoCo en cada build

## Decisiones de ingeniería

Las cuatro cosas que me parecen más interesantes de este código:

### Sesiones sin estado con rotación de tokens

El login devuelve un *access token* (1 h) y un *refresh token* (24 h) firmados con HMAC-SHA256.
Los refresh tokens **se persisten en base de datos con un flag de revocación**, de modo que el
logout invalida la sesión de inmediato en lugar de esperar a que el token caduque — que es el
punto débil habitual de un esquema JWT puro. El cliente detecta un `401`, pide un token nuevo y
reintenta la petición sin que el resto de la aplicación se entere.

📁 [`auth/security/JwtProvider.java`](src/main/java/com/habitame/api/auth/security/JwtProvider.java) ·
[`auth/service/AuthService.java`](src/main/java/com/habitame/api/auth/service/AuthService.java)

### Moderación como máquina de estados

Toda propiedad y habitación nace en `IN_REVIEW`. Un administrador la aprueba (`ACTIVE`) o la
rechaza, y **el rechazo exige un comentario obligatorio** que el propietario ve en su app para
corregir y reenviar. El histórico de revisiones se guarda en tablas propias
(`property_reviews`, `room_reviews`), así que cada decisión queda auditada con su autor.

📁 [`propertyReview/`](src/main/java/com/habitame/api/propertyReview) ·
[`roomReview/`](src/main/java/com/habitame/api/roomReview)

### Filtros combinables con JPA Specification

Ciudad, precio, ocupantes máximos y ordenación se componen como **predicados independientes**
(`Specification.where(...).and(...)`) en lugar de concatenar JPQL a mano. Añadir un filtro nuevo
no toca los existentes ni altera el SQL que genera el resto de combinaciones.

📁 [`room/repository/RoomSpecification.java`](src/main/java/com/habitame/api/room/repository/RoomSpecification.java) ·
[`property/repository/PropertySpecification.java`](src/main/java/com/habitame/api/property/repository/PropertySpecification.java)

### Imágenes a WebP, con la invariante en la base de datos

Cada imagen se redimensiona con Thumbnailator y se convierte a WebP antes de guardarse, lo que
reduce el peso entre un 25 % y un 40 % frente a JPEG. La regla de "solo una imagen principal por
anuncio" **no se valida en el servicio, sino con un índice único parcial en PostgreSQL**
(`WHERE is_main = TRUE`): si un día otro proceso escribe en esa tabla, la invariante se sostiene
igual.

📁 [`media/service/ApacheImageStorageService.java`](src/main/java/com/habitame/api/media/service/ApacheImageStorageService.java) ·
[`db/migration/V1__init_schema.sql`](src/main/resources/db/migration/V1__init_schema.sql)

## Arquitectura

Organización **por feature, no por capa**: cada módulo agrupa su controlador, DTOs, entidad,
repositorio y servicio, en lugar de repartirlos por carpetas `controllers/`, `services/`…

```
com.habitame.api
├── auth/            autenticación, JWT, refresh tokens, configuración de seguridad
├── user/            perfiles y administración de cuentas
├── property/        anuncios de propiedades
├── room/            habitaciones dentro de una propiedad
├── propertyReview/  cola y resolución de moderación de propiedades
├── roomReview/      cola y resolución de moderación de habitaciones
├── propertyImage/   imágenes de propiedades
├── roomImage/       imágenes de habitaciones
├── media/           procesado y almacenamiento de ficheros
├── amenities/       catálogo de características (N:M con propiedades y habitaciones)
├── country/         ┐
├── province/        ├ jerarquía geográfica
├── city/            ┘
└── common/          configuración transversal y manejo global de errores
```

Los errores se normalizan en un único `GlobalExceptionHandler`, de forma que todas las
respuestas de error de la API comparten el mismo contrato.

📁 [`common/exception/`](src/main/java/com/habitame/api/common/exception)

## Cómo ejecutarlo en local

Necesitas **Java 21** y Docker.

```bash
# 1. Levantar PostgreSQL con las credenciales que espera el perfil dev
docker run -d --name habitame-db \
  -e POSTGRES_DB=habitame_db \
  -e POSTGRES_USER=habitame \
  -e POSTGRES_PASSWORD=habitame \
  -p 5432:5432 postgres:16

# 2. Arrancar la API (Flyway crea el esquema y carga datos de ejemplo)
./mvnw spring-boot:run
```

La API queda en `http://localhost:8080` y la documentación interactiva en
`http://localhost:8080/swagger-ui.html`.

El perfil `dev` es el activo por defecto y trae credenciales locales; **producción no lleva
ningún valor por defecto** y falla al arrancar si no encuentra `DB_URL`, `DB_USER`, `DB_PASS`
y `JWT_SECRET` en el entorno.

## Tests y calidad

```bash
./mvnw verify
```

Ese único comando encadena todo lo que ejecuta la CI:

| Fase | Qué comprueba |
|---|---|
| `validate` | Checkstyle sobre todo el código |
| `test` | Tests unitarios y *slice tests* de controlador con MockMvc |
| `verify` | Informe de JaCoCo y **umbral del 70 % de líneas cubiertas** |

El umbral excluye las clases sin lógica de negocio (entidades JPA, DTOs, enums y configuración),
para que la cifra mida lo que realmente puede romperse.

La CI se ejecuta en cada push y pull request a `main` y `develop` contra un PostgreSQL 16 real
—no una base de datos en memoria—, y además valida que la imagen de Docker construye.

## Stack

Java 21 · Spring Boot 3.3 · Spring Security · Spring Data JPA · PostgreSQL 16 · Flyway ·
JJWT · springdoc-openapi · Thumbnailator + webp-imageio · JUnit 5 · JaCoCo · Checkstyle ·
Docker · GitHub Actions

## Autoría

Desarrollado por **[Daniel Amores](https://github.com/danielamoresdev)** — API, base de datos,
panel de administración e infraestructura — junto a **Samuel Copa**, autor de la aplicación
Flutter.

Publicado bajo licencia [MIT](LICENSE).

# Streaming Music REST API

Plataforma de streaming de música estilo Spotify desarrollada con **Java 21** y **Spring Boot 3.x**.

## Requisitos

- Java 21+
- Maven 3.9+

## Stack Tecnológico

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.5.x |
| API | REST + Swagger/OpenAPI (springdoc) |
| Concurrencia | Virtual Threads + AtomicInteger |
| Almacenamiento | In-memory (ConcurrentHashMap + HashMap) |
| Tests | JUnit 5 + TestRestTemplate |
| Build | Maven |

## Arranque Rápido

```bash
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

## Swagger UI / OpenAPI

La API incluye documentación interactiva generada automáticamente con **springdoc-openapi** (implementación de OpenAPI 3.0 para Spring Boot). Cada endpoint está anotado con `@Tag`, `@Operation` y `@ApiResponse`, lo que permite explorar y probar la API desde el navegador sin herramientas externas.

### Dependencia

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.6</version>
</dependency>
```

### URLs de acceso

| URL | Descripción |
|---|---|
| `http://localhost:8080/swagger-ui.html` | Interfaz interactiva Swagger UI — probar endpoints desde el navegador |
| `http://localhost:8080/api-docs` | Especificación OpenAPI 3.0 en JSON — importable en Postman, curl, etc. |

### Anotaciones utilizadas

| Anotación | Dónde se usa | Propósito |
|---|---|---|
| `@Tag(name, description)` | Clase (controller) | Agrupa endpoints en una sección de Swagger UI |
| `@Operation(summary, description)` | Método (endpoint) | Describe qué hace el endpoint |
| `@ApiResponse(responseCode, description)` | Método | Documenta un código de respuesta HTTP (200, 404, 400) |
| `@ApiResponses({...})` | Método | Agrupa múltiples `@ApiResponse` para un mismo endpoint |
| `@Parameter(description, required, example)` | Parámetro de método | Documenta `@PathVariable`, `@RequestParam` |

### Ejemplo: cómo se ve un endpoint documentado en código

```java
@GetMapping("/buscar")
@Operation(summary = "Buscar canciones con filtros compuestos",
        description = "Filtra canciones por género, rating mínimo y duración máxima usando Streams API")
@ApiResponse(responseCode = "200", description = "Resultado del filtrado")
public ResponseEntity<List<CancionDTO>> buscar(
        @Parameter(description = "Género musical (ROCK, POP, JAZZ, ELECTRÓNICA, CLÁSICA)")
        @RequestParam(required = false) Genero genero,
        @Parameter(description = "Rating mínimo (0.0 - 5.0)")
        @RequestParam(required = false) Integer ratingMinimo,
        @Parameter(description = "Duración máxima en segundos")
        @RequestParam(required = false) Integer duracionMaxima) {
    return ResponseEntity.ok(cancionService.filtrar(genero, ratingMinimo, duracionMaxima));
}
```

En Swagger UI, esto se traduce en una sección desplegable con formulario de prueba, esquema de respuesta JSON y códigos de estado documentados.

### Tags de la API

| Tag | Controlador | Endpoints |
|---|---|---|
| **Canciones** | `CancionController` | 14 endpoints (CRUD, búsqueda, recomendaciones, estadísticas, playlist) |
| **Artistas** | `ArtistaController` | 2 endpoints (listar, obtener por ID) |
| **Álbumes** | `AlbumController` | 2 endpoints (listar, obtener por ID) |
| **Productoras** | `ProductoraController` | 2 endpoints (listar, obtener por ID) |

### Importar en Postman

Desde la URL `http://localhost:8080/api-docs` se puede importar la colección directamente en Postman (Import → Link → pegar la URL), generando automáticamente todos los requests con sus parámetros documentados.

## Endpoints Principales

### Canciones (`/api/canciones`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/canciones` | Listar todas las canciones |
| GET | `/api/canciones/{id}` | Obtener canción por UUID |
| GET | `/api/canciones/buscar?genero=X&ratingMinimo=X&duracionMaxima=X` | Búsqueda con filtros compuestos (Streams API) |
| POST | `/api/canciones/{id}/reproducir` | Registrar reproducción (AtomicInteger thread-safe) |
| GET | `/api/canciones/{id}/recomendaciones/genero` | Recomendaciones por género (Strategy) |
| GET | `/api/canciones/recomendaciones/top5?baseId=X` | Top 5 global (Strategy) |
| GET | `/api/canciones/{id}/recomendaciones/descubrimiento` | Descubrimiento (<1000 plays, <2 años, distinto género) |
| GET | `/api/canciones/top10` | Top 10 más reproducidas |
| GET | `/api/canciones/playlist?minutos=X` | Playlist por duración exacta (Knapsack recursivo) |
| GET | `/api/canciones/busqueda-binaria?titulo=X` | Búsqueda binaria por título |
| GET | `/api/canciones/ordenar?criterio=X` | Ordenamiento personalizado (Pattern Matching) |
| GET | `/api/canciones/estadisticas/promedio-por-genero` | Promedio rating por género |
| GET | `/api/canciones/estadisticas/artista-mas-popular` | Artista más popular (maxBy) |
| GET | `/api/canciones/estadisticas/distribucion-decadas` | Distribución por décadas |

### Artistas (`/api/artistas`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/artistas` | Listar artistas |
| GET | `/api/artistas/{id}` | Artista por UUID |
| POST | `/api/artistas` | Crear artista (201 Created) |
| PUT | `/api/artistas/{id}` | Actualizar artista |
| DELETE | `/api/artistas/{id}` | Eliminar artista (204 No Content) |

### Álbumes (`/api/albumes`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/albumes` | Listar álbumes |
| GET | `/api/albumes/{id}` | Álbum por UUID |
| POST | `/api/albumes` | Crear álbum (201 Created, valida artista y productora) |
| PUT | `/api/albumes/{id}` | Actualizar álbum |
| DELETE | `/api/albumes/{id}` | Eliminar álbum (204 No Content) |

### Productoras (`/api/productoras`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/productoras` | Listar productoras |
| GET | `/api/productoras/{id}` | Productora por UUID |
| POST | `/api/productoras` | Crear productora (201 Created) |
| PUT | `/api/productoras/{id}` | Actualizar productora |
| DELETE | `/api/productoras/{id}` | Eliminar productora (204 No Content) |

## Tests

```bash
# Todos los tests
mvn test

# Solo el test de concurrencia
mvn test -Dtest=ConcurrencyVirtualThreadsTest
```

## Análisis de Complejidad Big O

### Búsqueda Binaria por Título

**Complejidad: O(n log n + log n)**

- **O(n log n)**: ordenamiento previo del catálogo por título (Comparator natural)
- **O(log n)**: búsqueda binaria propiamente dicha, dividiendo el espacio de búsqueda a la mitad en cada iteración
- El factor dominante es el ordenamiento; si el catálogo ya está ordenado, se reduce a O(log n)

```
left = 0, right = n-1
while (left <= right):
    mid = (left + right) / 2
    if   titulos[mid] == buscado → encontrado
    elif titulos[mid] < buscado  → left = mid + 1
    else                         → right = mid - 1
```

### Playlist Automática (Mochila / Subset Sum)

**Complejidad: O(2^n) en el peor caso (backtracking recursivo sin poda)**

- Cada canción tiene 2 opciones: incluir o no incluir
- El árbol de decisión tiene 2^n hojas en el peor caso
- Sin poda, se exploran todas las combinaciones posibles

```
backtrack(canciones, index, remaining):
    si remaining == 0 → solución encontrada
    si remaining < 0 o index >= n → retroceder
    
    // Rama 1: incluir canción[index]
    backtrack(index + 1, remaining - duracion[index])
    
    // Rama 2: excluir canción[index]  
    backtrack(index + 1, remaining)
```

**Optimizaciones posibles (no implementadas para claridad educativa):**
- DP con memoización: O(n * W) donde W = duración objetivo en segundos
- Poda por ordenamiento: ordenar por duración y descartar si remaining < duración mínima restante
- Programación dinámica bottom-up: tabla de booleanos dp[i][w]

### Búsqueda Lineal con Predicados Múltiples

**Complejidad: O(n * p)** donde n = tamaño del catálogo, p = número de predicados

Cada canción se evalúa contra todos los predicados combinados con AND.

## Patrón de Diseño: Strategy

El patrón **Strategy** permite definir una familia de algoritmos, encapsular cada uno en su propia clase y hacerlos intercambiables en tiempo de ejecución. Se aplica al **sistema de recomendaciones de canciones**.

### ¿Por qué Strategy y no métodos en el Service?

| Criterio | Sin Strategy | Con Strategy |
|---|---|---|
| Agregar nuevo algoritmo | Modificar `CancionService` (viola OCP) | Crear nueva clase que implemente la interface |
| Testear algoritmo | Testear todo el service | Testear la estrategia de forma aislada |
| Reutilizar | No se puede sin copiar código | Cualquier service puede inyectar la estrategia |
| SRP | Service con lógica de negocio + recomendación | Service solo orquesta; la lógica vive en su estrategia |

### Estructura

```
service/estrategia/
├── EstrategiaRecomendacion.java        ← Interface (Strategy)
├── RecomendacionPorGenero.java         ← Estrategia concreta #1
├── RecomendacionPorPopularidad.java    ← Estrategia concreta #2
└── RecomendacionDescubrimiento.java    ← Estrategia concreta #3
```

### Interface Strategy

```java
@FunctionalInterface
public interface EstrategiaRecomendacion {
    List<Cancion> recomendar(List<Cancion> catalogo, Cancion base);
}
```

Define el contrato común: dado un catálogo completo y una canción base, retorna una lista de canciones recomendadas.

### Estrategias Concretas

| Estrategia | Lógica del algoritmo | Endpoint |
|---|---|---|
| **RecomendacionPorGenero** | Filtra canciones del mismo género que la base, ordena por reproducciones descendente, retorna hasta 10 | `GET /api/canciones/{id}/recomendaciones/genero` |
| **RecomendacionPorPopularidad** | Toma todo el catálogo (excluyendo la base), ordena por reproducciones descendente, retorna el top 5 | `GET /api/canciones/recomendaciones/top5?baseId=X` |
| **RecomendacionDescubrimiento** | Filtra canciones con <1000 reproducciones, <2 años de antigüedad y género diferente a la base, retorna hasta 10 | `GET /api/canciones/{id}/recomendaciones/descubrimiento` |

### Flujo de una Petición

```
HTTP Request (GET /api/canciones/{id}/recomendaciones/genero)
    ↓
CancionController.recomendarPorGenero(id)
    ↓
CancionService.recomendarPorGenero(id)
    ↓  Busca la canción base y obtiene el catálogo completo
    ↓
RecomendacionPorGenero.recomendar(catalogo, base)  ← Se ejecuta la estrategia
    ↓  Filtra por género, ordena, limita
    ↓
CancionService convierte entidades a DTOs
    ↓
Response JSON
```

### Contexto: CancionService

`CancionService` actúa como el **Context** del patrón. Inyecta las 3 estrategias concretas por constructor y delega a cada una:

```java
private final RecomendacionPorGenero recomendacionPorGenero;
private final RecomendacionPorPopularidad recomendacionPorPopularidad;
private final RecomendacionDescubrimiento recomendacionDescubrimiento;

public List<CancionDTO> recomendarPorGenero(UUID cancionId) {
    Cancion base = cancionRepository.findById(cancionId)...;
    List<Cancion> catalogo = cancionRepository.findAll();
    return recomendacionPorGenero.recomendar(catalogo, base).stream()
            .map(this::toDTO).toList();
}
```

Para agregar una nueva estrategia (ej: `RecomendacionPorArtista`), solo se crea una nueva clase que implemente `EstrategiaRecomendacion`, se inyecta en el service y se expone un nuevo endpoint — sin modificar la lógica existente.

## Postman

Importa `postman_collection.json` en Postman para probar todos los endpoints. Contiene 20 requests de prueba.

## Arquitectura de Agentes

El desarrollo sigue un pipeline de 5 agentes especializados:

```
@software-architect → @backend-developer → @test-writer → @code-reviewer → @documentation-writer
```

Archivos de configuración en `.opencode/agent/`.

## Estructura del Proyecto

```
src/main/java/com/streaming/music/
├── controller/       # REST Controllers con Swagger
├── service/          # Lógica de negocio
│   └── estrategia/   # Patrón Strategy (3 implementaciones)
├── repository/       # Almacenamiento en memoria
├── model/            # Entidades (clases regulares + Genero enum)
├── dto/              # Data Transfer Objects
├── exception/        # @RestControllerAdvice global
├── config/           # DataInitializer (seed data)
└── StreamingMusicApplication.java
```

## Features de Java 21 Utilizadas

- **Virtual Threads**: `Executors.newVirtualThreadPerTaskExecutor()` en tests de concurrencia
- **Records**: `CancionDTO`, `ArtistaDTO`, `AlbumDTO`, `ProductoraDTO` son Records inmutables (DTOs)
- **Classes**: `Cancion`, `Artista`, `Album`, `Productora` son clases regulares con campos `final` + getters (entidades mutables/preparadas para JPA)
- **Switch Expressions**: `switch` mejorado en `BusquedaService.ordenarPersonalizado()`
- **AtomicInteger**: Contador thread-safe de reproducciones en `Cancion`
- **Streams API**: `groupingBy`, `averagingDouble`, `maxBy`, filtros compuestos
- **String.formatted()**: Formatos de string modernos en `toString()`

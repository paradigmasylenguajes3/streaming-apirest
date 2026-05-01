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
| Almacenamiento | In-memory (ConcurrentHashMap) |
| Tests | JUnit 5 + TestRestTemplate |
| Build | Maven |

## Arranque Rápido

```bash
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

## Swagger UI

Accede a la documentación interactiva en:

**http://localhost:8080/swagger-ui.html**

Todos los endpoints están documentados con `@Tag`, `@Operation` y `@ApiResponse`.

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

### Álbumes (`/api/albumes`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/albumes` | Listar álbumes |
| GET | `/api/albumes/{id}` | Álbum por UUID |

### Productoras (`/api/productoras`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/productoras` | Listar productoras |
| GET | `/api/productoras/{id}` | Productora por UUID |

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

## Postman

Importa `postman_collection.json` en Postman para probar todos los endpoints. Contiene 18 requests de prueba.

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
├── model/            # Entidades (Records + Cancion)
├── dto/              # Data Transfer Objects
├── exception/        # @RestControllerAdvice global
├── config/           # DataInitializer (seed data)
└── StreamingMusicApplication.java
```

## Features de Java 21 Utilizadas

- **Virtual Threads**: `Executors.newVirtualThreadPerTaskExecutor()` en tests de concurrencia
- **Records**: `CancionDTO`, `ArtistaDTO`, `AlbumDTO`, `ProductoraDTO` son Records inmutables (DTOs)
- **Classes**: `Cancion`, `Artista`, `Album`, `Productora` son clases regulares con campos `final` + getters (entidades mutables/preparadas para JPA)
- **Pattern Matching**: `switch` con patrones en `BusquedaService.ordenarPersonalizado()`
- **AtomicInteger**: Contador thread-safe de reproducciones en `Cancion`
- **Streams API**: `groupingBy`, `averagingDouble`, `maxBy`, filtros compuestos
- **String.formatted()**: Formatos de string modernos en `toString()`

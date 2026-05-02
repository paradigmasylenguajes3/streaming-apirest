# Guía de Java Streams API, Optional, Predicate y Comparator

> **Referencia principal**: [java.util.stream — Java SE 21](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html)

Esta guía documenta las APIs funcionales de Java 21 utilizadas en el proyecto **Streaming Music REST API**, con ejemplos reales del código y explicaciones detalladas.

---

## Tabla de Contenidos

1. [Java Streams API](#1-java-streams-api)
   - [Conceptos fundamentales](#conceptos-fundamentales)
   - [Operaciones intermedias (lazy)](#operaciones-intermedias-lazy)
   - [Operaciones terminales (eager)](#operaciones-terminales-eager)
   - [Collectors — reducciones mutables](#collectors--reducciones-mutables)
   - [Streams primitivos](#streams-primitivos)
2. [Optional](#2-optional)
3. [Predicate](#3-predicate)
4. [Comparator](#4-comparator)
5. [Patrones avanzados](#5-patrones-avanzados)
6. [Referencias](#6-referencias)

---

## 1. Java Streams API

> **Documentación oficial**: [Package java.util.stream](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html)

Un **Stream** es una secuencia de elementos que soporta operaciones agregadas secuenciales y paralelas. A diferencia de las colecciones, un Stream:

- **No almacena datos**: opera sobre una fuente (Collection, array, generador).
- **Es funcional por naturaleza**: no modifica la fuente, produce un nuevo resultado.
- **Es lazy**: las operaciones intermedias no se ejecutan hasta que se invoca una operación terminal.
- **Es consumible**: un Stream solo puede recorrerse una vez.

### Conceptos fundamentales

```
Fuente (Collection, array)  →  Operaciones intermedias (lazy)  →  Operación terminal (eager)
```

**Ejemplo del proyecto** — Pipeline completo en `ProcesamientoCancionService`:

```java
// Fuente: Collection → stream()
// Intermedias: filter(), sorted()
// Terminal: toList()
return cancionRepository.findAll().stream()
        .filter(c -> genero == null || c.getGenero() == genero)
        .filter(c -> ratingMinimo == null || c.getRating() >= ratingMinimo)
        .filter(c -> duracionMaxima == null || c.getDuracionSegundos() <= duracionMaxima)
        .toList();
```

> **Nota**: Las operaciones intermedias (`filter`, `sorted`, `map`) son **lazy** — no se ejecutan hasta que la operación terminal (`toList`, `collect`, `forEach`) se invoca. Esto permite optimizaciones como fusionar múltiples `filter` en una sola pasada.

---

### Operaciones intermedias (lazy)

Las operaciones intermedias retornan un nuevo `Stream` y no se ejecutan hasta que una operación terminal las dispara.

#### `filter(Predicate)` — Filtrado

> **Ref.**: [Stream.filter(Predicate)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#filter(java.util.function.Predicate))

Retorna un stream con los elementos que cumplen el predicado.

```java
// Ejemplo del proyecto — RecomendacionDescubrimiento.java
return catalogo.stream()
        .filter(c -> !c.equals(base))                          // excluir canción base
        .filter(c -> c.getReproducciones() < 1000)            // pocas reproducciones
        .filter(c -> ChronoUnit.YEARS.between(                // lanzada hace menos de 2 años
                c.getFechaLanzamiento(), LocalDate.now()) < 2)
        .filter(c -> c.getGenero() != base.getGenero())       // género diferente
        .limit(10)
        .toList();
```

**Patrón**: Se pueden encadenar múltiples `filter` para construir condiciones compuestas. Cada `filter` aplica un predicado independiente, y el Stream resultante contiene solo los elementos que cumplen **todos** los predicados.

#### `map(Function)` — Transformación

> **Ref.**: [Stream.map(Function)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#map(java.util.function.Function))

Aplica una función a cada elemento y retorna un stream con los resultados.

```java
// Ejemplo del proyecto — CancionService.toDTO()
private CancionDTO toDTO(Cancion cancion) {
    ArtistaDTO artistaDTO = ArtistaDTO.from(cancion.getArtista());
    AlbumDTO albumDTO = AlbumDTO.from(cancion.getAlbum());
    return CancionDTO.from(cancion, artistaDTO, albumDTO);
}

// map sobre Stream — convertir entidades a DTOs
return cancionRepository.findAll().stream()
        .map(this::toDTO)                   // method reference
        .toList();
```

**Nota**: `map` también existe en `Optional` — transforma el valor contenido si está presente.

#### `sorted(Comparator)` — Ordenamiento

> **Ref.**: [Stream.sorted(Comparator)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#sorted(java.util.Comparator))

Retorna un stream con los elementos ordenados según el comparador.

```java
// Ejemplo 1 — Ordenar por reproducciones descendente (RecomendacionPorPopularidad)
return catalogo.stream()
        .filter(c -> !c.equals(base))
        .sorted(Comparator.comparingInt(Cancion::getReproducciones).reversed())
        .limit(5)
        .toList();

// Ejemplo 2 — Ordenar por reproducciones con lambda (RecomendacionPorGenero)
return catalogo.stream()
        .filter(c -> !c.equals(base))
        .filter(c -> c.getGenero() == base.getGenero())
        .sorted((a, b) -> Integer.compare(b.getReproducciones(), a.getReproducciones()))
        .limit(10)
        .toList();

// Ejemplo 3 — Ordenamiento con switch expression (BusquedaService)
public List<Cancion> ordenarPersonalizado(List<Cancion> canciones, String criterio) {
    Comparator<Cancion> comparator = switch (criterio.toLowerCase()) {
        case "reproducciones" -> Comparator.comparingInt(Cancion::getReproducciones).reversed();
        case "rating"         -> Comparator.comparingDouble(Cancion::getRating).reversed();
        case "duracion"        -> Comparator.comparingInt(Cancion::getDuracionSegundos);
        case "fecha"           -> Comparator.comparing(Cancion::getFechaLanzamiento).reversed();
        default -> throw new IllegalArgumentException("Criterio no válido: " + criterio);
    };
    return canciones.stream().sorted(comparator).toList();
}
```

**⚠️ `sorted` es una operación stateful** — necesita ver todos los elementos antes de producir resultado. En pipelines paralelos puede requerir buffering significativo. Ver [Stateless behaviors](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html#Statelessness).

#### `limit(long)` — Limitar resultados

> **Ref.**: [Stream.limit(long)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#limit(long))

Retorna un stream truncado a no más de `maxSize` elementos.

```java
// Top 10 más reproducidas (ProcesamientoCancionService)
return cancionRepository.findAll().stream()
        .sorted(Comparator.comparingInt(Cancion::getReproducciones).reversed())
        .limit(10)
        .toList();
```

**Nota**: `limit` es una operación **short-circuiting** — puede completarse en tiempo finito incluso con streams infinitos.

#### `distinct()` — Eliminar duplicados

> **Ref.**: [Stream.distinct()](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#distinct())

Retorna un stream con elementos únicos (según `equals()`).

```java
// Obtener géneros únicos del catálogo
List<Genero> generosUnicos = canciones.stream()
        .map(Cancion::getGenero)
        .distinct()
        .toList();
```

**⚠️ `distinct` es stateful** — necesita almacenar elementos vistos para determinar unicidad.

#### `flatMap(Function)` — Aplanar streams anidados

> **Ref.**: [Stream.flatMap(Function)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#flatMap(java.util.function.Function))

Transforma cada elemento en un stream y aplana todos los streams resultantes en uno solo.

```java
// Obtener todas las canciones de una lista de artistas (ejemplo hipotético)
List<Cancion> todasLasCanciones = artistas.stream()
        .flatMap(a -> a.getCanciones().stream())   // Stream<Cancion> aplanado
        .distinct()
        .toList();

// flatMap también convierte Optional<Stream> en Stream
// Optional.stream() + flatMap = filtrar Optionals vacíos
List<Cancion> encontradas = ids.stream()
        .map(id -> busquedaService.busquedaBinariaPorTitulo(catalogo, id))
        .flatMap(Optional::stream)    // Optional<Cancion> → Stream<Cancion>
        .toList();
```

---

### Operaciones terminales (eager)

Las operaciones terminales producen un resultado o efecto secundario. Después de una operación terminal, el stream se considera **consumido** y no puede reutilizarse.

#### `toList()` — Recopilar en lista inmutable

> **Ref.**: [Stream.toList()](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#toList()) (desde Java 16)

Retorna una lista inmutable con los elementos del stream.

```java
// Patrón más usado en el proyecto
return cancionRepository.findAll().stream()
        .map(this::toDTO)
        .toList();    // Lista inmutable (Java 16+)
```

**⚠️ Diferencia con `Collectors.toList()`**:
- `stream.toList()` → retorna `List` inmutable (no se puede agregar/eliminar)
- `stream.collect(Collectors.toList())` → retorna `List` mutable

#### `collect(Collectors.*)` — Reducciones mutables avanzadas

> **Ref.**: [Collectors](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html)

##### `groupingBy` — Agrupar elementos

> **Ref.**: [Collectors.groupingBy()](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html#groupingBy(java.util.function.Function,java.util.stream.Collector))

```java
// Ejemplo 1 — Agrupar por género (sin downstream collector)
Map<Genero, List<Cancion>> porGenero = canciones.stream()
        .collect(Collectors.groupingBy(Cancion::getGenero));

// Ejemplo 2 — Agrupar por género + promedio de rating (con downstream collector)
Map<Genero, Double> promedioRating = cancionRepository.findAll().stream()
        .collect(Collectors.groupingBy(
                Cancion::getGenero,                        // clasificador
                Collectors.averagingDouble(Cancion::getRating)  // downstream
        ));

// Ejemplo 3 — Agrupar por artista + suma de reproducciones
Map<Artista, Integer> reproduccionesPorArtista = cancionRepository.findAll().stream()
        .collect(Collectors.groupingBy(
                Cancion::getArtista,                     // clasificador
                Collectors.summingInt(Cancion::getReproducciones)  // downstream
        ));

// Ejemplo 4 — Agrupar por década + contar
Map<String, Long> porDecada = cancionRepository.findAll().stream()
        .collect(Collectors.groupingBy(
                c -> (c.getFechaLanzamiento().getYear() / 10 * 10) + "s",  // clasificador dinámico
                Collectors.counting()                                          // downstream
        ));
```

**Patrón**: `groupingBy` acepta un **clasificador** (Function) y opcionalmente un **downstream collector** para aplicar una reducción sobre cada grupo.

##### `averagingDouble` — Promedio

> **Ref.**: [Collectors.averagingDouble()](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html#averagingDouble(java.util.function.ToDoubleFunction))

```java
// Promedio de rating por género
Map<Genero, Double> promedioRating = canciones.stream()
        .collect(Collectors.groupingBy(
                Cancion::getGenero,
                Collectors.averagingDouble(Cancion::getRating)
        ));
```

##### `summingInt` — Suma

> **Ref.**: [Collectors.summingInt()](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html#summingInt(java.util.function.ToIntFunction))

```java
// Total de reproducciones por artista
Map<Artista, Integer> totalPorArtista = canciones.stream()
        .collect(Collectors.groupingBy(
                Cancion::getArtista,
                Collectors.summingInt(Cancion::getReproducciones)
        ));
```

##### `counting` — Contar elementos por grupo

> **Ref.**: [Collectors.counting()](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html#counting())

```java
// Distribución por décadas
Map<String, Long> distribucion = canciones.stream()
        .collect(Collectors.groupingBy(
                c -> (c.getFechaLanzamiento().getYear() / 10 * 10) + "s",
                Collectors.counting()
        ));
```

#### `reduce` — Reducción con valor acumulador

> **Ref.**: [Stream.reduce()](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#reduce(java.util.function.BinaryOperator))

```java
// Ejemplo del proyecto — Componer múltiples predicados con AND (BusquedaService)
public List<Cancion> busquedaConPredicados(List<Cancion> canciones,
                                            List<Predicate<Cancion>> predicados) {
    Predicate<Cancion> combinado = predicados.stream()
            .reduce(Predicate::and)     // combina todos con AND
            .orElse(c -> true);          // si la lista está vacía, aceptar todo

    return canciones.stream()
            .filter(combinado)
            .toList();
}
```

**Variantes de `reduce`**:

```java
// 1. reduce(BinaryOperator<T>) → Optional<T>
Optional<Integer> maxEdad = personas.stream()
        .map(Persona::getEdad)
        .reduce(Integer::max);

// 2. reduce(T identity, BinaryOperator<T>) → T (con valor identidad)
int suma = numeros.stream()
        .reduce(0, Integer::sum);    // 0 es el valor identidad

// 3. reduce(identity, accumulator, combiner) → U (con mapeo)
int sumaPesos = widgets.stream()
        .reduce(0,
                (sum, b) -> sum + b.getWeight(),   // acumulador
                Integer::sum);                        // combiner (para paralelo)
```

#### `max` / `min` — Encontrar extremos

> **Ref.**: [Stream.max(Comparator)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#max(java.util.Comparator))

```java
// Ejemplo del proyecto — Artista más popular (ProcesamientoCancionService)
public ArtistaEstadistica artistaMasPopular() {
    return cancionRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                    Cancion::getArtista,
                    Collectors.summingInt(Cancion::getReproducciones)))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())              // Optional<Map.Entry<Artista, Integer>>
            .map(e -> new ArtistaEstadistica(e.getKey(), e.getValue()))
            .orElse(null);
}
```

#### `findFirst()` / `findAny()` — Búsqueda cortocircuitada

> **Ref.**: [Stream.findFirst()](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#findFirst())

```java
// Encontrar primera canción de ROCK con rating > 4
Optional<Cancion> resultado = canciones.stream()
        .filter(c -> c.getGenero() == Genero.ROCK)
        .filter(c -> c.getRating() > 4)
        .findFirst();    // Retorna Optional<Cancion>

// findAny() es más eficiente en streams paralelos
Optional<Cancion> cualquiera = canciones.parallelStream()
        .filter(c -> c.getGenero() == Genero.JAZZ)
        .findAny();
```

#### `anyMatch` / `allMatch` / `noneMatch` — Predicados sobre el stream

> **Ref.**: [Stream.anyMatch()](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html#anyMatch(java.util.function.Predicate))

```java
// ¿Hay alguna canción de JAZZ?
boolean hayJazz = canciones.stream()
        .anyMatch(c -> c.getGenero() == Genero.JAZZ);

// ¿Todas las canciones duran más de 2 minutos?
boolean todasLargas = canciones.stream()
        .allMatch(c -> c.getDuracionSegundos() > 120);

// ¿Ninguna canción tiene rating 0?
boolean sinCeros = canciones.stream()
        .noneMatch(c -> c.getRating() == 0);
```

**Nota**: Estas operaciones son **short-circuiting** — no necesitan recorrer todo el stream si pueden determinar el resultado antes.

---

### Streams primitivos

> **Ref.**: [IntStream](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/IntStream.html), [LongStream](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/LongStream.html), [DoubleStream](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/DoubleStream.html)

Java provee streams especializados para tipos primitivos, evitando el autoboxing:

```java
// mapToInt → IntStream (evita boxing de int a Integer)
int totalReproducciones = canciones.stream()
        .mapToInt(Cancion::getReproducciones)
        .sum();

// Estadísticas resumidas con IntSummaryStatistics
IntSummaryStatistics stats = canciones.stream()
        .mapToInt(Cancion::getDuracionSegundos)
        .summaryStatistics();

stats.getMax();     // duración máxima
stats.getMin();     // duración mínima
stats.getAverage(); // duración promedio
stats.getCount();   // cantidad de canciones
stats.getSum();     // duración total

// mapToDouble → DoubleStream
double ratingPromedio = canciones.stream()
        .mapToDouble(Cancion::getRating)
        .average()
        .orElse(0.0);
```

---

## 2. Optional

> **Documentación oficial**: [java.util.Optional](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html)

`Optional<T>` es un contenedor que puede contener o no un valor no-nulo. Su propósito principal es representar de forma explícita un resultado que puede estar ausente, evitando `NullPointerException`.

### Creación de Optional

```java
// Con valor presente
Optional<Cancion> opt = Optional.of(cancion);           // lanza NPE si cancion es null

// Con valor que puede ser null
Optional<Cancion> opt = Optional.ofNullable(cancion);   // retorna Optional.empty() si es null

// Vacío
Optional<Cancion> vacio = Optional.empty();
```

### Uso en el proyecto

```java
// Repository — findById retorna Optional
public Optional<Cancion> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
}

// Service — orElseThrow para lanzar excepción si no existe
public CancionDTO findById(UUID id) {
    Cancion cancion = cancionRepository.findById(id)
            .orElseThrow(() -> new CancionNotFoundException(id));
    return toDTO(cancion);
}

// Service — map para transformar si está presente
private CancionDTO toDTO(Cancion cancion) {
    ArtistaDTO artistaDTO = ArtistaDTO.from(cancion.getArtista());
    AlbumDTO albumDTO = AlbumDTO.from(cancion.getAlbum());
    // ...
}
```

### Métodos principales

| Método | Retorna | Descripción |
|--------|---------|-------------|
| `isPresent()` | `boolean` | `true` si hay valor presente |
| `isEmpty()` | `boolean` | `true` si NO hay valor (desde Java 11) |
| `get()` | `T` | Retorna el valor o lanza `NoSuchElementException` |
| `orElse(T)` | `T` | Retorna el valor o el valor por defecto |
| `orElseGet(Supplier)` | `T` | Retorna el valor o lo calcula con el Supplier |
| `orElseThrow()` | `T` | Retorna el valor o lanza `NoSuchElementException` |
| `orElseThrow(Supplier)` | `T` | Retorna el valor o lanza la excepción proporcionada |
| `map(Function)` | `Optional<U>` | Transforma el valor si está presente |
| `flatMap(Function)` | `Optional<U>` | Como `map` pero evita `Optional<Optional<U>>` |
| `filter(Predicate)` | `Optional<T>` | Filtra el valor si está presente |
| `ifPresent(Consumer)` | `void` | Ejecuta acción si el valor está presente |
| `ifPresentOrElse(Consumer, Runnable)` | `void` | Ejecuta una acción u otra (desde Java 9) |
| `stream()` | `Stream<T>` | Convierte a Stream de 0 o 1 elemento (desde Java 9) |

### Patrones recomendados

```java
// ✅ BUENO — orElseThrow con excepción personalizada
Cancion cancion = repository.findById(id)
        .orElseThrow(() -> new CancionNotFoundException(id));

// ✅ BUENO — map + orElse para transformar con fallback
ArtistaDTO dto = repository.findById(id)
        .map(ArtistaDTO::from)
        .orElse(null);

// ✅ BUENO — orElseGet cuando el valor por defecto es costoso
Cancion cancion = repository.findById(id)
        .orElseGet(() -> crearCancionDefault());  // solo se ejecuta si está vacío

// ❌ MALO — usar get() sin verificar
Cancion cancion = repository.findById(id).get();  // NoSuchElementException si no existe

// ❌ MALO — verificar con isPresent() y luego get()
if (repository.findById(id).isPresent()) {
    Cancion cancion = repository.findById(id).get();  // búsqueda doble
}

// ✅ BUENO — usar ifPresentOrElse para efectos secundarios
repository.findById(id)
        .ifPresentOrElse(
                c -> System.out.println("Encontrada: " + c.getTitulo()),
                () -> System.out.println("No encontrada")
        );
```

### Optional + Streams: `Optional.stream()`

```java
// Convertir lista de Optional a lista de valores presentes
List<Cancion> encontradas = ids.stream()
        .map(id -> busquedaBinariaPorTitulo(catalogo, id))  // Stream<Optional<Cancion>>
        .flatMap(Optional::stream)                           // Stream<Cancion> (filtra vacíos)
        .toList();
```

---

## 3. Predicate

> **Documentación oficial**: [java.util.function.Predicate](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Predicate.html)

`Predicate<T>` es una interfaz funcional que representa una función booleana de un argumento. Su método abstracto es `test(T t)`.

### Métodos de composición

| Método | Descripción |
|--------|-------------|
| `and(Predicate)` | Composición AND (cortocircuitada) |
| `or(Predicate)` | Composición OR (cortocircuitada) |
| `negate()` | Negación lógica |
| `isEqual(Object)` | Factory: predicado que compara con `equals` |
| `not(Predicate)` | Factory: negación de un predicado (desde Java 11) |

### Uso en el proyecto — Composición dinámica de filtros

```java
// BusquedaService.java — Componer múltiples filtros dinámicamente
public List<Cancion> busquedaConPredicados(List<Cancion> canciones,
                                            List<Predicate<Cancion>> predicados) {
    // reduce con Predicate::and combina todos los predicados en uno solo
    Predicate<Cancion> combinado = predicados.stream()
            .reduce(Predicate::and)     // AND lógico entre todos
            .orElse(c -> true);          // si no hay predicados, aceptar todo

    return canciones.stream()
            .filter(combinado)
            .toList();
}
```

**¿Cómo funciona `reduce(Predicate::and)`?**

```
Predicados: [p1, p2, p3, p4]

reduce(Predicate::and) combina:
  p1.and(p2).and(p3).and(p4)

Resultado: un solo Predicate que es true solo si TODOS son true
```

### Ejemplos de composición

```java
// Crear predicados individuales
Predicate<Cancion> esRock = c -> c.getGenero() == Genero.ROCK;
Predicate<Cancion> ratingAlto = c -> c.getRating() >= 4;
Predicate<Cancion> corta = c -> c.getDuracionSegundos() < 180;

// Componer con AND
Predicate<Cancion> rockYRatingAlto = esRock.and(ratingAlto);

// Componer con OR
Predicate<Cancion> rockOCorta = esRock.or(corta);

// Negar
Predicate<Cancion> noRock = esRock.negate();
// Equivalente con factory:
Predicate<Cancion> noRock2 = Predicate.not(esRock);

// Encadenar
Predicate<Cancion> compuesto = esRock.and(ratingAlto).and(corta);
// Equivale a: c -> c.getGenero() == Genero.ROCK && c.getRating() >= 4 && c.getDuracionSegundos() < 180
```

### Uso con filtros opcionales

```java
// Patrón: filtros dinámicos con parámetros opcionales
public List<Cancion> filtrar(Genero genero, Integer ratingMinimo, Integer duracionMaxima) {
    return cancionRepository.findAll().stream()
            .filter(c -> genero == null || c.getGenero() == genero)
            .filter(c -> ratingMinimo == null || c.getRating() >= ratingMinimo)
            .filter(c -> duracionMaxima == null || c.getDuracionSegundos() <= duracionMaxima)
            .toList();
}
```

**Patrón clave**: `parametro == null || condicion` — si el parámetro es null, el filtro se ignora (siempre true). Si tiene valor, se aplica la condición.

---

## 4. Comparator

> **Documentación oficial**: [java.util.Comparator](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Comparator.html)

`Comparator<T>` es una interfaz funcional que define un orden total sobre una colección de objetos. Su método abstracto es `compare(T o1, T o2)`.

### Métodos factory estáticos

| Método | Descripción |
|--------|-------------|
| `comparing(Function)` | Compara por clave extraída (Comparable) |
| `comparing(Function, Comparator)` | Compara por clave con Comparator personalizado |
| `comparingInt(ToIntFunction)` | Compara por clave int (sin autoboxing) |
| `comparingLong(ToLongFunction)` | Compara por clave long |
| `comparingDouble(ToDoubleFunction)` | Compara por clave double |
| `naturalOrder()` | Orden natural de Comparable |
| `reverseOrder()` | Inverso del orden natural |
| `nullsFirst(Comparator)` | null primero, luego el comparador dado |
| `nullsLast(Comparator)` | null último, luego el comparador dado |

### Métodos default de composición

| Método | Descripción |
|--------|-------------|
| `reversed()` | Invierte el orden |
| `thenComparing(Comparator)` | Orden secundario si el primario empata |
| `thenComparing(Function)` | Orden secundario por clave Comparable |
| `thenComparingInt(ToIntFunction)` | Orden secundario por clave int |

### Uso en el proyecto

```java
// 1. comparingInt + reversed — Top 10 más reproducidas
canciones.stream()
        .sorted(Comparator.comparingInt(Cancion::getReproducciones).reversed())
        .limit(10)

// 2. comparingDouble + reversed — Ordenar por rating descendente
Comparator<Cancion> porRating = Comparator.comparingDouble(Cancion::getRating).reversed();

// 3. comparing + reversed — Ordenar por fecha descendente
Comparator<Cancion> porFecha = Comparator.comparing(Cancion::getFechaLanzamiento).reversed();

// 4. comparing con CASE_INSENSITIVE_ORDER — Búsqueda binaria
List<Cancion> ordenadas = canciones.stream()
        .sorted(Comparator.comparing(Cancion::getTitulo, String.CASE_INSENSITIVE_ORDER))
        .toList();

// 5. Switch expression para seleccionar comparator dinámicamente
Comparator<Cancion> comparator = switch (criterio.toLowerCase()) {
    case "reproducciones" -> Comparator.comparingInt(Cancion::getReproducciones).reversed();
    case "rating"         -> Comparator.comparingDouble(Cancion::getRating).reversed();
    case "duracion"        -> Comparator.comparingInt(Cancion::getDuracionSegundos);
    case "fecha"           -> Comparator.comparing(Cancion::getFechaLanzamiento).reversed();
    default -> throw new IllegalArgumentException("Criterio no válido: " + criterio);
};
```

### Composición con `thenComparing`

```java
// Ordenar por género, luego por rating descendente, luego por título
Comparator<Cancion> compuesto = Comparator
        .comparing(Cancion::getGenero)
        .thenComparing(Comparator.comparingDouble(Cancion::getRating).reversed())
        .thenComparing(Cancion::getTitulo);

// Manejo de nulls — nullsFirst: artistas sin nombre van primero
Comparator<Artista> conNulls = Comparator.comparing(
        Artista::getNombre, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
```

### Lambda vs Method Reference vs Comparator factory

```java
// Todas estas expresiones ordenan por reproducciones descendente:

// 1. Lambda explícita
.sorted((a, b) -> Integer.compare(b.getReproducciones(), a.getReproducciones()))

// 2. Comparator factory (preferida — más legible y sin autoboxing)
.sorted(Comparator.comparingInt(Cancion::getReproducciones).reversed())

// 3. Comparator.comparing con Integer (con autoboxing)
.sorted(Comparator.comparing(Cancion::getReproducciones).reversed())
```

**Recomendación**: Preferir `comparingInt` / `comparingDouble` / `comparingLong` sobre `comparing` cuando el tipo de la clave sea primitivo, para evitar autoboxing innecesario.

---

## 5. Patrones avanzados

### Pipeline completo: fuente → intermedias → terminal

```java
// Ejemplo completo del proyecto — artistaMasPopular()
public ArtistaEstadistica artistaMasPopular() {
    return cancionRepository.findAll().stream()                                    // FUENTE
            .collect(Collectors.groupingBy(                                        // TERMINAL (collect)
                    Cancion::getArtista,                                         //   clasificador
                    Collectors.summingInt(Cancion::getReproducciones)))            //   downstream
            .entrySet().stream()                                                   // FUENTE (Map → Stream)
            .max(Map.Entry.comparingByValue())                                     // TERMINAL (max)
            .map(e -> new ArtistaEstadistica(e.getKey(), e.getValue()))            // INTERMEDIA (map sobre Optional)
            .orElse(null);                                                         // TERMINAL (orElse sobre Optional)
}
```

### Streams sobre Map con `entrySet()`

```java
// Convertir Map a Stream de entradas para procesar
Map<Artista, Integer> reproduccionesPorArtista = canciones.stream()
        .collect(Collectors.groupingBy(Cancion::getArtista,
                Collectors.summingInt(Cancion::getReproducciones)));

// Encontrar el artista con más reproducciones
Optional<Map.Entry<UUID, Integer>> maxEntry = reproduccionesPorArtista.entrySet().stream()
        .max(Map.Entry.comparingByValue());
```

### Optional + Stream interop

```java
// Optional.stream() convierte Optional en Stream de 0 o 1 elemento
// Útil para filtrar Optionals vacíos en un stream

List<Cancion> resultados = ids.stream()
        .map(this::buscarPorId)          // Stream<Optional<Cancion>>
        .flatMap(Optional::stream)       // Stream<Cancion> (filtra vacíos)
        .toList();
```

### Reducción de Predicados

```java
// Componer N predicados dinámicamente con reduce
List<Predicate<Cancion>> filtros = List.of(
        c -> c.getGenero() == Genero.ROCK,
        c -> c.getRating() >= 4,
        c -> c.getDuracionSegundos() < 300
);

Predicate<Cancion> combinado = filtros.stream()
        .reduce(Predicate::and)          // AND de todos
        .orElse(c -> true);             // fallback: aceptar todo

// También se puede componer con OR
Predicate<Cancion> cualquiera = filtros.stream()
        .reduce(Predicate::or)           // OR de todos
        .orElse(c -> false);            // fallback: rechazar todo
```

---

## 6. Referencias

### Documentación oficial Java 21

| Recurso | URL |
|---------|-----|
| **Package java.util.stream** | [docs.oracle.com/.../java/util/stream/package-summary.html](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html) |
| **Stream\<T\>** | [docs.oracle.com/.../Stream.html](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html) |
| **Collectors** | [docs.oracle.com/.../Collectors.html](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Collectors.html) |
| **Optional\<T\>** | [docs.oracle.com/.../Optional.html](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html) |
| **Predicate\<T\>** | [docs.oracle.com/.../Predicate.html](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Predicate.html) |
| **Comparator\<T\>** | [docs.oracle.com/.../Comparator.html](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Comparator.html) |
| **IntStream** | [docs.oracle.com/.../IntStream.html](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/IntStream.html) |
| **Function\<T,R\>** | [docs.oracle.com/.../Function.html](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Function.html) |

### Tutoriales oficiales Oracle

| Recurso | URL |
|---------|-----|
| **Aggregate Operations** (tutorial) | [docs.oracle.com/.../collections/streams.html](https://docs.oracle.com/javase/tutorial/collections/streams/) |
| **Lambda Expressions** (tutorial) | [docs.oracle.com/.../javaOO/lambdaexpressions.html](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html) |
| **Method References** (tutorial) | [docs.oracle.com/.../javaOO/methodreferences.html](https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html) |

### Archivos del proyecto que usan estas APIs

| Archivo | APIs utilizadas |
|---------|----------------|
| `ProcesamientoCancionService.java` | `stream()`, `filter`, `sorted`, `limit`, `toList`, `Collectors.groupingBy`, `averagingDouble`, `summingInt`, `counting`, `max`, `map` |
| `BusquedaService.java` | `stream()`, `sorted`, `Comparator.comparing/comparingInt/comparingDouble`, `reversed`, `reduce(Predicate::and)`, `switch expression` |
| `CancionService.java` | `stream()`, `map`, `toList` (pipeline de conversión a DTO) |
| `RecomendacionPorGenero.java` | `stream()`, `filter`, `sorted` (lambda comparator), `limit`, `toList` |
| `RecomendacionPorPopularidad.java` | `stream()`, `filter`, `sorted(Comparator.comparingInt)`, `limit`, `toList` |
| `RecomendacionDescubrimiento.java` | `stream()`, `filter` (4 encadenados), `limit`, `toList` |
| `CancionRepository.java` | `ConcurrentHashMap`, `Optional.ofNullable()` |
| `Cancion.java` | `AtomicInteger` (thread-safe) |
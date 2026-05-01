# AGENTS.md - Streaming Music REST API

## Stack

- Java 21, Spring Boot 3.5.x, Maven (`mvn`)
- Spring Web, springdoc-openapi (Swagger/OpenAPI)
- Persistencia en memoria: ConcurrentHashMap + HashMap
- JUnit 5 + TestRestTemplate para tests

## Package Structure

```
src/main/java/com/streaming/music/
├── controller/    # @RestController, delgados — sin lógica de negocio
├── service/       # @Service, lógica de negocio
│   └── estrategia/  # Patrón Strategy (3 implementaciones)
├── repository/    # Almacenamiento en memoria (@Repository, in-memory)
├── model/         # Clases regulares (entidades) + Genero enum
├── dto/           # Records inmutables para request/response
├── exception/     # Custom exceptions + @ControllerAdvice global
├── config/        # DataInitializer (seed data)
└── StreamingMusicApplication.java
```

## Commands

```bash
mvn spring-boot:run              # Arrancar la app (Swagger en http://localhost:8080/swagger-ui.html)
mvn test                          # Todos los tests (incluye test de concurrencia con Virtual Threads)
mvn test -Dtest=ConcurrencyVirtualThreadsTest  # Solo test de concurrencia
mvn clean package                 # Build para producción
```

## Agent Workflow (orden obligatorio)

1. **@software-architect** — Diseñar entidades, endpoints, DTOs, arquitectura
2. **@backend-developer** — Implementar según las especificaciones del architect
3. **@test-writer** — Escribir tests unitarios e integración
4. **@code-reviewer** — Revisar código, seguridad, anti-patrones
5. **@documentation-writer** — Documentar endpoints, Javadoc, README

## Convenciones

- **Inyección por constructor** (nunca `@Autowired` en campos)
- **DTO pattern**: las entidades JAMÁS se retornan desde controllers — usar Records DTO
- **Entidades**: clases regulares con campos `final` + getters (no Records, para soportar mutabilidad)
- **DTOs**: Records inmutables de Java 21
- **Excepciones**: `@ControllerAdvice` centralizado, no try/catch en controllers
- **HTTP status**: 200 OK, 400 bad request, 404 not found
- **Tests**: `methodName_StateUnderTest_ExpectedBehavior` con `@DisplayName`

## Arquitectura de datos

| Capa | Tipo | Inmutabilidad |
|------|------|--------------|
| `model/` | Clases regulares | Campos `final`, mutable solo `Cancion.reproducciones` (AtomicInteger) |
| `dto/` | Records | Totalmente inmutables |
| `repository/` | HashMap/ConcurrentHashMap | Thread-safe para CancionRepository |

## Agentes disponibles

| Agente | Modo | Propósito |
|--------|------|-----------|
| `software-architect` | primary | Diseñar soluciones, arquitectura, contratos API |
| `backend-developer` | all | Implementar código Java/Spring Boot |
| `test-writer` | subagent | Tests unitarios e integración |
| `code-reviewer` | subagent | Revisión de código (solo lectura) |
| `documentation-writer` | subagent | Documentación de API, Javadoc, README |

# AGENTS.md

This document provides guidelines and instructions for agents working on the Adopet API project.

## Project Overview

Adopet is a Spring Boot REST API for a pet adoption platform. It manages tutors, shelters (refugios), pets (mascotas), and adoption requests.

**Tech Stack:**
- Java 17
- Spring Boot 3.3.1
- Maven
- JPA/Hibernate
- Flyway (database migrations)
- MySQL
- Jakarta Validation

## Build & Test Commands

### Maven Wrapper (Preferred)
```bash
./mvnw clean package           # Build the project
./mvnw test                     # Run all tests
./mvnw test -Dtest=ClassName    # Run single test class
./mvnw test -Dtest=ClassName#methodName  # Run single test method
./mvnw spring-boot:run          # Run the application
./mvnw clean                    # Clean build artifacts
```

### Standard Maven
```bash
mvn clean package
mvn test
mvn test -Dtest=ClassName
mvn spring-boot:run
```

## Code Style Guidelines

### Package Structure
```
com.aluracursos.adopet.api/
├── controller/    # REST controllers
├── service/       # Business logic
├── repository/    # JPA repositories
├── model/         # JPA entities and enums
├── dto/           # Data Transfer Objects (records)
├── validations/   # Validation interfaces and implementations
└── exception/     # Custom exceptions
```

### Naming Conventions

**Classes:**
- Models: `PascalCase` (e.g., `Tutor`, `Adopcion`, `StatusAdopcion`)
- Controllers: `PascalCase` + `Controller` suffix (e.g., `TutorController`)
- Services: `PascalCase` + `Service` suffix (e.g., `TutorService`)
- Repositories: `PascalCase` + `Repository` suffix (e.g., `TutorRepository`)
- DTOs: `PascalCase` + `Dto` suffix (e.g., `RegistroTutorDto`)
- Validations: Descriptive `PascalCase` (e.g., `ValidacionTutorConLimiteDeAdopciones`)

**Variables & Methods:**
- camelCase (e.g., `nombre`, `idTutor`, `registrarTutor()`)
- Boolean prefixes: `is`, `has`, `can` (e.g., `isAdoptada()`)

**Database Tables:**
- snake_case plural (e.g., `tutores`, `adopciones`, `mascotas`)

### Entity Conventions

```java
@Entity
@Table(name = "table_name")
public class EntityName {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // JPA annotations with explicit column names
    @Column(name = "column_name")
    private String field;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    private OtherEntity other;

    // Always include these methods for JPA entities
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityName that = (EntityName) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Getters for all fields
    // No setters for JPA-managed fields; use business methods instead
}
```

### DTO Conventions

Use Java records for DTOs:

```java
public record DtoName(
    @NotBlank String campo1,
    @NotBlank @Email String email,
    String campoOpcional
) {}
```

**DTO Naming Patterns:**
- `RegistroXxxDto` - For creation requests
- `ActualizacionXxxDto` - For update requests (include ID)
- `XxxDto` - For response DTOs
- `SolicitudAdopcionDTO` - Use DTO suffix consistently if already established

### Controller Conventions

```java
@RestController
@RequestMapping("/resource-path")
public class ResourceController {

    @Autowired
    private ResourceService service;

    @PostMapping
    @Transactional
    public ResponseEntity<String> create(@RequestBody @Valid Dto dto) {
        try {
            service.operation(dto);
            return ResponseEntity.ok().build();
        } catch (ValidacionException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody @Valid Dto dto) {
        // ...
    }
}
```

### Service Conventions

```java
@Service
public class XxxService {

    @Autowired
    private XxxRepository repository;

    public void operation(Dto dto) {
        if (condition) {
            throw new ValidacionException("Error message");
        }
        repository.save(new Entity(dto));
    }
}
```

### Validation Pattern

Implement validation interfaces for cross-cutting validations:

```java
public interface ValidacionesSolicitudAdopcion {
    void validar(SolicitudAdopcionDTO dto);
}
```

Implementations are automatically injected as a list:

```java
@Autowired
private List<ValidacionesSolicitudAdopcion> validaciones;

public void method() {
    validaciones.forEach(v -> v.validar(dto));
}
```

### Exception Handling

Custom exception for validation errors:

```java
public class ValidacionException extends RuntimeException {
    public ValidacionException(String message) {
        super(message);
    }
}
```

### Imports Organization

1. `java.*` packages
2. `javax.*` / `jakarta.*` packages
3. Third-party packages (Spring, Hibernate, etc.)
4. `com.aluracursos.adopet.*` project packages
5. Blank line between groups

### Code Formatting

- Indentation: 4 spaces
- No trailing whitespace
- One blank line between methods
- Use `@Transactional` on mutating controller methods
- Use `@Valid` on request body parameters
- Use `FetchType.LAZY` for relationships
- Prefer `getReferenceById()` over `findById()` when entity is needed for persistence

### Enums

```java
public enum StatusAdopcion {
    ESPERANDO_EVALUACION,
    APROBADO,
    REPROBADO;
}
```

### Testing Conventions

- Test classes: `XxxTests` or `XxxServiceTest`
- Use `@SpringBootTest` for integration tests
- Use JUnit 5 (`org.junit.jupiter.api.Test`)

## Database Migrations (Flyway)

- Location: `src/main/resources/db/migration/`
- Naming: `V{number}_description.sql`
- Example: `V1__create-table-abrigos.sql`

## API Endpoints Pattern

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /resource | Create new |
| PUT | /resource | Update |
| PUT | /resource/aprobar | Approve |
| PUT | /resource/reprobar | Reject |

## Error Handling

- Return `ResponseEntity.badRequest()` with error message for `ValidacionException`
- Use descriptive error messages in Spanish (consistent with codebase)
- Example: `"Datos ya registrados por otro tutor!"`

## Email Service

Email operations through `EmailService.enviarEmail(destinatario, asunto, cuerpo)`.

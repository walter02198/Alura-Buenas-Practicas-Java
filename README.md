# Adopet API

API REST para una plataforma ficticia de adopción de mascotas. Gestiona tutores, refugios, mascotas y solicitudes de adopción.

---

## Tecnologías

| Tecnología | Propósito |
|---|---|
| **Java 17** | Lenguaje base |
| **Spring Boot 3.3.1** | Framework principal |
| **Maven** | Gestión de dependencias |
| **JPA / Hibernate** | ORM para acceso a datos |
| **Flyway** | Migraciones de base de datos |
| **MySQL** | Base de datos relacional |
| **Jakarta Validation** | Validación de datos |
| **Spring Mail** | Envío de notificaciones por email |

---

## Arquitectura

El proyecto sigue una estructura en capas:

```
com.aluracursos.adopet.api/
├── controller/     Controladores REST
├── service/       Lógica de negocio
├── repository/    Acceso a datos (JPA)
├── model/         Entidades JPA y enumeraciones
├── dto/           Objetos de transferencia (records)
├── validations/   Reglas de validación reutilizables
└── exception/     Excepciones personalizadas
```

---

## Decisiones de diseño

### 1. DTOs como Records

Se utilizan **records** de Java para los DTOs, lo que permite definir datos inmutables con validaciones integradas mediante anotaciones de Jakarta:

```java
public record SolicitudAdopcionDTO(
    @NotNull Long idTutor,
    @NotNull Long idMascota,
    @NotBlank String motivo
) {}
```

- `RegistroXxxDto` — solicitud de creación
- `ActualizacionXxxDto` — solicitud de actualización
- `XxxDto` — respuesta
- `SolicitudAdopcionDTO` — solicitud de adopción

### 2. Validaciones externas al servicio

En lugar de acumular validaciones dentro del servicio, se implementó un **patrón de interfaz de validación** que permite agregar reglas de forma extensible:

```java
public interface ValidacionesSolicitudAdopcion {
    void validar(SolicitudAdopcionDTO dto);
}
```

Las implementaciones se inyectan automáticamente como lista, y el servicio las ejecuta en lote:

```java
@Autowired
private List<ValidacionesSolicitudAdopcion> validaciones;

public void solicitar(SolicitudAdopcionDTO dto) {
    validaciones.forEach(v -> v.validar(dto));
}
```

**Validaciones existentes:**
- `ValidacionTutorConLimiteDeAdopciones` — verifica que el tutor no supere el límite de adopciones
- `ValidacionTutorConAdopcionEnMarcha` — verifica que el tutor no tenga una adopción pendiente
- `ValidacionMascotaConAdopcionEnMarcha` — verifica que la mascota no tenga una adopción pendiente
- `ValidacionMascotaYaAdoptada` — verifica que la mascota no esté ya adoptada

### 3. Excepciones personalizadas

Se usa una `ValidacionException` para unificar los errores de validación del dominio, lanzándola desde los servicios y capturándola en los controladores:

```java
public class ValidacionException extends RuntimeException {
    public ValidacionException(String message) {
        super(message);
    }
}
```

### 4. Métodos de negocio en las entidades

Las entidades exponen métodos que encapsulan cambios de estado, evitando lógica de dominio dispersa en los servicios:

```java
public void marcarComoAprobada() {
    this.status = StatusAdopcion.APROBADO;
}

public void marcarComoReprobada(@NotBlank String justificacion) {
    this.status = StatusAdopcion.REPROBADO;
    this.justificacionStatus = justificacion;
}
```

### 5. Uso de `getReferenceById()`

Se prioriza `getReferenceById()` sobre `findById()` cuando la entidad solo se necesita para persistencia, ya que evita consultas innecesarias a la base de datos:

```java
Mascota mascota = mascotaRepository.getReferenceById(dto.idMascota());
```

### 6. Servicio de email

`EmailService` actúa como una fachada sobre `JavaMailSender`, centralizando la configuración del remitente y simplificando el envío desde los servicios:

```java
emailService.enviarEmail(destinatario, asunto, cuerpo);
```

---

## Endpoints

### Tutores
| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/tutores` | Registrar tutor |
| `PUT` | `/tutores` | Actualizar tutor |

### Refugios
| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/refugios` | Registrar refugio |

### Mascotas
| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/mascotas` | Registrar mascota |
| `GET` | `/mascotas` | Listar mascotas disponibles |

### Adopciones
| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/adopciones` | Solicitar adopción |
| `PUT` | `/adopciones/aprobar` | Aprobar adopción |
| `PUT` | `/adopciones/reprobar` | Rechazar adopción |

---

## Migraciones

Las migraciones se encuentran en `src/main/resources/db/migration/` y son administradas por Flyway:

| Archivo | Descripción |
|---|---|
| `V1__create-table-abrigos.sql` | Tabla de refugios |
| `V2__create-table-pets.sql` | Tabla de mascotas |
| `V3__create-table-tutores.sql` | Tabla de tutores |
| `V4__create-table-adocoes.sql` | Tabla de adopciones |

---

## Licencia

Proyecto desarrollado por [Alura Latam](https://www.aluracursos.com) para los cursos de buenas prácticas de programación con Java.

Instructor: [Bruno Dario Fernandez Ellerbach](https://app.aluracursos.com/user/brunofernandez)

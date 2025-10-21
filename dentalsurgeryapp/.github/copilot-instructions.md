# Dental Surgery Application - AI Coding Agent Instructions

## Architecture Overview

This is a **hybrid Spring Boot application** combining both MVC web pages and REST API endpoints. The unique architecture serves both human users (via Thymeleaf templates) and API consumers using a unified controller pattern.

### Core Technologies
- **Spring Boot 3.5.6** with Java 25
- **Spring Data JPA** with H2 (dev) / MySQL (prod) support
- **Thymeleaf** for server-side rendering
- **Maven** build system
- **Lombok** for reducing boilerplate

## Critical Architectural Patterns

### 1. Unified Controller Pattern
Controllers handle both MVC routes (`/secured/*`) and REST API routes (`/dentalsugery/api/*`):

```java
@Controller("patientController")  // Note: Named bean to avoid conflicts
public class PatientController {
    // MVC endpoints
    @GetMapping("/secured/patient/list")
    public String listPatients(Model model) { ... }
    
    // REST API endpoints  
    @ResponseBody
    @GetMapping("/dentalsugery/api/patients")
    public ResponseEntity<List<PatientResponseDTO>> apiGetAllPatients() { ... }
}
```

### 2. DTO Mapping Convention
All DTOs use **Java records** with consistent naming:
- `*RequestDTO` for incoming data
- `*ResponseDTO` for outgoing data
- Manual mapping in controllers via private `mapToDTO()` and `mapToEntity()` methods
- **Always null-check nested objects** (e.g., Address) before mapping

```java
private PatientResponseDTO mapToDTO(Patient patient) {
    AddressResponseDTO addressDTO = null;
    if (patient.getAddress() != null) {  // Critical null check
        addressDTO = new AddressResponseDTO(/*...*/);
    }
    return new PatientResponseDTO(/*...*/, addressDTO);
}
```

### 3. Service Layer Transaction Patterns
- Use `@Transactional` at service implementation level
- **findOrCreate pattern** to prevent duplicates (see `PatientServiceImpl.findOrCreatePatient()`)
- Custom exceptions extend `Exception` (checked exceptions): `PatientNotFoundException`

### 4. Repository Query Methods
Leverage Spring Data JPA derived queries extensively:
```java
List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
    String firstName, String lastName, String email);
List<Patient> findAllByOrderByLastNameAsc();
```

## Project-Specific Conventions

### Package Structure
```
cs489.miu.dentalsurgeryapp/
├── controller/              # Main controllers + sysadmin subpackage
│   └── sysadmin/           # REST API controllers
├── dto/                    # All request/response DTOs  
├── model/                  # JPA entities
├── repository/             # Spring Data repositories
├── service/                # Service interfaces
│   └── impl/              # Service implementations
└── exception/             # Custom exception classes
```

### Database Configuration
- **Development**: H2 in-memory (`spring.sql.init.mode=always` runs `data.sql`)
- **H2 Console**: Available at `/h2-console` (username: `sa`, no password)
- **MySQL Profile**: Use `-Dspring-boot.run.profiles=mysql` for production

### URL Patterns
- **Public pages**: `/`, `/about`
- **Admin MVC**: `/secured/*` (Thymeleaf templates)
- **REST API**: `/dentalsugery/api/*` (JSON responses)
- **Static resources**: Templates use Bootstrap 5 + FontAwesome icons

### Common Development Workflows

#### Build & Test
```powershell
mvn clean test                    # Run tests
mvn spring-boot:run              # Start development server
mvn spring-boot:run -Dspring-boot.run.profiles=mysql  # Use MySQL
```

#### Key Files to Check
- `application.properties` - Database and H2 console config
- `data.sql` - Sample data initialization
- `templates/fragments/` - Reusable Thymeleaf components

### Integration Points
- **Address entities** are shared across Patient, Dentist, and SurgeryLocation
- **Bi-directional relationships** use `@JsonIgnore` to prevent circular serialization
- **Form validation** uses Bean Validation annotations (`@Valid`, `@NotBlank`, etc.)

### Error Handling Patterns
- REST endpoints return appropriate HTTP status codes
- MVC controllers use `RedirectAttributes` for flash messages
- Service methods throw checked exceptions that controllers catch and handle

## Testing Strategy
- Integration tests use `@SpringBootTest` with random ports
- Repository tests leverage Spring Data JPA test slices
- H2 in-memory database for fast test execution

When working with this codebase, always consider both the MVC and API aspects of features, maintain the DTO mapping patterns, and leverage the extensive Spring Data JPA query methods rather than writing custom queries.
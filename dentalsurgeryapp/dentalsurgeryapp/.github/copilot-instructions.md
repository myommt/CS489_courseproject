# Dental Surgery App - AI Coding Agent Instructions

## Architecture Overview
This is a Spring Boot 3.5.6 dental surgery management application with dual MVC/REST API architecture. The app manages patients, dentists, appointments, and billing using:
- **H2 in-memory database** for development (auto-seeded via `data.sql`)
- **Thymeleaf** templating for MVC views
- **Lombok** for entity boilerplate reduction
- **JPA/Hibernate** with MySQL connector for production

## Key Architectural Patterns

### Dual Controller Strategy
Controllers serve both MVC pages AND REST APIs:
- **MVC routes**: `/secured/patient/list` (returns Thymeleaf views)
- **REST API routes**: `/dentalsugery/api/patients` (returns JSON DTOs)
- Example: `PatientController` handles both patterns in a single class

### DTO Pattern Conventions
- **Request DTOs**: User input validation (e.g., `PatientRequestDTO`)
- **Response DTOs**: API output formatting (e.g., `PatientResponseDTO`)
- **Specialized DTOs**: Domain-specific like `OutstandingBillCheckDTO`
- DTOs use Java records for immutability

### Entity Relationships
- `Patient` ↔ `Address` (ManyToOne with cascade persist)
- `Patient` ↔ `Appointment` ↔ `Dentist` (appointment scheduling)
- `Bill` linked to appointments for billing management
- Use `@JoinColumn(name = "billing_address_id")` naming pattern

## Development Workflows

### Running the Application
```bash
./mvnw spring-boot:run
# Default port 8080, H2 console at /h2-console (sa/blank)
# To run on port 8081: server.port=8081 in application.properties
```

### Database Management
- **Local**: H2 in-memory with auto-seeding from `src/main/resources/data.sql`
- **Schema**: Auto-generated via `spring.jpa.hibernate.ddl-auto=update`
- **Sample data**: Always loaded via `spring.sql.init.mode=always`

### Service Layer Pattern
Services implement interfaces with implementations in `service/impl/`:
```java
// Interface in service/
public interface PatientService { ... }

// Implementation in service/impl/
@Service
public class PatientServiceImpl implements PatientService { ... }
```

## Project-Specific Conventions

### Repository Queries
Custom query methods follow Spring Data JPA naming:
```java
// Multi-field search pattern
findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase()

// Sorting pattern
findAllByOrderByLastNameAsc()
```

### Exception Handling
Custom exceptions extend RuntimeException:
- `PatientNotFoundException`
- `AppointmentLimitExceededException`
- `OutstandingBillException`

### Template Structure
Thymeleaf templates use fragment composition:
- `fragments/head-elements.html` - Common head content
- `fragments/header.html` - Navigation header
- `fragments/footer.html` - Page footer
- `public/` - Unauthenticated pages
- `secured/` - Admin/authenticated pages

### Model Conventions
- Use Lombok annotations: `@NoArgsConstructor`, `@AllArgsConstructor`, `@Getter`, `@Setter`
- Validation with `@NotBlank` and `@Valid`
- ID generation: `@GeneratedValue(strategy = GenerationType.IDENTITY)`

## Integration Points

### Controller Flow
1. **Public pages**: `PublicPagesController` handles `/`, `/about`, `/contact`
2. **Admin dashboard**: `SysAdminController` at `/secured/*` with service integration
3. **Entity management**: Specific controllers in `controller/sysadmin/` package

### Service Dependencies
Controllers inject multiple services for cross-entity operations:
```java
public SysAdminController(PatientService patientService, 
                         DentistService dentistService,
                         AppointmentService appointmentService,
                         BillService billService)
```

When modifying this codebase:
- Follow the dual MVC/REST pattern for new controllers
- Use DTOs for all API responses, entities for internal logic
- Add custom repository methods following the established naming patterns
- Include Thymeleaf fragments for consistent UI structure
- Handle exceptions gracefully with custom exception types
# Student Management System

A comprehensive Spring Boot enterprise application for managing students, courses, departments, and teachers with role-based access control, automated testing, and continuous integration.

## Project Overview

This is a full-stack educational platform built with Spring Boot 3.2.2 and Java 17. It demonstrates enterprise-grade software engineering practices including layered architecture, security enforcement, comprehensive testing, and CI/CD automation.

### Core Purpose

The application manages a student information system where:
- Students can view and manage their academic information
- Teachers manage course assignments and student enrollment
- Administrators configure departments, courses, and user access
- Role-based security enforces authorization at every layer

---

## Project Structure

The project follows a monorepo layout with the main application under `student-management-system/`:

```
.
├── student-management-system/          # Main Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/studentmanagementsystem/
│   │   │   │   ├── controller/          # HTTP endpoints
│   │   │   │   ├── service/             # Business logic
│   │   │   │   ├── repository/          # Data access (Spring Data JPA)
│   │   │   │   ├── entity/              # JPA domain models
│   │   │   │   ├── dto/                 # Data transfer objects
│   │   │   │   ├── security/            # Security config and custom services
│   │   │   │   ├── exception/           # Exception handling
│   │   │   │   └── config/              # Application configuration
│   │   │   └── resources/
│   │   │       ├── application.properties    # Main configuration
│   │   │       ├── templates/               # Thymeleaf HTML templates
│   │   │       └── static/                  # CSS, JS, images
│   │   └── test/
│   │       ├── java/com/example/studentmanagementsystem/
│   │       │   ├── controller/          # Controller integration tests
│   │       │   ├── service/             # Service unit tests
│   │       │   ├── repository/          # Repository integration tests
│   │       │   ├── entity/              # Entity validation tests
│   │       │   ├── security/            # Security service tests
│   │       │   └── StudentManagementSystemApplicationTests.java
│   │       └── resources/
│   │           └── application-test.yml     # Test profile configuration
│   ├── pom.xml                          # Maven build configuration
│   ├── mvnw / mvnw.cmd                  # Maven wrapper scripts
│   ├── Dockerfile                       # Docker container definition
│   ├── docker-compose.yml               # Docker Compose for local development
│   ├── TESTING_GUIDE.md                 # Comprehensive testing documentation
│   └── GITHUB_ACTIONS_WORKFLOW_GUIDE.md # CI/CD pipeline documentation
├── .github/
│   └── workflows/
│       └── tests.yml                    # GitHub Actions CI pipeline
└── README.md                            # This file
```

---

## Technology Stack

### Core Framework
- **Spring Boot 3.2.2**: Application framework with embedded Tomcat
- **Spring Security 6**: Authentication, authorization, and CSRF protection
- **Spring Data JPA**: Object-relational mapping and query abstraction
- **Spring MVC**: Web request handling and view rendering

### Data & Persistence
- **PostgreSQL**: Production relational database
- **H2 Database**: In-memory test database in PostgreSQL compatibility mode
- **Hibernate**: ORM framework for entity mapping
- **Thymeleaf**: Server-side HTML template engine

### Build & Dependency Management
- **Maven 3.9.12**: Build automation and dependency resolution
- **Java 17**: Modern JDK with latest language features

### Testing Infrastructure
- **JUnit 5**: Test framework with parameterized test support
- **Mockito**: Mocking and stubbing framework
- **AssertJ**: Fluent assertion library
- **Spring Test**: Spring context loading and integration testing utilities
- **Spring Security Test**: Security-aware testing helpers

### Code Quality & Monitoring
- **JaCoCo**: Code coverage instrumentation and reporting
- **Lombok**: Annotation-based boilerplate reduction

### CI/CD
- **GitHub Actions**: Automated testing on push/PR
- **Maven Surefire**: Test discovery and execution

---

## Architecture Overview

### Layered Design Pattern

The application strictly follows a three-tier architecture:

#### 1. Controller Layer (HTTP Boundary)
Files: `src/main/java/com/example/studentmanagementsystem/controller/`

Responsibility: Handle HTTP requests/responses, validate input, enforce security rules.

Key Classes:
- `StudentController`: Student CRUD operations and listing endpoints
- `CourseController`: Course management endpoints
- `DepartmentController`: Department CRUD operations
- `TeacherController`: Teacher management endpoints
- `RegistrationController`: User registration without authentication
- `HomeController`: Landing page and general navigation

Security enforcement at controller:
- `@PreAuthorize` annotations restrict methods to specific roles
- `@WithMockUser` in tests validates authorization checks
- CSRF tokens validated on POST/PUT/DELETE operations

#### 2. Service Layer (Business Logic)
Files: `src/main/java/com/example/studentmanagementsystem/service/`

Responsibility: Implement business rules, coordinate repositories, handle exceptions.

Key Services:
- `StudentService`: Student creation, updates, enrollment management
- `CourseService`: Course management and enrollment logic
- `DepartmentService`: Department operations
- `TeacherService`: Teacher assignment and management
- `CustomUserDetailsService`: Security principal loading from database

Each service:
- Receives requests from controllers
- Validates business constraints (e.g., email uniqueness, valid enrollments)
- Coordinates repository calls to persist changes
- Throws meaningful exceptions on constraint violations

#### 3. Repository/Persistence Layer (Data Access)
Files: `src/main/java/com/example/studentmanagementsystem/repository/`

Responsibility: Database queries and entity lifecycle management.

Key Repositories:
- `StudentRepository`: Query students by email, roll number; custom finder methods
- `CourseRepository`: Course queries and enrollment lookups
- `DepartmentRepository`: Department access patterns
- `TeacherRepository`: Teacher lookups

All repositories extend `JpaRepository`, providing:
- CRUD methods (save, findById, delete, etc.)
- Pagination and sorting support
- Custom query methods via `@Query` annotations

#### 4. Data Models
Files: `src/main/java/com/example/studentmanagementsystem/entity/`

Responsibility: Define domain objects and persistence mappings.

Core Entities:
- `Student`: JPA entity mapping to 'students' table with student-specific fields
- `Course`: Entity for course catalog
- `Department`: Entity for academic departments
- `Teacher`: Entity for faculty
- `Role`: Enum for ADMIN, STUDENT, TEACHER roles

Relationships:
- Student -> Department (ManyToOne)
- Course -> Department (ManyToOne)
- Student <- Course (ManyToMany through enrollment)

#### 5. Data Transfer Objects
Files: `src/main/java/com/example/studentmanagementsystem/dto/`

Responsibility: Define request/response DTOs separate from entities to prevent unintended field exposure.

Key DTOs:
- `StudentCreateDto`: Input for student creation
- `StudentUpdateDto`: Input for student updates
- `CourseDto`: Course transfer object
- `DepartmentDto`: Department transfer object
- `TeacherDto`: Teacher transfer object

Benefits:
- Allows entity schema evolution without breaking API contracts
- Prevents exposing sensitive fields (passwords, internal IDs)
- Enables partial updates and selective field serialization

#### 6. Security
Files: `src/main/java/com/example/studentmanagementsystem/security/`

Components:
- `SecurityConfig`: Spring Security filter chain, password encoding, HTTP security rules
- `CustomUserDetailsService`: Implements `UserDetailsService` to load user principals from database

Security Enforcement:
- HTTP Basic authentication (with form-based login fallback)
- Role-based access control via `@PreAuthorize` and `@Secured`
- CSRF token validation on state-modifying requests
- Password encoding with BCrypt

#### 7. Exception Handling
Files: `src/main/java/com/example/studentmanagementsystem/exception/`

- `GlobalExceptionHandler`: Centralized exception mapping to HTTP responses
- `ResourceNotFoundException`: Thrown when requested entity is not found

---

## Data Flow Example: Creating a Student

To illustrate the layered architecture:

1. HTTP Request arrives at `StudentController.createStudent(StudentCreateDto)`
2. Controller validates CSRF token and authorization via `@PreAuthorize("hasRole('ADMIN')")`
3. Controller calls `StudentService.createStudent(StudentCreateDto)`
4. Service performs business validation (email uniqueness, department existence)
5. Service calls `StudentRepository.save(Student entity)`
6. Repository delegates to Hibernate ORM, which executes INSERT SQL
7. PostgreSQL persists the record and returns the generated ID
8. Service returns created Student to controller
9. Controller serializes Student to JSON and returns HTTP 201 Created

---

## Testing Strategy

The project uses a comprehensive test pyramid with 214 passing tests covering all layers.

### Test Distribution

- **Unit Tests**: 77 tests (services, entity logic)
- **Integration Tests**: 130 tests (repositories, controllers)
- **Smoke Tests**: 7 tests (context loading, startup verification)

### Test Execution

#### Unit Tests (Service Layer)

Files: `src/test/java/com/example/studentmanagementsystem/service/`

Example: `StudentServiceTest`
- Mocks `StudentRepository` using Mockito
- Tests business logic in isolation
- Validates service decisions (exception throwing, data mapping)
- Fast execution (no database, no HTTP layer)

Tests validate:
- Student creation with email uniqueness checks
- Enrollment operations
- Authorization checks in service methods
- Exception handling for invalid inputs

#### Integration Tests (Repository Layer)

Files: `src/test/java/com/example/studentmanagementsystem/repository/`

Example: `StudentRepositoryIntegrationTest`
- Uses `@DataJpaTest` to load only persistence layer
- Runs against H2 in-memory database
- Tests custom query methods and JPA relationships
- Validates transaction semantics and data consistency

Tests validate:
- Custom finder methods (findByEmail, findByRoll)
- Entity relationship loading (lazy vs eager)
- Persistence lifecycle (insert, update, delete)
- Query performance and N+1 prevention

#### Integration Tests (Controller Layer)

Files: `src/test/java/com/example/studentmanagementsystem/controller/`

Example: `StudentControllerIntegrationTest`
- Uses `@SpringBootTest` with `MockMvc`
- Tests HTTP request/response behavior
- Validates security authorization enforcement
- Tests view resolution and form submission

Tests validate:
- HTTP status codes (200, 302, 403, 404)
- Request parameter binding and validation
- CSRF token requirement on POST/PUT/DELETE
- Authorization (Forbidden access, role-based redirects)
- Model attributes passed to views
- Exception handling and error pages

### Test Profile and Database

Integration tests activate `test` profile via `@ActiveProfiles("test")`.

Configuration file: `src/test/resources/application-test.yml`

Test-specific settings:
- Database: H2 in-memory in PostgreSQL compatibility mode
- Connection: `jdbc:h2:mem:testdb;MODE=PostgreSQL`
- Schema lifecycle: `ddl-auto: create-drop` (fresh schema per test class)
- Isolation: `@Transactional` on test methods rolls back changes after each test

Benefits:
- Zero dependency on external PostgreSQL instance
- Deterministic, repeatable test execution
- Instant startup and teardown
- Full transaction isolation

### Code Coverage

JaCoCo Maven plugin configured in `pom.xml`:
- Instruments bytecode during test phase
- Generates coverage report in `target/site/jacoco/`
- Enforces minimum 50% line coverage threshold per package

Run coverage: `./mvnw test jacoco:report`

---

## Build & Deployment

### Local Development

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd lab3
   ```

2. Install PostgreSQL and create development database:
   ```bash
   createdb student-managementDB
   ```

3. Configure credentials in `student-management-system/src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   ```

4. Build and run:
   ```bash
   cd student-management-system
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

5. Access application at `http://localhost:8081`

### Docker Deployment

A `Dockerfile` and `docker-compose.yml` are provided for containerized deployment:

```bash
docker-compose up -d
```

This starts:
- Spring Boot application on port 8081
- PostgreSQL database on port 5432

### Maven Wrapper

The project includes Maven Wrapper scripts (`mvnw`/`mvnw.cmd`), allowing:
- Consistent Maven version across environments
- No system-wide Maven installation required
- Automatic Maven download on first use

### Continuous Integration

GitHub Actions workflow (`.github/workflows/tests.yml`) automatically:

1. Triggers on push to `main`, `develop`, `feature/**`, `testing/**` branches
2. Triggers on pull requests to `main`, `develop`, `master`
3. Runs on `ubuntu-latest` runner with:
   - JDK 17 (Temurin distribution)
   - Maven with dependency caching

Pipeline Steps:
1. Checkout source code
2. Set up JDK 17
3. Compile: `./mvnw clean compile`
4. Test: `./mvnw test` with test profile activated
5. Parse test results with `dorny/test-reporter`
6. Upload test artifacts for diagnostics
7. Publish pass/fail status as GitHub check

### Branch Protection

Recommended configuration for `main` branch:

- Require pull requests before merge
- Require passing GitHub Actions status check (the `test` job)
- Require PR reviews before merge (optional)
- Require up-to-date branch before merge
- Restrict force pushes and deletions

This ensures:
- No direct commits to main
- All code changes tested in CI before integration
- Code review enforcement
- Stable, deployable main branch

---

## Development Workflow

### Feature Development

1. Create feature branch: `git checkout -b feature/new-feature`
2. Implement feature and write tests
3. Ensure all tests pass: `./mvnw clean test`
4. Check code coverage: `./mvnw jacoco:report`
5. Commit and push to feature branch
6. Open pull request against `develop` branch
7. GitHub Actions automatically tests the PR
8. Merge after review approval and passing CI checks

### Testing in Development

Run all tests:
```bash
./mvnw clean test
```

Run specific test class:
```bash
./mvnw test -Dtest=StudentServiceTest
```

Run tests and generate coverage report:
```bash
./mvnw clean test jacoco:report
```

View coverage report:
```bash
open target/site/jacoco/index.html
```

---

## Key Features

### Authentication & Authorization

- Form-based login with HTTP session management
- Role-based access control: ADMIN, TEACHER, STUDENT
- Method-level security with `@PreAuthorize` annotations
- CSRF protection on all state-modifying operations

### Student Management

- Student registration and profile management
- Enrollment in courses
- Academic information tracking (roll number, email)
- Department assignment

### Course Management

- Course creation and deletion by administrators
- Course enrollment management
- Teacher assignment to courses
- Enrollment status tracking

### Department Management

- Department CRUD operations
- Department-level resource organization
- Course association with departments

### Teacher Management

- Teacher registration and profile
- Course assignment
- Student enrollment oversight

---

## Configuration

### Application Properties

Main configuration: `student-management-system/src/main/resources/application.properties`

Key settings:
- Server port: 8081
- Database URL, username, password
- JPA Hibernate DDL strategy: update
- Thymeleaf template caching: disabled in development

### Test Configuration

Test profile: `student-management-system/src/test/resources/application-test.yml`

Activated by `@ActiveProfiles("test")` in integration tests.

Overrides:
- Database: H2 in-memory (instead of PostgreSQL)
- DDL strategy: create-drop (fresh schema per test)
- Logging: DEBUG for SQL and Spring Security

---

## Documentation

Two detailed guides are included:

1. **TESTING_GUIDE.md**: Comprehensive explanation of testing strategy
   - What tests were written and why
   - How tests are organized and executed
   - Dependencies used for testing
   - Terminology reference
   - Commands to run tests

2. **GITHUB_ACTIONS_WORKFLOW_GUIDE.md**: CI/CD pipeline documentation
   - GitHub Actions workflow sequence
   - Why automated testing is important
   - How branch protection enforces quality gates
   - Troubleshooting CI failures

---

## Common Commands

```bash
# Build and test
./mvnw clean verify

# Run application in development mode
./mvnw spring-boot:run

# Run all tests
./mvnw clean test

# Generate code coverage report
./mvnw test jacoco:report

# Generate site documentation
./mvnw site

# Package as JAR for deployment
./mvnw clean package

# Docker operations
docker-compose up -d        # Start containers
docker-compose down         # Stop containers
docker-compose logs -f      # View logs
```

---

## Troubleshooting

### Tests fail in CI but pass locally

- Verify test profile is active: `@ActiveProfiles("test")`
- Check H2 compatibility mode in test config
- Ensure `application-test.yml` is in `src/test/resources`

### GitHub Actions CI fails

- Check workflow file: `.github/workflows/tests.yml`
- Verify working directory is `student-management-system`
- Ensure all test reports path is correct: `target/surefire-reports/*.xml`
- Check JDK 17 compatibility with dependencies

### Database connection issues in development

- Verify PostgreSQL is running
- Confirm database credentials in `application.properties`
- Ensure database `student-managementDB` exists
- Check PostgreSQL port (default 5432)

### Port already in use

- Application runs on port 8081 by default
- Change port in `application.properties`: `server.port=8082`

---

## Future Enhancements

Potential areas for expansion:

- Grades and transcript management
- Course prerequisites and scheduling
- Attendance tracking
- User notification system
- REST API endpoints (currently web-based)
- Advanced reporting and analytics
- Multi-tenant support for multiple institutions

---

## Contributors

This project was developed as a comprehensive learning exercise in enterprise Spring Boot development, testing practices, and CI/CD automation.

---

## License

This project is provided as-is for educational purposes.

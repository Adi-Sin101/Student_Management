# Comprehensive Testing Guide

## 1) What testing has been done

This project uses a layered testing strategy:

- **Context load smoke test**
  - Verifies Spring application context starts successfully.
  - File: `src/test/java/com/example/studentmanagementsystem/StudentManagementSystemApplicationTests.java`

- **Unit tests (service/security/entity behavior in isolation)**
  - Service logic with mocked repositories/dependencies.
  - Security service behavior with mocked collaborators.
  - Entity model validation and behavior checks.
  - Files:
    - `src/test/java/com/example/studentmanagementsystem/service/StudentServiceTest.java`
    - `src/test/java/com/example/studentmanagementsystem/service/DepartmentServiceTest.java`
    - `src/test/java/com/example/studentmanagementsystem/service/CourseServiceTest.java`
    - `src/test/java/com/example/studentmanagementsystem/service/TeacherServiceTest.java`
    - `src/test/java/com/example/studentmanagementsystem/security/CustomUserDetailsServiceTest.java`
    - `src/test/java/com/example/studentmanagementsystem/entity/StudentEntityTest.java`
    - `src/test/java/com/example/studentmanagementsystem/entity/CourseEntityTest.java`

- **Repository integration tests (JPA + DB behavior)**
  - Uses Spring Data test slice and real persistence behavior against test DB.
  - Files:
    - `src/test/java/com/example/studentmanagementsystem/repository/StudentRepositoryIntegrationTest.java`
    - `src/test/java/com/example/studentmanagementsystem/repository/DepartmentRepositoryIntegrationTest.java`
    - `src/test/java/com/example/studentmanagementsystem/repository/CourseRepositoryIntegrationTest.java`

- **Controller integration tests (web layer + security + persistence path)**
  - Uses `MockMvc` to test request/response behavior, validation, authorization, and status codes.
  - Files:
    - `src/test/java/com/example/studentmanagementsystem/controller/StudentControllerIntegrationTest.java`
    - `src/test/java/com/example/studentmanagementsystem/controller/CourseControllerIntegrationTest.java`

- **Coverage instrumentation and reporting**
  - JaCoCo agent attached during test phase, report generated after tests.
  - `jacoco-maven-plugin` configured in `pom.xml`.

---

## 2) How all testing is done

### A. Execution flow

1. Compile production and test code.
2. Discover tests by Surefire includes (`**/*Test.java`, `**/*Tests.java`).
3. Execute JUnit 5 tests.
4. Collect test XML reports under `target/surefire-reports`.
5. Generate JaCoCo coverage report under `target/site/jacoco`.

### B. Test profile and DB mode

- Integration tests activate `test` profile with `@ActiveProfiles("test")`.
- Test config file: `src/test/resources/application-test.yml`.
- DB in tests: **H2 in-memory** in PostgreSQL compatibility mode.
- JPA in tests: `ddl-auto: create-drop` for isolated schema lifecycle.

### C. Testing approaches by layer

- **Unit tests**
  - Mock external dependencies (`@Mock`, `@InjectMocks`, Mockito stubbing).
  - Validate service decisions, exceptions, mapping, and branching logic.
- **Repository integration tests**
  - Validate query behavior, persistence, relationships, and transactional semantics.
- **Controller integration tests**
  - Validate endpoints, redirects/views, model attributes, auth checks, and CSRF/security behavior.

---

## 3) Dependencies used for testing

From `pom.xml`:

- `spring-boot-starter-test` (test scope)
  - Provides JUnit Jupiter, Spring Test, MockMvc helpers, assertions ecosystem integration.
- `spring-security-test` (test scope)
  - Enables security testing utilities (`@WithMockUser`, security request post-processors).
- `mockito-core` and `mockito-junit-jupiter` (test scope)
  - Mocking and stubbing framework integrated with JUnit 5.
- `assertj-core` (test scope)
  - Fluent assertions.
- `h2` (test scope)
  - In-memory database for fast, isolated integration testing.
- `maven-surefire-plugin`
  - Test execution plugin and test discovery patterns.
- `jacoco-maven-plugin`
  - Coverage instrumentation, report generation, and minimum threshold checking.

---

## 4) Why these tests were done

- **Reliability**: catch regressions in business logic and endpoint behavior.
- **Security confidence**: verify role/authorization behavior and forbidden access paths.
- **Persistence confidence**: ensure repositories and mappings behave correctly under real JPA operations.
- **Refactoring safety**: enable internal changes with low risk.
- **CI quality gate**: enforce automated verification before merge.

### Why Controller Integration Tests?

Controllers are the HTTP entry point and **cannot be fully tested with unit mocks alone** because:

- **Request/Response cycle**: Mocks bypass Spring's servlet layer, routing, interceptors, and filters. Real controllers need HTTP context (headers, parameters, CSRF tokens, session).
- **Security context**: Spring Security annotations (`@PreAuthorize`, `@Secured`) and role checks require a real security context. Mock-based tests won't trigger authentication/authorization logic.
- **Model binding and validation**: Spring's data binding, validation annotation processing, and error handling require the real Spring MVC machinery, not isolated method calls.
- **View resolution**: Controllers return views with model attributes. Integration tests verify that redirects, form submissions, and view templates receive correct data.
- **CSRF protection**: Web forms require CSRF tokens. Unit tests can't verify CSRF validation without the real Spring Security filter chain.
- **Exception handling**: Global exception handlers and error views are part of Spring's request pipeline. Integration tests ensure exception mapping works end-to-end.

**Example**: A `@PreAuthorize("hasRole('ADMIN')")` annotation looks correct in code, but only integration tests actually trigger Spring Security to enforce it.

### Why Repository Integration Tests?

Repositories interact directly with **Hibernate/JPA and the database**. Unit mocks can't catch real problems:

- **Query correctness**: Custom repository methods use JPQL/native SQL. Mocks won't reveal syntax errors, wrong joins, or logic bugs. Only real JPA execution surfaces these.
- **Relationship mapping**: `@OneToMany`, `@ManyToOne`, `@ManyToMany` relationships and their fetch strategies (eager/lazy) only behave correctly against real persistence context. Mocks can't validate cascade behavior or orphan deletion.
- **Transaction semantics**: Database transactions, locking, and isolation levels depend on real DB connections. Mocks skip all of this.
- **Data consistency**: Unique constraints, foreign keys, and check constraints are enforced by the DB, not by mocks. Real integration tests catch constraint violations.
- **Persistence lifecycle**: Operations like entity refresh, detachment, and reattachment behave differently in real Hibernate session vs mocks.
- **Performance issues**: N+1 query problems, inefficient joins, and missing indexes are invisible to mocks but critical to catch.

**Example**: A repository method that queries students by email might work in unit tests with a mock, but fail in production if the query doesn't load the department relationship, causing lazy-loading exceptions or missing data.

### Summary: The Testing Pyramid Justification

- **Unit tests** (many, fast, mocked) catch logic errors in isolation.
- **Integration tests** (moderate, slower, real Spring/DB) catch interaction bugs that mocks hide.
- **Smoke test** (few, validates startup) ensures basic context loads.

Without repository integration tests, SQL/ORM bugs go undetected. Without controller integration tests, authorization and security bypass bugs go undetected. **Mocks are necessary but not sufficient for web/data applications.**

---

## 5) How dependencies perform testing

- `spring-boot-starter-test` boots test contexts and integrates JUnit + Spring.
- Mockito isolates unit boundaries by replacing collaborators with controlled test doubles.
- `spring-security-test` simulates authenticated/authorized users and security context.
- H2 provides deterministic, local, resettable DB behavior for integration tests.
- Surefire discovers and runs tests in Maven lifecycle (`test` phase).
- JaCoCo instruments bytecode, tracks executed lines/branches, and emits coverage reports.

---

## 6) Why this approach was chosen

This is a **test pyramid** style approach:

- Many fast **unit tests** for logic correctness.
- Focused **integration tests** for repository and web/security behavior.
- Minimal **full-context smoke test** for startup confidence.

Reasons for choosing this approach:

- Fast feedback for developers.
- Good balance between speed and realism.
- Better defect localization (unit vs integration failures).
- Stable CI execution without depending on external DB services.

---

## 7) Terminology reference

- **Unit test**: tests one class/function with dependencies mocked.
- **Integration test**: tests interaction among real framework/data components.
- **Mock**: programmable test double used to isolate behavior.
- **Stub**: predefined behavior for method calls.
- **Test slice**: partial Spring context (for example repository-only).
- **Test profile**: Spring profile with test-only configuration.
- **Fixture**: known test data arrangement.
- **Assertion**: expected outcome check.
- **Smoke test**: minimal check that app context starts.
- **Coverage**: proportion of code executed by tests.
- **CI**: automated pipeline that runs tests/build on pushes/PRs.

---

## 8) Exactly where tests happen

### Source locations

- Unit + integration tests: `src/test/java/com/example/studentmanagementsystem/**`
- Test config: `src/test/resources/application-test.yml`

### Build lifecycle locations

- Test execution phase: Maven `test` phase (Surefire).
- Reports:
  - Unit/integration XML: `target/surefire-reports/*.xml`
  - Coverage HTML/XML: `target/site/jacoco/**`

### Runtime behavior by annotation

- `@SpringBootTest`: full app context integration testing.
- `@DataJpaTest`: repository/data slice integration testing.
- `@ActiveProfiles("test")`: test-specific datasource/config activation.
- `@Transactional` in tests: rollback-oriented isolation per test path where applicable.

---

## Practical commands

- Run all tests: `./mvnw clean test`
- Run with profile explicitly: `./mvnw test -Dspring.profiles.active=test`
- Generate coverage report: `./mvnw test jacoco:report`

(Use `mvnw.cmd` on Windows.)

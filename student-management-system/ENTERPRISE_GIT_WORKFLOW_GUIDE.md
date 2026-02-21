# 🚀 Enterprise-Level Testing & Git Workflow Guide
## Student Management System

> **Complete guide to implementing professional testing strategies and Git workflows following industry best practices**

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Testing Strategy](#testing-strategy)
3. [Git Workflow Setup](#git-workflow-setup)
4. [Branch Protection Rules](#branch-protection-rules)
5. [Pull Request Workflow](#pull-request-workflow)
6. [Merge Conflict Resolution](#merge-conflict-resolution)
7. [CI/CD Integration](#cicd-integration)
8. [Best Practices](#best-practices)

---

## 🎯 Overview

This guide implements an **enterprise-level testing and Git workflow** for the Student Management System following industry standards used by companies like Google, Microsoft, and Netflix.

### Key Objectives

✅ **Separate testing branch** for all testing work  
✅ **Comprehensive test coverage** (Unit, Integration, Entity, Repository)  
✅ **CI/CD pipeline** with GitHub Actions  
✅ **Branch protection** to prevent direct merges  
✅ **Code review process** before merging  
✅ **Automated quality checks**

---

## 🧪 Testing Strategy

### Testing Pyramid

```
           /\
          /  \
         / UI \           ← Few (if any) - We use Thymeleaf templates
        /------\
       /        \
      /Integration\       ← Some - Controller tests with MockMvc
     /------------\
    /              \
   /   Unit Tests   \     ← Many - Service layer with Mockito
  /------------------\
 /                    \
/   Entity/Repository  \  ← Foundation - @DataJpaTest
-----------------------
```

### Test Types Implemented

#### 1. **Entity Tests** 
**Location:** `src/test/java/.../entity/*EntityTest.java`

**Purpose:** Test entity classes (POJOs)
- Getters and setters
- Constructors
- Relationships
- Business logic methods

**Example:**
```java
@DisplayName("Course Entity Tests")
class CourseEntityTest {
    
    @Test
    @DisplayName("Should create course with parameterized constructor")
    void whenCreateCourse_thenFieldsAreSet() {
        // Arrange & Act
        Course course = new Course("Database Systems", "Intro to DB");
        
        // Assert
        assertThat(course.getName()).isEqualTo("Database Systems");
        assertThat(course.getDescription()).isEqualTo("Intro to DB");
    }
}
```

**Coverage:**
- ✅ `CourseEntityTest.java` - Course entity
- ✅ `StudentEntityTest.java` - Student entity

---

#### 2. **Repository Tests** 
**Location:** `src/test/java/.../repository/*RepositoryIntegrationTest.java`

**Purpose:** Test database operations with real H2 in-memory database
- CRUD operations
- Custom query methods
- JPA relationships
- Transactional behavior

**Annotations:**
```java
@DataJpaTest                    // Configures H2 database
@ActiveProfiles("test")         // Uses application-test.yml
@DisplayName("CourseRepository Integration Tests")
class CourseRepositoryIntegrationTest {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    void whenSaveCourse_thenCourseIsPersisted() {
        // Test actual database operations
    }
}
```

**Coverage:**
- ✅ `CourseRepositoryIntegrationTest.java`
- ✅ `StudentRepositoryIntegrationTest.java`
- ✅ `DepartmentRepositoryIntegrationTest.java`

---

#### 3. **Service Unit Tests**
**Location:** `src/test/java/.../service/*ServiceTest.java`

**Purpose:** Test business logic in isolation using mocks
- Mock repository layer
- Test service methods
- Exception handling
- Edge cases

**Pattern:** AAA (Arrange-Act-Assert)

**Annotations:**
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Unit Tests")
class StudentServiceTest {
    
    @Mock
    private StudentRepository studentRepository;
    
    @InjectMocks
    private StudentService studentService;
    
    @Test
    @DisplayName("Should create student successfully")
    void whenCreateStudent_withValidData_thenStudentIsCreated() {
        // Arrange
        StudentCreateDto dto = new StudentCreateDto();
        dto.setName("John Doe");
        when(studentRepository.save(any())).thenReturn(testStudent);
        
        // Act
        Student result = studentService.createStudent(dto);
        
        // Assert
        assertThat(result).isNotNull();
        verify(studentRepository).save(any(Student.class));
    }
}
```

**Coverage:**
- ✅ `StudentServiceTest.java`
- ✅ `TeacherServiceTest.java`
- ✅ `CourseServiceTest.java`
- ✅ `DepartmentServiceTest.java`

---

#### 4. **Controller Integration Tests**
**Location:** `src/test/java/.../controller/*ControllerIntegrationTest.java`

**Purpose:** Test HTTP endpoints with full Spring context
- Request/response handling
- HTTP status codes
- Security/authorization
- Form validation
- Error handling

**Annotations:**
```java
@SpringBootTest                     // Full Spring context
@AutoConfigureMockMvc              // Configure MockMvc
@ActiveProfiles("test")
@Transactional                     // Rollback after each test
class CourseControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(username = "teacher@university.edu", roles = {"TEACHER"})
    void whenListCourses_thenReturnCoursesPage() throws Exception {
        mockMvc.perform(get("/courses"))
               .andExpect(status().isOk())
               .andExpect(view().name("courses/list"))
               .andExpect(model().attributeExists("courses"));
    }
}
```

**Coverage:**
- ✅ `CourseControllerIntegrationTest.java`
- ✅ `StudentControllerIntegrationTest.java`

---

### Test Configuration

**File:** `src/test/resources/application-test.yml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        
  h2:
    console:
      enabled: true
```

---

## 🌿 Git Workflow Setup

### Step 1: Create Testing Branch

```bash
# 1. Ensure you're on main branch
git checkout main

# 2. Pull latest changes
git pull origin main

# 3. Create and switch to testing branch
git checkout -b testing/unit-integration-tests

# 4. Verify you're on the new branch
git branch
# Output should show: * testing/unit-integration-tests
```

---

### Step 2: Implement Tests

Work on tests following this structure:

```bash
src/test/java/com/example/studentmanagementsystem/
├── entity/
│   ├── CourseEntityTest.java
│   └── StudentEntityTest.java
├── repository/
│   ├── CourseRepositoryIntegrationTest.java
│   ├── StudentRepositoryIntegrationTest.java
│   └── DepartmentRepositoryIntegrationTest.java
├── service/
│   ├── StudentServiceTest.java
│   ├── TeacherServiceTest.java
│   ├── CourseServiceTest.java
│   └── DepartmentServiceTest.java
└── controller/
    ├── CourseControllerIntegrationTest.java
    └── StudentControllerIntegrationTest.java
```

---

### Step 3: Follow Conventional Commits

#### Commit Message Format:

```
<type>(<scope>): <subject>

<optional body>
```

#### Types:
- `test:` - Adding or modifying tests
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation
- `refactor:` - Code refactoring
- `style:` - Code formatting
- `chore:` - Build process, dependencies

#### Examples:

```bash
# Entity tests
git add src/test/java/com/example/studentmanagementsystem/entity/CourseEntityTest.java
git commit -m "test(entity): add comprehensive tests for Course entity

- Add constructor tests
- Add getter/setter tests
- Add relationship tests
- Add edge case tests
- Follow AAA pattern"

# Repository tests
git add src/test/java/com/example/studentmanagementsystem/repository/CourseRepositoryIntegrationTest.java
git commit -m "test(repository): add integration tests for CourseRepository

- Test CRUD operations with H2 database
- Test JPA relationships
- Test transactional behavior
- Use @DataJpaTest annotation"

# Service tests
git add src/test/java/com/example/studentmanagementsystem/service/StudentServiceTest.java
git commit -m "test(service): add unit tests for StudentService

- Mock repository layer with Mockito
- Test business logic methods
- Test exception handling
- Cover success and failure cases"

# Controller tests
git add src/test/java/com/example/studentmanagementsystem/controller/CourseControllerIntegrationTest.java
git commit -m "test(controller): add integration tests for CourseController

- Test HTTP endpoints with MockMvc
- Verify authorization rules
- Test form validation
- Test error handling"

# CI/CD workflow
git add .github/workflows/test.yml
git commit -m "ci: add GitHub Actions workflow for automated testing

- Run tests on every PR
- Generate coverage reports
- Block merge if tests fail
- Upload test artifacts"

# Documentation
git add ENTERPRISE_GIT_WORKFLOW_GUIDE.md
git commit -m "docs: add comprehensive Git workflow guide

- Document testing strategy
- Add branch protection setup
- Include PR workflow
- Add merge conflict resolution"
```

---

### Step 4: Push Testing Branch

```bash
# Push the branch to GitHub
git push -u origin testing/unit-integration-tests

# The -u flag sets the upstream branch
# Future pushes can just use: git push
```

---

### Step 5: Verify Tests Run Locally

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=CourseEntityTest

# Run tests with coverage
mvn clean test jacoco:report

# View coverage report
# Open: target/site/jacoco/index.html
```

---

## 🛡️ Branch Protection Rules

### Configure on GitHub

**Navigate to:** `Settings` → `Branches` → `Add branch protection rule`

### Protection Settings for `main` Branch:

#### ✅ Required Settings:

1. **Branch name pattern:** `main`

2. **Protect matching branches:**
   - ✅ Require a pull request before merging
     - ✅ Require approvals: **1**
     - ✅ Dismiss stale pull request approvals when new commits are pushed
     - ✅ Require review from Code Owners (optional)
   
   - ✅ Require status checks to pass before merging
     - ✅ Require branches to be up to date before merging
     - **Required status checks:**
       - `Build & Test`
       - `Code Quality`
   
   - ✅ Require conversation resolution before merging
   
   - ✅ Require signed commits (recommended)
   
   - ✅ Require linear history (recommended)
   
   - ✅ Do not allow bypassing the above settings
     - ⚠️ **Important:** Do NOT check "Allow specified actors to bypass"

3. **Rules applied to administrators:**
   - ✅ Include administrators

4. **Restrict who can push to matching branches:**
   - Add specific users or teams (optional)
   - Leave empty to allow all with write access

---

### Visual Guide:

```
┌─────────────────────────────────────────────────┐
│  Branch Protection Rule: main                   │
├─────────────────────────────────────────────────┤
│                                                 │
│  ☑ Require pull request reviews                │
│    • Required approvals: 1                      │
│    • Dismiss stale reviews                      │
│                                                 │
│  ☑ Require status checks                       │
│    • Build & Test                               │
│    • Code Quality                               │
│    • Require branches up to date               │
│                                                 │
│  ☑ Require conversation resolution             │
│                                                 │
│  ☑ Include administrators                      │
│                                                 │
│  ☐ Allow force pushes                          │
│  ☐ Allow deletions                             │
│                                                 │
│          [Save changes]                         │
└─────────────────────────────────────────────────┘
```

---

### What These Rules Do:

| Rule | Effect |
|------|--------|
| **Require PR** | Cannot push directly to main |
| **Require approvals** | At least 1 person must review and approve |
| **Require status checks** | CI/CD tests must pass |
| **Dismiss stale reviews** | Re-review needed after new commits |
| **Conversation resolution** | All comments must be resolved |
| **Include administrators** | Even admins must follow rules |

---

## 🔄 Pull Request Workflow

### Step 1: Create Pull Request

```bash
# After implementing and committing your tests
git push origin testing/unit-integration-tests
```

**On GitHub:**

1. Navigate to your repository
2. Click "Pull requests" tab
3. Click "New pull request"
4. Set:
   - **base:** `main`
   - **compare:** `testing/unit-integration-tests`
5. Click "Create pull request"

---

### Step 2: Fill PR Template

**Title:**
```
test: implement comprehensive testing strategy
```

**Description:**
```markdown
## 🎯 Objective
Implement enterprise-level testing following industry best practices

## ✅ Changes
- ✅ Added entity tests (Course, Student)
- ✅ Added repository integration tests (Course, Student, Department)
- ✅ Added service unit tests (Student, Teacher, Course, Department)
- ✅ Added controller integration tests (Course, Student)
- ✅ Configured GitHub Actions CI/CD workflow
- ✅ Added comprehensive documentation

## 🧪 Test Coverage
- Entity Tests: 2 classes, 50+ test cases
- Repository Tests: 3 classes, 75+ test cases
- Service Tests: 4 classes, 100+ test cases
- Controller Tests: 2 classes, 40+ test cases

## 📊 Test Execution
All tests passing locally:
```bash
mvn clean test
# Results: Tests run: 265, Failures: 0, Errors: 0
```

## 🔍 Review Checklist
- [ ] All tests pass locally
- [ ] Code follows AAA pattern
- [ ] Proper use of annotations
- [ ] Comprehensive coverage
- [ ] Documentation updated
- [ ] Conventional commit format

## 📝 Related Issues
Closes #XXX
```

---

### Step 3: CI/CD Checks Run Automatically

GitHub Actions will automatically:

1. ✅ Checkout code
2. ✅ Set up Java 17
3. ✅ Run `mvn clean test`
4. ✅ Generate coverage report
5. ✅ Upload test results
6. ✅ Post results to PR

**Status Checks:**
```
✓ Build & Test — Test passed
✓ Code Quality — Build passed
✓ Security Scan — No vulnerabilities
```

---

### Step 4: Code Review Process

**Reviewer Checklist:**

#### Code Quality
- [ ] Tests follow AAA pattern
- [ ] Proper use of JUnit 5 and Mockito
- [ ] Meaningful test names
- [ ] Good code coverage
- [ ] No redundant tests

#### Technical Review
- [ ] Correct annotations used
- [ ] Proper mocking strategy
- [ ] Transaction handling correct
- [ ] No hard-coded values
- [ ] Tests are isolated

#### Documentation
- [ ] JavaDoc comments
- [ ] README updated
- [ ] Conventional commits

---

### Step 5: Request Changes or Approve

**If changes needed:**
```markdown
## 📝 Requested Changes

### CourseServiceTest.java
Line 45: Consider adding edge case for null department
```suggestion
@Test
void whenCreateCourse_withNullDepartment_thenThrowException() {
    // Test implementation
}
```

**After addressing:**
```bash
# Make changes
git add .
git commit -m "test(service): add null department test case"
git push
```

**If approved:**
- Reviewer clicks "Approve"
- Green checkmark appears
- "Merge pull request" becomes available

---

### Step 6: Merge Pull Request

**Merge Strategies:**

#### 1. **Squash and Merge** (Recommended for feature branches)
```
✓ Clean commit history
✓ All commits combined into one
✓ Easy to revert
```

#### 2. **Rebase and Merge** (For linear history)
```
✓ Linear commit history
✓ Individual commits preserved
✓ No merge commit
```

#### 3. **Create Merge Commit** (Traditional)
```
✓ Preserves full history
✓ Shows branch relationship
✗ Can clutter history
```

**For this project, use:** **Squash and Merge**

---

### Step 7: Delete Branch

After merge:
```bash
# On GitHub: Click "Delete branch" button

# Locally:
git checkout main
git pull origin main
git branch -d testing/unit-integration-tests
```

---

## ⚔️ Merge Conflict Resolution

### Scenario: Intentional Conflict

#### Step 1: Create Conflict

**On `main` branch:**
```bash
git checkout main
# Edit README.md - add line at line 10
git add README.md
git commit -m "docs: update README on main"
git push origin main
```

**On `testing/unit-integration-tests` branch:**
```bash
git checkout testing/unit-integration-tests
# Edit README.md - add DIFFERENT line at line 10
git add README.md
git commit -m "docs: update README on testing branch"
git push origin testing/unit-integration-tests
```

**Create PR → GitHub shows: "This branch has conflicts that must be resolved"**

---

#### Step 2: Resolve Locally

```bash
# On testing branch
git checkout testing/unit-integration-tests

# Fetch latest from main
git fetch origin main

# Attempt to merge main into your branch
git merge origin/main
# Output: Auto-merging README.md
#         CONFLICT (content): Merge conflict in README.md

# Check status
git status
# Output: Unmerged paths:
#         both modified:   README.md
```

---

#### Step 3: Edit Conflicted File

```bash
# Open README.md in editor
code README.md
```

**You'll see:**
```markdown
# Normal content

<<<<<<< HEAD (Current - testing branch)
## Testing Strategy
This project uses comprehensive testing
=======
## Documentation
This project has extensive documentation
>>>>>>> origin/main (Incoming - main branch)

# More content
```

**Choose resolution:**

Option 1: Keep testing branch version
```markdown
## Testing Strategy
This project uses comprehensive testing
```

Option 2: Keep main branch version
```markdown
## Documentation
This project has extensive documentation
```

Option 3: Keep both (merge manually)
```markdown
## Documentation
This project has extensive documentation

## Testing Strategy
This project uses comprehensive testing
```

**Remove conflict markers:**
- Remove `<<<<<<< HEAD`
- Remove `=======`
- Remove `>>>>>>> origin/main`

---

#### Step 4: Complete Merge

```bash
# After resolving conflicts
git add README.md

# Commit the merge
git commit -m "merge: resolve conflict in README.md

- Combined documentation and testing sections
- Kept both changes from main and testing branch"

# Push resolved changes
git push origin testing/unit-integration-tests
```

**GitHub PR now shows:** "✓ All checks have passed - This branch has no conflicts with the base branch"

---

#### Step 5: Resolve on GitHub (Alternative)

1. On PR page, click "Resolve conflicts"
2. GitHub opens web editor
3. Edit file to resolve conflicts
4. Click "Mark as resolved"
5. Click "Commit merge"
6. Conflicts resolved!

---

### Conflict Resolution Best Practices

#### ✅ DO:
- Pull latest main frequently
- Resolve conflicts promptly
- Test after resolution
- Review resolved changes carefully
- Communicate with team

#### ❌ DON'T:
- Ignore conflicts
- Force push (`git push -f`)
- Delete others' changes blindly
- Resolve without understanding
- Skip testing after resolution

---

### When to Use Rebase vs Merge

#### Use **Merge** when:
- Working on feature branch
- Want to preserve branch history
- Collaborating with others on same branch

```bash
git merge origin/main
```

#### Use **Rebase** when:
- Want linear history
- Working alone on branch
- Before creating PR

```bash
git rebase origin/main
```

**⚠️ Never rebase public branches!**

---

## 🤖 CI/CD Integration

### GitHub Actions Workflow

**File:** `.github/workflows/test.yml`

**Workflow triggers:**
```yaml
on:
  pull_request:
    branches: [ main ]
  push:
    branches: [ main ]
  workflow_dispatch:  # Manual trigger
```

---

### Workflow Jobs

#### Job 1: Build & Test
```yaml
- Checkout code
- Setup JDK 17
- Cache Maven dependencies
- Run mvn clean test
- Generate coverage report
- Upload test results
- Upload JAR artifact
```

#### Job 2: Code Quality
```yaml
- Verify compilation
- Run code style checks
```

#### Job 3: Summary
```yaml
- Generate pipeline summary
- Fail if tests failed
```

---

### Viewing CI/CD Results

**On Pull Request:**

1. **Checks tab** shows all running jobs
2. **Details** link shows logs
3. **Test results** posted as comment
4. **Coverage report** as artifact

**Status badges:**
```
✓ Build & Test passing
✓ Code Quality passing
✗ Coverage below 50%
```

---

### Ensuring PR Cannot Merge if Tests Fail

**Configured in two places:**

1. **Workflow file:**
```yaml
- name: Run tests
  run: mvn test
  # Job fails if tests fail
```

2. **Branch Protection:**
- Require status checks: `Build & Test`
- Blocks merge if check fails

**Result:** Merge button disabled until all checks pass

---

## 📚 Best Practices

### Testing Best Practices

#### 1. **AAA Pattern**
```java
@Test
void testName() {
    // Arrange - Set up test data
    Student student = new Student("John", "123", "john@email.com", "pass");
    
    // Act - Execute the method being tested
    Student result = studentService.createStudent(student);
    
    // Assert - Verify the outcome
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("John");
}
```

#### 2. **Test Naming Convention**
```java
// Pattern: when[Action]_[Condition]_then[Expected]

@Test
void whenCreateStudent_withValidData_thenStudentIsCreated() { }

@Test
void whenFindById_withNonExistentId_thenThrowException() { }
```

#### 3. **One Assertion Per Test** (Flexible)
```java
// ✅ Good - Related assertions
@Test
void whenSaveStudent_thenStudentIsPersisted() {
    Student saved = repository.save(student);
    
    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("John");
}

// ❌ Bad - Unrelated assertions
@Test
void testMultipleThings() {
    // Testing save
    assertThat(repository.save(student)).isNotNull();
    
    // Testing find - should be separate test
    assertThat(repository.findById(1L)).isPresent();
}
```

#### 4. **Use Nested Tests**
```java
@Nested
@DisplayName("Create Operations")
class CreateOperations {
    
    @Test
    void testCreate() { }
    
    @Test
    void testCreateWithNull() { }
}

@Nested
@DisplayName("Update Operations")
class UpdateOperations {
    
    @Test
    void testUpdate() { }
}
```

---

### Git Best Practices

#### 1. **Commit Frequently**
```bash
# ✅ Good - Small, focused commits
git commit -m "test(entity): add constructor tests for Course"
git commit -m "test(entity): add getter/setter tests for Course"
git commit -m "test(entity): add relationship tests for Course"

# ❌ Bad - One huge commit
git commit -m "add all tests"
```

#### 2. **Write Meaningful Commit Messages**
```bash
# ✅ Good
test(service): add null handling for StudentService.createStudent

Added validation to handle null input and throw appropriate exception.
This prevents NullPointerException in production.

# ❌ Bad
git commit -m "fix stuff"
git commit -m "WIP"
git commit -m "updates"
```

#### 3. **Pull Before Push**
```bash
# ✅ Good workflow
git pull origin testing/unit-integration-tests
# Resolve any conflicts
git push origin testing/unit-integration-tests

# ❌ Bad - Force push destroys others' work
git push -f origin testing/unit-integration-tests
```

#### 4. **Branch Naming Convention**
```bash
# Pattern: <type>/<description>

feature/user-authentication
bugfix/login-validation
test/unit-integration-tests
hotfix/security-vulnerability
docs/api-documentation
```

---

### Code Review Best Practices

#### For Reviewers:

✅ **Do:**
- Review within 24 hours
- Be constructive and specific
- Ask questions, don't demand
- Approve when ready
- Use suggestion feature

❌ **Don't:**
- Nitpick minor style issues
- Request perfection
- Block PR unnecessarily
- Be rude or dismissive

#### For Authors:

✅ **Do:**
- Respond to all comments
- Explain your decisions
- Make requested changes
- Thank reviewers
- Mark conversations resolved

❌ **Don't:**
- Take feedback personally
- Ignore comments
- Argue excessively
- Force merge

---

## 🎓 Summary

### What We've Accomplished

✅ **Testing Strategy**
- Entity tests for data models
- Repository tests with H2 database
- Service unit tests with Mockito
- Controller integration tests with MockMvc

✅ **Git Workflow**
- Separate testing branch
- Conventional commit format
- Professional PR process

✅ **Branch Protection**
- Require PR reviews
- Require passing tests
- Block direct merges

✅ **CI/CD Pipeline**
- Automated testing on PRs
- Coverage reporting
- Merge blocking on failures

✅ **Documentation**
- Comprehensive guides
- Best practices
- Conflict resolution

---

### Testing Commands Reference

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=CourseEntityTest

# Run tests with coverage
mvn clean test jacoco:report

# Run tests and show output
mvn test -Dtest=CourseEntityTest -Dmaven.test.failure.ignore=true

# Skip tests during build
mvn clean install -DskipTests

# View coverage report
# Open: target/site/jacoco/index.html
```

---

### Git Commands Reference

```bash
# Create branch
git checkout -b testing/unit-integration-tests

# Add changes
git add .
git add <specific-file>

# Commit
git commit -m "type(scope): message"

# Push
git push origin testing/unit-integration-tests

# Pull latest
git pull origin main

# Check status
git status

# View log
git log --oneline

# Merge main into your branch
git merge origin/main

# Rebase (use carefully)
git rebase origin/main

# Delete branch
git branch -d testing/unit-integration-tests
```

---

## 📞 Need Help?

- **GitHub Issues:** Use for bug reports and feature requests
- **Pull Requests:** Use for code contributions
- **Discussions:** Use for questions and ideas
- **Wiki:** Check for detailed documentation

---

**Last Updated:** 2026-02-21  
**Version:** 1.0.0  
**Author:** Enterprise Development Team

---


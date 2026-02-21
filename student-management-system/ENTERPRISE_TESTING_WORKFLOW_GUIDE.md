# Enterprise-Level Testing & Git Workflow Guide
## Student Management System

---

## 📚 Table of Contents
1. [Testing Strategy](#testing-strategy)
2. [Git Workflow](#git-workflow)
3. [Branch Protection Rules](#branch-protection-rules)
4. [Pull Request Process](#pull-request-process)
5. [Merge Conflict Resolution](#merge-conflict-resolution)
6. [CI/CD Integration](#cicd-integration)
7. [Best Practices](#best-practices)

---

## 🎯 Testing Strategy

### Test Pyramid

```
           /\
          /  \  E2E Tests (Few)
         /----\
        /      \  Integration Tests (Some)
       /--------\
      /          \  Unit Tests (Many)
     /____________\
```

### Test Types Implemented

#### 1. **Unit Tests** (Service Layer)
- **Purpose**: Test business logic in isolation
- **Tools**: JUnit 5, Mockito
- **Location**: `src/test/java/.../service/*Test.java`
- **Coverage**: StudentService, TeacherService, CourseService, DepartmentService
- **Pattern**: AAA (Arrange-Act-Assert)

**Example:**
```java
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
```

#### 2. **Integration Tests** (Repository Layer)
- **Purpose**: Test database operations
- **Tools**: @DataJpaTest, H2 Database
- **Location**: `src/test/java/.../repository/*IntegrationTest.java`
- **Coverage**: StudentRepository, DepartmentRepository

**Example:**
```java
@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryIntegrationTest {
    @Test
    void whenSaveStudent_thenStudentIsPersisted() {
        Student saved = studentRepository.save(testStudent);
        assertThat(saved.getId()).isNotNull();
    }
}
```

#### 3. **Controller Integration Tests**
- **Purpose**: Test HTTP endpoints with security
- **Tools**: @SpringBootTest, MockMvc
- **Location**: `src/test/java/.../controller/*IntegrationTest.java`
- **Coverage**: StudentController, DepartmentController

**Example:**
```java
@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerIntegrationTest {
    @Test
    @WithMockUser(roles = "STUDENT")
    void whenListStudents_thenReturn200() throws Exception {
        mockMvc.perform(get("/students"))
            .andExpect(status().isOk());
    }
}
```

#### 4. **Entity Tests**
- **Purpose**: Test POJO behavior
- **Tools**: JUnit 5, AssertJ
- **Location**: `src/test/java/.../entity/*Test.java`
- **Coverage**: Student, Department entities

---

## 🌿 Git Workflow

### Branching Strategy

```
main (protected)
  │
  ├── testing/unit-integration-tests (feature branch)
  │   ├── Add repository tests
  │   ├── Add service unit tests
  │   ├── Add controller integration tests
  │   └── Add entity tests
  │
  └── feature/another-feature
```

### Step-by-Step Git Commands

#### **STEP 1: Create Testing Branch from Main**

```bash
# Ensure you're on main and it's up to date
git checkout main
git pull origin main

# Create and switch to testing branch
git checkout -b testing/unit-integration-tests

# Verify you're on the correct branch
git branch
# Output: * testing/unit-integration-tests
```

#### **STEP 2: Make Changes and Commit (Conventional Commits)**

```bash
# Check status of changes
git status

# Add specific files (recommended)
git add src/test/java/com/example/studentmanagementsystem/repository/
git add src/test/resources/application-test.yml
git add pom.xml

# OR add all changes
git add .

# Commit with conventional commit format
git commit -m "test: add integration tests for StudentRepository

- Add @DataJpaTest for database operations
- Test CRUD operations with H2 in-memory database
- Cover findByEmail and findByRoll custom queries
- Ensure transactional rollback after tests"

# View commit history
git log --oneline
```

#### **STEP 3: Continue Development with Multiple Commits**

```bash
# Add service unit tests
git add src/test/java/com/example/studentmanagementsystem/service/
git commit -m "test: add unit tests for StudentService

- Mock repository layer with Mockito
- Test create, update, delete operations
- Cover authorization logic
- Test exception handling scenarios"

# Add controller integration tests
git add src/test/java/com/example/studentmanagementsystem/controller/
git commit -m "test: add integration tests for StudentController

- Use MockMvc for HTTP endpoint testing
- Test security with @WithMockUser
- Verify status codes and response bodies
- Test CSRF protection"

# Add entity tests
git add src/test/java/com/example/studentmanagementsystem/entity/
git commit -m "test: add unit tests for Student entity

- Test getters and setters
- Test constructor behavior
- Test bidirectional relationship helpers
- Cover edge cases"

# Add CI/CD workflow
git add .github/workflows/test.yml
git commit -m "ci: add GitHub Actions workflow for automated testing

- Run tests on pull requests to main
- Upload test results and coverage reports
- Fail PR if tests don't pass"

# Update dependencies
git add pom.xml
git commit -m "build: add H2 database and test dependencies

- Add H2 for in-memory testing
- Add Mockito for mocking
- Configure JaCoCo for coverage reporting
- Configure Maven Surefire for test execution"
```

#### **STEP 4: Push Branch to Remote**

```bash
# Push branch to GitHub for the first time
git push -u origin testing/unit-integration-tests

# For subsequent pushes
git push
```

#### **STEP 5: Create Pull Request**

**Via Command Line (using GitHub CLI):**
```bash
# Install GitHub CLI first: https://cli.github.com/

# Create PR
gh pr create --base main --head testing/unit-integration-tests \
  --title "Add comprehensive unit and integration tests" \
  --body "## Changes
- ✅ Added repository integration tests (StudentRepository, DepartmentRepository)
- ✅ Added service unit tests (StudentService, DepartmentService)
- ✅ Added controller integration tests (StudentController)
- ✅ Added entity tests (Student)
- ✅ Configured H2 database for testing
- ✅ Added GitHub Actions CI/CD workflow
- ✅ All 150+ tests passing

## Testing Coverage
- Unit Tests: 94 tests
- Integration Tests: 40+ tests
- Entity Tests: 30+ tests
- Total: 150+ tests

## Checklist
- [x] All tests pass locally
- [x] Code follows conventions
- [x] Documentation updated
- [x] CI/CD configured"
```

**Via GitHub Web UI:**
1. Go to your repository on GitHub
2. Click "Pull requests" tab
3. Click "New pull request"
4. Set base: `main`, compare: `testing/unit-integration-tests`
5. Click "Create pull request"
6. Fill in title and description
7. Add reviewers (yourself or team members)
8. Click "Create pull request"

---

## 🛡️ Branch Protection Rules

### Configuring Protection for `main` Branch

#### **Step-by-Step in GitHub UI:**

1. **Navigate to Settings**
   - Go to your repository on GitHub
   - Click **Settings** (top right)
   - Click **Branches** (left sidebar)

2. **Add Branch Protection Rule**
   - Click **Add rule** or **Add branch protection rule**
   - Branch name pattern: `main`

3. **Configure Protection Rules**

   **✅ Require pull request before merging**
   - Check this box
   - Required approvals: `1` (minimum)
   - ✅ Dismiss stale pull request approvals when new commits are pushed
   - ✅ Require review from Code Owners (optional)

   **✅ Require status checks to pass**
   - Check this box
   - Search and select: `CI - Test Suite / test` (from GitHub Actions)
   - ✅ Require branches to be up to date before merging

   **✅ Require conversation resolution before merging**
   - Check this box (ensures all comments are addressed)

   **✅ Require signed commits** (optional but recommended)
   - Check this box for extra security

   **✅ Require linear history** (optional)
   - Check this box to prevent merge commits

   **✅ Do not allow bypassing the above settings**
   - Check this box
   - Specify who can bypass (e.g., admins only)

   **❌ Allow force pushes** - UNCHECK
   - Never allow force pushes to main

   **❌ Allow deletions** - UNCHECK
   - Prevent accidental branch deletion

4. **Save Changes**
   - Click **Create** or **Save changes**

### Verification

After setting up, try to:
- ❌ Push directly to main → Should be blocked
- ❌ Merge PR without approval → Should be blocked
- ❌ Merge PR with failing tests → Should be blocked
- ✅ Merge PR with approval and passing tests → Should succeed

---

## 🔄 Pull Request Process

### 1. **Create PR** (Already covered above)

### 2. **Automated Checks**

Once PR is created, GitHub Actions will automatically:
- ✅ Run all tests (`mvn clean test`)
- ✅ Generate coverage report
- ✅ Upload test results
- ✅ Report status back to PR

**PR Status Indicators:**
```
✅ CI - Test Suite / test — Checks have passed
✅ CI - Test Suite / build — Checks have passed
```

### 3. **Code Review**

**As PR Author:**
- Respond to comments
- Make requested changes
- Push new commits (they'll trigger CI again)

**As Reviewer:**
- Review code changes
- Leave comments/suggestions
- Request changes or approve

**Review Commands:**
```bash
# Checkout PR locally to test
git fetch origin
git checkout testing/unit-integration-tests

# Run tests locally
mvn clean test

# Return to main
git checkout main
```

### 4. **Request Review**

```bash
# Using GitHub CLI
gh pr review --approve
gh pr review --request-changes --body "Please fix XYZ"

# Or use GitHub UI: Click "Review changes" → "Approve"
```

### 5. **Merge PR**

**After approval and passing checks:**

**Option A: Merge via GitHub UI**
1. Go to PR page
2. Click **Merge pull request**
3. Choose merge strategy:
   - **Create a merge commit** (recommended for features)
   - **Squash and merge** (clean history, single commit)
   - **Rebase and merge** (linear history)
4. Click **Confirm merge**

**Option B: Merge via CLI**
```bash
# Squash and merge (recommended)
gh pr merge --squash --delete-branch

# Regular merge
gh pr merge --merge --delete-branch

# Rebase and merge
gh pr merge --rebase --delete-branch
```

### 6. **Clean Up**

```bash
# Delete local branch
git checkout main
git branch -d testing/unit-integration-tests

# Pull latest main
git pull origin main

# Remote branch is usually auto-deleted by GitHub
```

---

## 🔧 Merge Conflict Resolution

### Scenario: Intentional Conflict

Let's simulate a merge conflict for learning purposes.

#### **Create Conflict**

1. **On `main` branch:**
```bash
git checkout main

# Edit README.md
echo "# Main Branch Update" >> README.md
git add README.md
git commit -m "docs: update README on main"
git push origin main
```

2. **On `testing/unit-integration-tests` branch:**
```bash
git checkout testing/unit-integration-tests

# Edit the SAME file, SAME lines
echo "# Testing Branch Update" >> README.md
git add README.md
git commit -m "docs: update README on testing branch"
git push origin testing/unit-integration-tests
```

3. **Create PR** → GitHub will show **"This branch has conflicts that must be resolved"**

#### **Resolution Method 1: Resolve in GitHub UI**

1. On PR page, click **Resolve conflicts**
2. GitHub shows conflicted file:
```markdown
<<<<<<< testing/unit-integration-tests
# Testing Branch Update
=======
# Main Branch Update
>>>>>>> main
```
3. Edit to keep both or choose one:
```markdown
# Main Branch Update
# Testing Branch Update
```
4. Click **Mark as resolved**
5. Click **Commit merge**

#### **Resolution Method 2: Resolve Locally (Recommended)**

```bash
# Switch to your feature branch
git checkout testing/unit-integration-tests

# Fetch latest changes from remote
git fetch origin

# Attempt to merge main into your branch
git merge origin/main

# Git will report conflicts:
# Auto-merging README.md
# CONFLICT (content): Merge conflict in README.md

# View conflicted files
git status

# Open README.md in editor and look for conflict markers
<<<<<<< HEAD
# Testing Branch Update
=======
# Main Branch Update
>>>>>>> origin/main

# Manually resolve by editing the file
# Keep both, or choose one:
# Main Branch Update
# Testing Branch Update

# After resolving, add the file
git add README.md

# Complete the merge
git commit -m "merge: resolve conflict in README.md"

# Push resolved changes
git push origin testing/unit-integration-tests
```

#### **Resolution Method 3: Using Rebase (Clean History)**

```bash
# Switch to your feature branch
git checkout testing/unit-integration-tests

# Rebase onto main
git rebase origin/main

# If conflicts occur, Git will pause
# Resolve conflicts in files
# Then:
git add <conflicted-file>
git rebase --continue

# Force push (rewriting history)
git push --force-with-lease origin testing/unit-integration-tests
```

### Best Practices for Conflict Prevention

1. **Pull frequently**
```bash
git checkout main
git pull origin main
git checkout testing/unit-integration-tests
git merge main
```

2. **Small, focused PRs** - Easier to review and merge

3. **Communicate** - Let team know which files you're working on

4. **Keep branches short-lived** - Merge within days, not weeks

---

## 🚀 CI/CD Integration

### GitHub Actions Workflow Explained

Our workflow file `.github/workflows/test.yml`:

```yaml
name: CI - Test Suite

on:
  pull_request:
    branches: [main]  # Run on PRs to main
  push:
    branches: [main]  # Run on pushes to main

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
      - name: Run tests
        run: mvn clean test
```

### How It Works

1. **Trigger**: PR created or code pushed
2. **Runs**: GitHub provisions Ubuntu VM
3. **Checks out code**: Latest commit
4. **Sets up Java 17**: Installs JDK
5. **Runs tests**: `mvn clean test`
6. **Reports**: Success ✅ or Failure ❌

### Viewing Test Results

1. Go to **Actions** tab in GitHub
2. Click on workflow run
3. View logs, download artifacts

### Enforcing CI in Branch Protection

In branch protection rules:
- ✅ Require status checks to pass before merging
- Select: `CI - Test Suite / test`

Now PRs **cannot merge** if tests fail!

---

## 📋 Best Practices

### Conventional Commits

Format: `<type>(<scope>): <subject>`

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Maintenance
- `ci`: CI/CD changes
- `build`: Build system changes

**Examples:**
```bash
git commit -m "test: add unit tests for StudentService"
git commit -m "feat(auth): implement JWT authentication"
git commit -m "fix(student): resolve duplicate email bug"
git commit -m "docs: update README with testing guide"
git commit -m "ci: add code coverage threshold"
```

### Code Review Checklist

**Before Creating PR:**
- [ ] All tests pass locally
- [ ] Code follows project conventions
- [ ] No console.log() or debug statements
- [ ] Documentation updated
- [ ] Commit messages follow conventions

**During Review:**
- [ ] Code is readable and maintainable
- [ ] Tests cover new functionality
- [ ] No security vulnerabilities
- [ ] Performance considerations addressed
- [ ] Edge cases handled

### Testing Checklist

- [ ] **Unit tests** for business logic
- [ ] **Integration tests** for database operations
- [ ] **Controller tests** for HTTP endpoints
- [ ] **Entity tests** for POJO behavior
- [ ] **Security tests** for authorization
- [ ] **Edge cases** covered
- [ ] **Exception handling** tested

---

## 📊 Testing Metrics

### Coverage Goals

- **Line Coverage**: > 80%
- **Branch Coverage**: > 70%
- **Service Layer**: 100%
- **Repository Layer**: 90%+

### Running Coverage Report

```bash
# Generate coverage report
mvn clean test jacoco:report

# View report
# Open: target/site/jacoco/index.html
```

### Quality Gates

Our project enforces:
- ✅ All tests must pass
- ✅ Minimum 50% code coverage (configurable in pom.xml)
- ✅ No critical security vulnerabilities
- ✅ All PR comments resolved

---

## 🎓 Summary

### Git Workflow Summary

```bash
# 1. Create branch
git checkout -b testing/unit-integration-tests

# 2. Make changes and commit
git add .
git commit -m "test: add integration tests"

# 3. Push to remote
git push -u origin testing/unit-integration-tests

# 4. Create PR (via GitHub UI or CLI)
gh pr create

# 5. Wait for review and CI checks

# 6. Resolve any conflicts if needed
git merge origin/main

# 7. Merge PR after approval

# 8. Clean up
git checkout main
git pull
git branch -d testing/unit-integration-tests
```

### Testing Strategy Summary

1. **Unit Tests**: Test business logic in isolation (Mockito)
2. **Integration Tests**: Test with real database (H2)
3. **Controller Tests**: Test HTTP endpoints (MockMvc)
4. **Entity Tests**: Test POJO behavior
5. **CI/CD**: Automated testing on every PR

---

## 📚 Additional Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [GitHub Actions](https://docs.github.com/en/actions)

---

**Last Updated**: February 2026  
**Status**: ✅ Production Ready

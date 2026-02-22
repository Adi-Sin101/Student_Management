# GitHub Actions Workflow Guide

## Workflow file and purpose

- Workflow file: `.github/workflows/tests.yml` (repository root).
- Primary goal: run compile + test checks automatically on pushes and pull requests.
- Secondary goals: publish machine-readable test reports and upload test artifacts.

---

## 1) What is happening in this workflow

The workflow performs the following sequence:

1. Trigger on:
   - `push` to `main`, `master`, `develop`, `feature/**`, `testing/**`
   - `pull_request` to `main`, `master`, `develop`
2. Start `test` job on `ubuntu-latest`.
3. Set `working-directory` to `student-management-system`.
4. Checkout repository.
5. Install JDK 17 (`temurin`) with Maven dependency caching.
6. Make `mvnw` executable.
7. Build project (`./mvnw -B clean compile`).
8. Run tests (`./mvnw -B test`) with `SPRING_PROFILES_ACTIVE=test`.
9. Parse Surefire XML with `dorny/test-reporter` and publish check results.
10. Upload `target/surefire-reports/` as artifact for troubleshooting.

---

## 2) Why this was done

- Enforce automated quality checks for every important branch and PR.
- Prevent accidental merges with failing tests.
- Provide rapid feedback and visibility for maintainers/reviewers.
- Ensure consistent Java version and build environment in CI.
- Preserve evidence (reports/artifacts) for failed pipeline analysis.

---

## 3) How this was implemented

### Key implementation details

- Uses `actions/checkout@v4` for source retrieval.
- Uses `actions/setup-java@v4` with:
  - `java-version: '17'`
  - `distribution: 'temurin'`
  - Maven cache and dependency path (`student-management-system/pom.xml`).
- Uses Maven Wrapper (`mvnw`) so Maven version is project-controlled.
- Uses batch mode (`-B`) for non-interactive CI-safe logs.
- Uses test profile environment variable to activate H2-backed test configuration.
- Uses `if: always()` on report/artifact steps to preserve output even on failures.

### Path correctness

All CI paths are intentionally aligned to monorepo-style layout:

- Build/test commands execute in `student-management-system`.
- Test report read path: `student-management-system/target/surefire-reports/*.xml`.
- Artifact upload path: `student-management-system/target/surefire-reports/`.

---

## 4) Branch protection: how and why

### Why

Branch protection prevents bypassing CI quality checks and enforces stable integration flow.

### Recommended protection rules

For `main` (and optionally `develop`):

- Require pull request before merging.
- Require status checks to pass before merging.
- Select the workflow job status check (the `test` job from this workflow).
- Require up-to-date branch before merging.
- Include administrators (optional but recommended for strict governance).
- Restrict force-pushes and deletions.

### How to configure (GitHub UI)

1. Repository **Settings** → **Branches**.
2. Add branch protection rule for `main`.
3. Enable required pull request reviews.
4. Enable required status checks and choose test check from this workflow.
5. Save rule.

---

## 5) How testing is integrated into workflow

Integration model:

- Maven Surefire runs test suite in CI.
- Spring `test` profile ensures test environment consistency.
- XML outputs from Surefire are consumed by test reporter action.
- GitHub checks annotate pass/fail results in PR context.
- Artifacts preserve detailed outputs for diagnosis.

This creates a continuous loop:

1. Developer pushes or opens PR.
2. CI compiles and tests in controlled environment.
3. Results become visible on commit/PR checks.
4. Branch protection blocks merge until checks are green.

---

## Troubleshooting checklist

- Verify `working-directory` matches actual project folder.
- Verify test XML files exist under `target/surefire-reports`.
- Verify JDK 17 compatibility with project plugins/dependencies.
- Verify `application-test.yml` is valid for CI execution.
- Verify no path typo in artifact and reporter steps.
- Re-run failed jobs using artifact logs for root-cause analysis.

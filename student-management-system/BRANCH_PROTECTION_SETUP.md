
# 🛡️ GitHub Branch Protection Rules Setup Guide
## Complete Configuration for Enterprise-Level Security

> **Protect your main branch from accidental changes and enforce code quality standards**

---

## 📋 Table of Contents

1. [Why Branch Protection?](#why-branch-protection)
2. [Prerequisites](#prerequisites)
3. [Step-by-Step Setup](#step-by-step-setup)
4. [Configuration Options Explained](#configuration-options-explained)
5. [Testing Protection Rules](#testing-protection-rules)
6. [Common Scenarios](#common-scenarios)
7. [Troubleshooting](#troubleshooting)
8. [Best Practices](#best-practices)

---

## 🎯 Why Branch Protection?

### The Problem

**Without Protection:**
```bash
# Developer can directly push to main
git checkout main
git add .
git commit -m "quick fix"
git push origin main   # ✅ Allowed (BAD!)

# No code review
# No tests run
# Production breaks
```

**With Protection:**
```bash
# Developer tries to push to main
git push origin main   
# ❌ ERROR: Protected branch - use Pull Request

# Must:
# 1. Create feature branch
# 2. Make changes
# 3. Create Pull Request
# 4. Pass CI/CD tests
# 5. Get code review
# 6. Then merge
```

---

### Benefits

✅ **Code Quality**
- All code reviewed before merge
- Automated tests must pass
- Style and lint checks enforced

✅ **Prevent Accidents**
- No direct pushes to main
- No force pushes
- No accidental deletions

✅ **Team Collaboration**
- Mandatory code reviews
- Discussion on changes
- Knowledge sharing

✅ **Compliance**
- Audit trail of changes
- Required approvals
- Signed commits (optional)

---

## 📝 Prerequisites

Before setting up branch protection:

### 1. **Repository Setup**

```bash
# Ensure repository exists on GitHub
# URL: https://github.com/YOUR-USERNAME/student-management-system

# Verify main branch exists
git branch -a
# Should show: * main
```

### 2. **GitHub Permissions**

You need **Admin** or **Write** access:

```
Repository → Settings → Manage access
Your role: Admin ✓
```

### 3. **CI/CD Workflow**

Ensure GitHub Actions workflow exists:

```
.github/workflows/test.yml ✓
```

The workflow should have named jobs:
- `Build & Test`
- `Code Quality`

---

## 🚀 Step-by-Step Setup

### Step 1: Navigate to Settings

1. Go to your repository on GitHub
2. Click **"Settings"** tab (top right)
3. In left sidebar, click **"Branches"** (under "Code and automation")

**URL Pattern:**
```
https://github.com/YOUR-USERNAME/student-management-system/settings/branches
```

---

### Step 2: Add Branch Protection Rule

1. Click **"Add branch protection rule"** button
2. You'll see the protection rule form

```
┌─────────────────────────────────────────────────────┐
│  Branch protection rule                             │
│                                                     │
│  Branch name pattern                                │
│  ┌───────────────────────────────────────────────┐ │
│  │ main                                          │ │
│  └───────────────────────────────────────────────┘ │
│                                                     │
│  Protect matching branches                         │
│  ☐ Require a pull request before merging           │
│  ☐ Require status checks to pass before merging    │
│  ☐ Require conversation resolution before merging  │
│  ☐ Require signed commits                          │
│  ☐ Require linear history                          │
│  ☐ Require deployments to succeed before merging   │
│  ☐ Lock branch                                     │
│  ☐ Do not allow bypassing the above settings       │
│  ☐ Restrict who can push to matching branches      │
│  ☐ Allow force pushes                              │
│  ☐ Allow deletions                                 │
│                                                     │
│           [Create]  [Cancel]                        │
└─────────────────────────────────────────────────────┘
```

---

### Step 3: Configure Basic Protection

#### 3.1 Set Branch Pattern

```
Branch name pattern: main
```

**Tip:** You can use patterns:
- `main` - Only main branch
- `release/*` - All release branches
- `main,develop` - Multiple specific branches

---

#### 3.2 Require Pull Requests

✅ **Check:** "Require a pull request before merging"

This expands to show sub-options:

```
☑ Require a pull request before merging
  
  Additional pull request settings:
  
  ☑ Require approvals
    Required number of approvals before merging: [1▼]
  
  ☑ Dismiss stale pull request approvals when new commits are pushed
  
  ☐ Require review from Code Owners
  
  ☐ Restrict who can dismiss pull request reviews
  
  ☐ Allow specified actors to bypass required pull requests
  
  ☑ Require approval of the most recent reviewable push
```

**Recommended Settings:**
- ✅ Require approvals: **1**
- ✅ Dismiss stale approvals
- ✅ Require approval of most recent push
- ⬜ Require Code Owners (optional, for larger teams)

---

#### 3.3 Require Status Checks

✅ **Check:** "Require status checks to pass before merging"

This expands:

```
☑ Require status checks to pass before merging
  
  ☑ Require branches to be up to date before merging
  
  Status checks that are required:
  
  Search for status checks...
  ┌─────────────────────────────────────┐
  │ 🔨 Build & Test                    │  ← Click to add
  │ 🔍 Code Quality                    │  ← Click to add
  └─────────────────────────────────────┘
```

**To add status checks:**

1. Type in search box: "Build & Test"
2. Click on the check when it appears
3. Repeat for "Code Quality"

**Note:** Status checks only appear after they've run at least once. If you don't see them:
1. Create a Pull Request first
2. Let GitHub Actions run
3. Come back and add the checks

---

#### 3.4 Additional Protection

✅ **Check:** "Require conversation resolution before merging"
- All PR comments must be resolved before merging

✅ **Check:** "Do not allow bypassing the above settings"
- Even admins must follow the rules

**Optional (Recommended):**

✅ "Require linear history"
- Forces squash merges or rebases
- Keeps history clean

✅ "Require signed commits"
- Extra security
- Requires GPG key setup

---

### Step 4: Configure Advanced Options

#### 4.1 Restrictions

**Restrict who can push to matching branches:**

```
☑ Restrict who can push to matching branches

  Restrict pushes that create matching branches
  
  People, teams, or apps with push access:
  ┌─────────────────────────────────────┐
  │ Search for people, teams, or apps  │
  │                                     │
  │ [Add people or teams]              │
  └─────────────────────────────────────┘
```

**Options:**
- Leave **empty** = All collaborators can create PRs
- Add **specific users** = Only they can push (advanced)

**Recommendation:** Leave empty for most projects

---

#### 4.2 Force Pushes and Deletions

```
☐ Allow force pushes
  ⚠ Specify who can force push
  
☐ Allow deletions
  ⚠ Allow users with push access to delete matching branches
```

**Recommendation:** Leave **UNCHECKED**
- ❌ No force pushes
- ❌ No deletions

**Why?**
- Prevents history rewriting
- Prevents accidental branch deletion
- Maintains audit trail

---

### Step 5: Rules Applied to Administrators

Scroll down to find:

```
Rules applied to everyone including administrators

☑ Include administrators

  Apply these rules to repository administrators
```

**✅ IMPORTANT: Check this box!**

Why?
- Ensures consistent workflow
- Admins aren't exempt from reviews
- Better code quality overall

---

### Step 6: Save Configuration

1. Review all settings one final time
2. Click **"Create"** button at the bottom
3. You should see success message:

```
┌─────────────────────────────────────────────────────┐
│ ✓ Branch protection rule created                    │
│                                                     │
│ main                                                │
│ • Require pull request reviews before merging      │
│ • Require status checks to pass before merging     │
│ • Require conversation resolution before merging   │
└─────────────────────────────────────────────────────┘
```

---

## 📖 Configuration Options Explained

### Complete Settings Breakdown

#### 1. **Require Pull Request Before Merging**

**Purpose:** Prevent direct commits to protected branch

**Sub-options:**

| Option | Description | Recommended |
|--------|-------------|-------------|
| **Require approvals** | Number of reviewers needed | ✅ 1 for small teams, 2+ for large |
| **Dismiss stale approvals** | Re-review after new commits | ✅ Yes |
| **Require Code Owners review** | Specific teams must review | ⚪ Optional |
| **Restrict dismissals** | Who can dismiss reviews | ⬜ No (unless sensitive) |
| **Allow bypass** | Some users skip PR | ❌ No |

**Impact:**
```bash
# Without this setting
git push origin main  # ✅ Works

# With this setting
git push origin main  # ❌ Error: protected branch
# Must create PR instead
```

---

#### 2. **Require Status Checks to Pass**

**Purpose:** Ensure automated tests pass before merge

**Sub-options:**

| Option | Description | Recommended |
|--------|-------------|-------------|
| **Require branch up to date** | Rebase before merge | ✅ Yes |
| **Status checks** | Which checks must pass | ✅ All CI/CD jobs |

**How it works:**

```
PR Timeline:
├─ Created PR
├─ CI/CD starts running
├─ Build & Test: ✓ Passed
├─ Code Quality: ✓ Passed
└─ Merge button: ENABLED ✅

If test fails:
├─ Created PR
├─ CI/CD starts running
├─ Build & Test: ✗ Failed
└─ Merge button: DISABLED ❌
```

**Adding Status Checks:**

Status checks come from:
1. GitHub Actions workflows (`.github/workflows/*.yml`)
2. External CI/CD tools (Jenkins, CircleCI, etc.)
3. Third-party apps (Codecov, SonarCloud, etc.)

**Important:** Checks must run at least once before they appear in the list!

---

#### 3. **Require Conversation Resolution**

**Purpose:** Ensure all review comments are addressed

**Example:**

```
PR Conversation:
├─ @reviewer: "Should we add error handling here?"
│   ├─ @author: "Good point, added try-catch"
│   └─ [Resolve conversation] ✓
└─ Merge button: NOW ENABLED
```

**Without this:**
- Comments can be ignored
- Merge happens anyway

**With this:**
- All conversations must be resolved
- Merge blocked until resolution

---

#### 4. **Require Signed Commits**

**Purpose:** Verify commit author identity with GPG signatures

**Setup Required:**
```bash
# Generate GPG key
gpg --full-generate-key

# Add to GitHub
Settings → SSH and GPG keys → New GPG key

# Configure Git
git config --global user.signingkey YOUR_GPG_KEY_ID
git config --global commit.gpgsign true

# Commits now signed
git commit -S -m "feat: add new feature"
```

**Pros:**
- ✅ Verifies author identity
- ✅ Extra security layer
- ✅ Compliance requirement for some orgs

**Cons:**
- ❌ Requires setup for all contributors
- ❌ Can be complex for beginners

**Recommendation:** Optional unless required by compliance

---

#### 5. **Require Linear History**

**Purpose:** Enforce clean, linear commit history

**Without linear history:**
```
* Merge pull request #45
|\
| * Commit D
| * Commit C
* | Commit B
|/
* Commit A
```

**With linear history:**
```
* Commit D (squashed)
* Commit C (squashed)
* Commit B
* Commit A
```

**How to achieve:**
- Use "Squash and merge"
- Use "Rebase and merge"
- Blocked: "Create a merge commit"

**Recommendation:** ✅ Enable for cleaner history

---

#### 6. **Lock Branch**

**Purpose:** Make branch completely read-only

```
☑ Lock branch
  Lock this branch to make it read-only
```

**Use cases:**
- Archiving old branches
- Preventing any changes to historical releases
- Deprecating branches

**Recommendation:** ⬜ Don't lock main (use other protections instead)

---

#### 7. **Do Not Allow Bypassing**

**Purpose:** Enforce rules on everyone

```
☑ Do not allow bypassing the above settings
  Enforces all commit, status, and review requirements
```

**Recommendation:** ✅ **ALWAYS ENABLE**

Why?
- Prevents "emergency" rule breaking
- Ensures consistent workflow
- No shortcuts even for admins

---

#### 8. **Restrict Who Can Push**

**Purpose:** Limit who can push to protected branches

**Use cases:**
- Large organizations
- Public repositories
- High-security projects

**Recommendation:** ⬜ Leave empty for most projects
- Anyone with write access can create PRs
- Only prevents direct pushes, not PRs

---

#### 9. **Allow Force Pushes**

**Purpose:** Allow rewriting history on protected branch

```
☐ Allow force pushes
  ⚠ Specify who can force push
```

**Recommendation:** ❌ **NEVER ENABLE**

**Why force pushes are dangerous:**

```bash
# Force push rewrites history
git push -f origin main

# Problems:
❌ Loses commits from other developers
❌ Breaks cloned repositories
❌ Destroys audit trail
❌ Can't recover lost commits
```

**Exceptions:** None for main branch!

---

#### 10. **Allow Deletions**

**Purpose:** Allow branch deletion even when protected

**Recommendation:** ❌ **NEVER ENABLE for main**

**Why?**
- Accidentally deleting main = disaster
- No recovery without contacting GitHub support
- Breaks entire repository

---

## 🧪 Testing Protection Rules

### Test 1: Direct Push (Should Fail)

```bash
# Try to push directly to main
git checkout main
git add .
git commit -m "test: direct push"
git push origin main
```

**Expected Result:**
```
remote: error: GH006: Protected branch update failed for refs/heads/main.
remote: error: Cannot push to protected branch
To https://github.com/YOUR-USERNAME/student-management-system.git
 ! [remote rejected] main -> main (protected branch hook declined)
error: failed to push some refs
```

**✅ Protection working!**

---

### Test 2: Pull Request Without Tests (Should Block)

```bash
# Create branch and push
git checkout -b test/without-tests
echo "test" > test.txt
git add test.txt
git commit -m "test: add test file"
git push origin test/without-tests
```

**On GitHub:**
1. Create Pull Request
2. Wait for CI/CD to run and fail
3. Try to merge

**Expected Result:**
```
❌ Merging is blocked
Required status check "Build & Test" has not been successful
```

**✅ Protection working!**

---

### Test 3: Pull Request With Tests (Should Pass)

```bash
# Create branch and push working code
git checkout -b feature/working-tests
# ... make valid changes with tests ...
git add .
git commit -m "test: add valid tests"
git push origin feature/working-tests
```

**On GitHub:**
1. Create Pull Request
2. Wait for CI/CD to pass
3. Request review
4. Get approval
5. Merge button enabled ✅

**Expected Result:**
```
✓ All checks have passed
✓ 1 approval

[Squash and merge] ← Button enabled
```

**✅ Protection working correctly!**

---

### Test 4: Merge Without Approval (Should Block)

**On GitHub:**
1. Create PR with passing tests
2. Try to merge without approval

**Expected Result:**
```
❌ Merging is blocked
Review required
At least 1 approving review is required
```

**✅ Protection working!**

---

## 🎭 Common Scenarios

### Scenario 1: Emergency Hotfix

**Problem:** Production is down, need to deploy NOW!

**❌ Wrong Approach:**
```bash
# Disable branch protection
# Push directly to main
# Deploy
# Re-enable protection
```

**✅ Correct Approach:**
```bash
# 1. Create hotfix branch
git checkout -b hotfix/critical-bug
git add .
git commit -m "fix: critical production bug"
git push origin hotfix/critical-bug

# 2. Create PR (mark as urgent)
# 3. Quick review (5-10 minutes)
# 4. Ensure tests pass
# 5. Get approval
# 6. Merge

# Total time: 15-20 minutes (worth it for safety!)
```

---

### Scenario 2: Auto-merge Dependabot PRs

**Problem:** Dependabot creates many PRs, manual approval tedious

**Solution: Use auto-merge**

```yaml
# .github/workflows/auto-merge-dependabot.yml
name: Auto-merge Dependabot PRs

on:
  pull_request:
    types: [opened, synchronize]

jobs:
  auto-merge:
    runs-on: ubuntu-latest
    if: github.actor == 'dependabot[bot]'
    steps:
      - name: Auto-approve
        run: gh pr review --approve "$PR_URL"
        env:
          PR_URL: ${{github.event.pull_request.html_url}}
          GITHUB_TOKEN: ${{secrets.GITHUB_TOKEN}}
      
      - name: Auto-merge
        run: gh pr merge --auto --squash "$PR_URL"
        env:
          PR_URL: ${{github.event.pull_request.html_url}}
          GITHUB_TOKEN: ${{secrets.GITHUB_TOKEN}}
```

---

### Scenario 3: Multiple Reviewers Required

**For sensitive projects:**

```
Require a pull request before merging
  ☑ Require approvals
    Required number of approvals: [2▼]
  
  ☑ Require review from Code Owners
```

**Setup CODEOWNERS file:**

```
# .github/CODEOWNERS

# Backend code requires backend team
/src/main/java/          @backend-team

# Frontend requires frontend team
/src/main/resources/templates/ @frontend-team

# Config requires DevOps
*.yml                    @devops-team
```

---

## 🔧 Troubleshooting

### Problem 1: "Status check not found"

**Symptoms:**
- Can't add status check in protection settings
- Check doesn't appear in dropdown

**Solutions:**

**A. Run workflow first:**
```bash
# Create a PR and let GitHub Actions run
git checkout -b test/trigger-workflow
git push origin test/trigger-workflow
# Create PR on GitHub
# Wait for workflow to complete
# Then add status check
```

**B. Check workflow file:**
```yaml
# Ensure job has a name
jobs:
  build-and-test:  # ← This becomes the status check name
    name: Build & Test  # ← This is what appears in dropdown
```

---

### Problem 2: "Can't merge - branch out of date"

**Symptoms:**
```
❌ This branch is out of date with the base branch
   Update branch
```

**Solution:**

```bash
# Update your branch with latest main
git checkout your-feature-branch
git fetch origin
git merge origin/main

# Or use rebase
git rebase origin/main

# Push updated branch
git push origin your-feature-branch
```

**Or use GitHub button:**
- Click "Update branch" on PR page

---

### Problem 3: "Admin can't merge own PR"

**Symptoms:**
- Created PR as admin
- Tests pass
- Can't merge (need approval)

**Why:** "Include administrators" is checked (correct!)

**Solution:**

**Option A: Get another admin to review**
```
Request review from another team member with admin access
```

**Option B: Use organization teams**
```
Create team on GitHub
Add admins to team
Rotate who reviews each other's PRs
```

**Option C: Temporarily bypass (NOT RECOMMENDED)**
```
Settings → Branches → Edit rule
☐ Include administrators  ← Uncheck temporarily
Merge PR
Re-enable protection
```

---

### Problem 4: "Protection too strict - blocking productivity"

**Symptoms:**
- Taking too long to merge
- Team complaining
- Small changes blocked

**Solutions:**

**A. Adjust required approvals:**
```
Change from 2 approvals → 1 approval
For small teams, 1 is often enough
```

**B. Create exception branches:**
```
Protected branches:
- main (strict protection)
- develop (relaxed protection)
- feature/* (no protection)
```

**C. Use auto-merge for small changes:**
```yaml
# Auto-approve for docs/tests only
if: contains(github.event.pull_request.files, '.md')
```

---

## ✅ Best Practices

### Enterprise Configuration

**For production projects:**

```
Branch: main

☑ Require pull request
  - Required approvals: 2
  - Dismiss stale reviews: Yes

☑ Require status checks
  - Build & Test (required)
  - Code Quality (required)
  - Security Scan (required)
  - Branches up to date: Yes

☑ Require conversation resolution

☑ Require signed commits

☑ Require linear history

☑ Do not allow bypassing

☑ Include administrators

☐ Allow force pushes: NO
☐ Allow deletions: NO
```

---

### Small Team Configuration

**For learning/startup projects:**

```
Branch: main

☑ Require pull request
  - Required approvals: 1
  - Dismiss stale reviews: Yes

☑ Require status checks
  - Build & Test (required)
  - Branches up to date: Yes

☑ Require conversation resolution

☑ Do not allow bypassing

☑ Include administrators

☐ Allow force pushes: NO
☐ Allow deletions: NO
```

---

### Development Branch Configuration

**For develop/staging branches:**

```
Branch: develop

☑ Require pull request
  - Required approvals: 1

☑ Require status checks
  - Build & Test (required)

☑ Include administrators

☐ Allow force pushes: NO
☐ Allow deletions: NO

Note: More relaxed than main, but still protected
```

---

## 📊 Visual Guide Summary

### Complete Protection Setup Flowchart

```
GitHub Repository Settings
│
├─ Navigate to Branches
│  │
│  ├─ Add Branch Protection Rule
│  │  │
│  │  ├─ Branch pattern: "main"
│  │  │
│  │  ├─ ☑ Require pull request
│  │  │  ├─ Approvals: 1
│  │  │  └─ Dismiss stale: Yes
│  │  │
│  │  ├─ ☑ Require status checks
│  │  │  ├─ Build & Test
│  │  │  └─ Code Quality
│  │  │
│  │  ├─ ☑ Conversation resolution
│  │  │
│  │  ├─ ☑ Do not allow bypassing
│  │  │
│  │  ├─ ☑ Include administrators
│  │  │
│  │  ├─ ☐ Allow force pushes: NO
│  │  │
│  │  └─ ☐ Allow deletions: NO
│  │
│  └─ [Create] Protection Rule
│
└─ Protection Active ✅
```

---

## 🎓 Quick Start Checklist

```markdown
□ Navigate to Settings → Branches

□ Click "Add branch protection rule"

□ Set branch pattern: main

□ ☑ Require pull request before merging
  □ Required approvals: 1
  □ Dismiss stale reviews

□ ☑ Require status checks to pass
  □ Add: Build & Test
  □ Add: Code Quality
  □ Require branches up to date

□ ☑ Require conversation resolution

□ ☑ Do not allow bypassing

□ ☑ Include administrators

□ ☐ Allow force pushes (leave UNCHECKED)

□ ☐ Allow deletions (leave UNCHECKED)

□ Click "Create" to save

□ Test with a Pull Request
```

---

**Congratulations! Your main branch is now enterprise-grade protected! 🛡️**

---

**Last Updated:** 2026-02-21  
**Version:** 1.0.0

